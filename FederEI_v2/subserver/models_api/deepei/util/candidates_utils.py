import pathlib
import numpy as np
import pandas as pd
from tqdm import tqdm

from resources.global_var import general_var,deepei_var

import torch


def ms2vec(peakindex, peakintensity, maxmz=2000):
    output = np.zeros(maxmz)
    for i, j in enumerate(peakindex):
        if round(j) >= maxmz:
            continue
        output[int(round(j))] = float(peakintensity[i])
    output = output / (max(output) + 10**-6)
    return output


def predict_fingerprint(specs, model_list):
    pred_fp = np.zeros((specs.shape[0], len(model_list)))
    
    for i, m in enumerate(tqdm(model_list)):
        pred = m.predict(torch.tensor(specs,dtype=torch.float32))
        pred_fp[:, i] = pred
    return pred_fp

def tanimoto(s, t):
    return (np.sum(s * t, axis=-1)) / (
        np.sum(s**2, axis=-1) + np.sum(t**2) - np.sum(s * t, axis=-1)
    )


def get_result_from_vectors(vectors=None):

    # print("==========deepei:get all fingerprints of unknown_spectra==========")
    # unknown_peak_vecs = []
    # for spec in tqdm(specs):
    #     unknown_peak_vecs.append(ms2vec(spec.mz, spec.intensities))
    # unknown_peak_vecs = np.array(unknown_peak_vecs)
    # pred_fps = predict_fingerprint(unknown_peak_vecs, deepei_var["model_list"])

    # search
    print("==========deepei:search==========")
    deepei_var["hnsw_index"].set_ef(300)  # ef should always be > k   ##
    k = 200
    I, D = deepei_var["hnsw_index"].knn_query(vectors, k)

    # The Jaccard similarity between the calculation and the result
    print("==========deepei:calculate jaccard==========")
    jaccard_D = []
    for i in tqdm(range(len(I))):
        fps = np.array(deepei_var["hnsw_index"].get_items(I[i]))
        jaccard_D.append(1 - tanimoto(fps,vectors[i]))
        
    # return result
    print("==========deepei:return result==========")
    ans = []
    for i in tqdm(range(len(I))):
        # oneresult = {}
        candidates = []
        
        for j in range(len(I[i])):
            onecandi = {}
            # onecandi["candi_index"] = int(I[i][j])
            onecandi["candi_index"] = general_var["indexs_list"][int(I[i][j])]
            
            onecandi["distance"] = float(jaccard_D[i][j])
            onecandi["inchikey"] = str(general_var["inchikey_list"][I[i][j]])
            
            # onecandi["smiles"] = str(general_var["smi_list"][I[i][j]])
            # onecandi["mw"] = float(general_var["mw_list"][I[i][j]])
            candidates.append(onecandi)
        # oneresult["number"] = i
        # oneresult["title"] = specs[i].metadata["compound_name"]
        # oneresult["candidates"] = candidates
        # ans.append(oneresult)
        ans.append(candidates)
    print("==========deepei:over==========")
    return ans
