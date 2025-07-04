# -*- coding: utf-8 -*-
"""
Created on Thu Mar 21 13:42:15 2024

@author: DELL
"""


import os
import itertools
import numpy as np
import pandas as pd
from sklearn.metrics import jaccard_score
from tensorflow.keras.models import model_from_json, load_model
from matchms.importing import load_from_mgf, load_from_msp
import pathlib
import pickle
from tqdm import tqdm
from joblib import Parallel,delayed

from PyFingerprint.fingerprint import get_fingerprint

mlp = pd.read_csv(
    str(pathlib.Path(__file__).resolve().parent / "Fingerprint" / "results" / "mlp_result_new.txt"),
    sep="\t",
    header=None,
)
mlp.columns = ["id", "accuracy", "precision", "recall", "f1"]
fpkeep = mlp["id"][np.where(mlp["f1"] > 0.5)[0]]  # only keep the model with F1>0.5


def ms2vec(peakindex, peakintensity, maxmz=2000):
    output = np.zeros(maxmz)
    for i, j in enumerate(peakindex):
        if round(j) >= maxmz:
            continue
        output[int(round(j))] = float(peakintensity[i])
    output = output / (max(output) + 10**-6)
    return output


def predict_fingerprint(spec, fpkeep):
    
    files = os.listdir(str(pathlib.Path(__file__).resolve().parent / "Fingerprint" / "mlp_models"))
    rfp = np.array([int(f.split(".")[0]) for f in files if ".h5" in f])
    rfp = np.sort(rfp)

    rfp = set(rfp).intersection(set(fpkeep))
    rfp = np.sort(list(rfp)).astype(int)

    files = [str(f) + ".h5" for f in rfp]
    
    modjs = open(str(pathlib.Path(__file__).resolve().parent / "Fingerprint" / "mlp_models" / "model.json"), "r").read()
    model = model_from_json(modjs)
    pred_fp = np.zeros((spec.shape[0], len(files)))
    for i, f in enumerate(tqdm(files)):
        
        model.load_weights(str(pathlib.Path(__file__).resolve().parent / "Fingerprint" / "mlp_models") + "/" + f)
        pred = np.round(model.predict(spec))[:, 0]
        pred_fp[:, i] = pred
    return pred_fp

def get_all_fingerprints(smi,fpkeep):
    types = ["standard", "pubchem", "klekota-roth", "maccs", "estate", "circular"]
    fp = [get_fingerprint(smi, t).to_numpy() for t in types]
    fp = list(itertools.chain(*fp))
    
    fpkeep = set(fpkeep)
    fpkeep = np.sort(list(fpkeep)).astype(int)
    
    return [fp[i] for i in fpkeep]

def get_fp_score(fp, all_fps):
    scores = np.zeros(all_fps.shape[0])
    for i in range(all_fps.shape[0]):
        fpi = all_fps[i, :]
        fpi = fpi.transpose()
        scores[i] = jaccard_score(fp, fpi)
    return scores

def get_fp_from_fpkeep(fps,fpkeep):
        
    fpkeep = set(fpkeep)
    fpkeep = np.sort(list(fpkeep)).astype(int)
    
    return [fps[i] for i in fpkeep]



