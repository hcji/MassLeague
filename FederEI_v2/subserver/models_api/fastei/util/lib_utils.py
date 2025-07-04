import pathlib
import numpy as np
import pandas as pd
from matchms.importing import load_from_msp
from tools.loader import load_from_mgf
from rdkit import Chem
from joblib import Parallel,delayed
from tqdm import tqdm
from .dataset_utils import load_npy,save_npy
import hnswlib
import gensim
from scipy.sparse import csr_matrix, save_npz
from ..data_process.spec_to_wordvector import spec_to_wordvector
from ..data_process import spec
from matchms.filtering import normalize_intensities

# The following method loads the library file
def load_models():
    model_file = str(
        pathlib.Path(__file__).resolve().parent.parent / "model" / "references_word2vec.model"
    )
    # Load pretrained model (here dummy model)
    model = gensim.models.Word2Vec.load(model_file)
    return spec_to_wordvector(model=model, intensity_weighting_power=0.5)


def load_lib(dataset_name:str):
    load_path = pathlib.Path(__file__).resolve().parent.parent / "save_data" /dataset_name
    print("==========fastei:load smi_list==========")
    smi_list = load_npy(str(load_path / "lib_smi.npy"))
    print("==========fastei:load mz_list==========")
    mz_list = load_npy(str(load_path / "lib_mz.npy"))
    print("==========fastei:load intensities_list==========")
    intensities_list = load_npy(str(load_path / "lib_intensities.npy"))
    print("==========fastei:load mw_list==========")
    mw_list = load_npy(str(load_path / "lib_mw.npy"))
    print("==========fastei:load inchikey_list==========")
    inchikey_list = load_npy(str(load_path / "lib_inchikey.npy"))
    print("==========fastei:load spectovec==========")
    spectovec = load_models()
    
    print("==========fastei:load hnsw_index==========")
    # load hnswlib.Index
    dim = 500
    hnsw_index = hnswlib.Index(space="l2", dim=dim)
    hnsw_index.load_index(
        str(load_path / "references_index.bin"),
        max_elements=2166721,
    )
    
    return (
        smi_list,
        mz_list,
        intensities_list,
        mw_list,
        inchikey_list,
        spectovec,
        hnsw_index,
    )


# The following method generates the library file
def get_inchikey(smi):
    try:
        mol = Chem.MolFromSmiles(smi)
        inchikey = Chem.MolToInchiKey(mol)
        return inchikey
    except:
        return None
    
def gen_lib(path:str,dataset_name:str):
    if path.endswith(".msp"):
        spectrum_list = load_from_msp(path)
    elif path.endswith(".mgf"):
        spectrum_list = load_from_mgf(path)
    else:
        assert False, "input file should be .msp or .mgf file"

    spectrum_list = list(spectrum_list)
    
    smi_list = []
    mz_list = []
    intensities_list = []
    mw_list = []
    
    for spectrum in tqdm(spectrum_list):
        try:
            smi = spectrum.metadata['smiles']
            mz = spectrum.mz
            intensities = spectrum.intensities
            mw = spectrum.metadata['exactmolwt']
            
            smi_list.append(smi)
            mz_list.append(mz)
            intensities_list.append(intensities)
            mw_list.append(mw)
        except:
            print('gen lib except')
    
    inchikey_list = Parallel(n_jobs=-1)(delayed(get_inchikey)(s) for s in tqdm(smi_list))
    
    save_path = pathlib.Path(__file__).resolve().parent.parent / "save_data" / dataset_name
    # Check if the folder exists. If it doesn't exist, create it.
    if not save_path.exists():
        save_path.mkdir(parents=True)
        save_npy(str(save_path / "lib_smi.npy"),np.array(smi_list))
        save_npy(str(save_path / "lib_mz.npy"),np.array(mz_list))
        save_npy(str(save_path / "lib_intensities.npy"),np.array(intensities_list))
        save_npy(str(save_path / "lib_mw.npy"),np.array(mw_list))
        save_npy(str(save_path / "lib_inchikey.npy"),np.array(inchikey_list))
        
        # gen references_index.bin
        spectovec = load_models()
        word2vectors = []
        for i in tqdm(range(len(spectrum_list))):
            spectrum_in = spec.SpectrumDocument(normalize_intensities(spectrum_list[i]), n_decimals=0)
            try:
                vetors = spectovec._calculate_embedding(spectrum_in)
    
                word2vectors.append(vetors)
            except:
                pass
        word_vec = csr_matrix(np.array(word2vectors))

        xb = word_vec.todense().astype("float32")
        xb_len = np.linalg.norm(xb, axis=1, keepdims=True)
        xb = xb / xb_len
        dim = 500
        num_elements = len(xb)
        ids = np.arange(num_elements)
        ## Declaring index
        p = hnswlib.Index(space="l2", dim=dim)  # possible options are l2, cosine or ip
        ## Initializing index - the maximum number of elements should be known beforehand
        p.init_index(max_elements=num_elements, ef_construction=800, M=64)
        ## Element insertion
        p.add_items(xb, ids)
        ## Controlling the recall by setting ef:
        p.set_ef(300)  # ef should always be > k   ##
        p.save_index(str(save_path / "references_index.bin"))
    else:
        raise 'Database with the same name！'
    