def get_result_from_file(path: str):
    if path.endswith(".msp"):
        spectrum_list = load_from_msp(path)
    elif path.endswith(".mgf"):
        spectrum_list = load_from_mgf(path)
    else:
        assert False, "input file should be .msp or .mgf file"
    spectrum_list = list(spectrum_list)

    print("==========get all fingerprints of unknown_spectra==========")
    unknown_spectra = [
        [spectrum.mz, spectrum.intensities] for spectrum in spectrum_list
    ]
    
    unknown_peak_vecs = []
    for spec in tqdm(unknown_spectra):
        unknown_peak_vecs.append(ms2vec(spec[0], spec[1]))
    unknown_peak_vecs = np.array(unknown_peak_vecs)
    # unknown_peak_vecs = np.array([ms2vec(spec[0], spec[1]) for spec in unknown_spectra])
    pred_fps = predict_fingerprint(unknown_peak_vecs, fpkeep)

    print("==========load database==========")
    with open(
        str(
            pathlib.Path(__file__).resolve().parent
            / "save_data"
            / "IN_SILICO_LIBRARY_Spectrum_List.pkl"
        ),
        "rb",
    ) as file:
        database_spectrum_list = pickle.load(file)
        
    print("==========get all fingerprints of database==========")
    if (pathlib.Path(__file__).resolve().parent / "save_data" / "IN_SILICO_LIBRARY_fingerprints.pkl").exists():
        print("==========fp exists==========")
        with open(pathlib.Path(__file__).resolve().parent / "save_data" / "IN_SILICO_LIBRARY_fingerprints.pkl","rb") as f:
            candidate_fps = pickle.load(f)
    else:
        candidate_smiles = [spec.metadata["smiles"] for spec in database_spectrum_list]
        candidate_fps = Parallel(n_jobs=-1)(delayed(get_all_fingerprints)(s,fpkeep) for s in tqdm(candidate_smiles))
        candidate_fps = np.array(candidate_fps)
        print(f"candidate_fps_shape:{candidate_fps.shape}")
        with open(pathlib.Path(__file__).resolve().parent / "save_data" / "IN_SILICO_LIBRARY_fingerprints.pkl","wb") as f:
            pickle.dump(candidate_fps, f)
        # candidate_fps = []
        # for s in tqdm(candidate_smiles):
        #     candidate_fps.append(get_all_fingerprints(s,fpkeep))
        # candidate_fps = np.array(candidate_fps)
        # with open(pathlib.Path(__file__).resolve().parent / "save_data" / "lib_deepei_fp.pkl.pkl","wb") as f:
        #     pickle.dump(candidate_fps, f)
            
    print("==========get results==========")
    results = []
    for idx, fp in enumerate(tqdm(pred_fps)):
        scores = get_fp_score(fp, candidate_fps)
        # By default, from smallest to largest, use slicing with inversion, and select the first 100 items.
        sorted_index = np.argsort(scores)[::-1][:100]
        scores = scores[sorted_index]
        re_spectra = [database_spectrum_list[i] for i in sorted_index]
        
        re_smi = [spec.metadata["smiles"] for spec in re_spectra]
        re_mw = [spec.metadata["exactmolwt"] for spec in re_spectra]
        re_intensities = [spec.intensities for spec in re_spectra]
        re_mz = [spec.mz for spec in re_spectra]
        re_inchikey = [spec.inchikey for spec in re_spectra]
        
        results.append(
            {
                "number": idx,
                # "title": spectrum.metadata["compound_name"],
                "result": {
                    "candidates": [
                        {
                            "smiles": smi,
                            "score": s,
                            "mw": mw,
                            "intensities": intensities,
                            "mz": mz,
                            "inchikey":inchikey
                            # "title": title,
                        }
                        for smi, s, mw, intensities, mz, inchikey in zip(
                            re_smi, list(scores), re_mw, re_intensities, re_mz,re_inchikey
                        )
                    ],
                },
            }
        )

    return results

if __name__ == "__main__":
    pass
    # unknown_spectra = [
    #     [[55, 70, 145, 255], [23, 999, 344, 77]],
    #     [[58, 75, 233, 259], [23, 566, 304, 999]],
    #     [[15, 88, 170, 335], [15, 99, 999, 664]],
    # ]  # not real spectra
    # unknown_peak_vecs = np.array([ms2vec(s[0], s[1]) for s in unknown_spectra])
    # pred_fps = predict_fingerprint(unknown_peak_vecs, fpkeep)

    # candidate_smiles = [
    #     "CCOP(C)(=O)OP(C)(=S)OCC",
    #     "C[Si](C)(C)NC(=O)N1c2ccccc2CC(O[Si](C)(C)C)c2ccccc21",
    #     "O=C(C(Br)C(Br)c1ccccc1)C(Br)C(Br)c1ccccc1",
    # ]
    # candidate_fps = np.array([get_all_fingerprints(s,fpkeep) for s in candidate_smiles])
    # # candidate_fps = candidate_fps[
    # #     :, fpkeep
    # # ]  # only keep the fingerprints with the prediction model

    # pred_fp = pred_fps[0]  # choose the first unknown compound
    # scores = get_fp_score(pred_fp, candidate_fps)
    # sorted_index = np.argsort(scores)[::-1][:100]
    # print(f"scores: {scores}")
    # print(f"sorted_index: {sorted_index}")
    # print(f"socores: {scores[sorted_index]}")
    
    # get_result_from_file(str(pathlib.Path(__file__).resolve().parent.parent.parent / "cache/ms_file" / "1-10.msp"))
