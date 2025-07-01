import pathlib
from .dataset_utils import load_npy, save_npy
import hnswlib
import gensim
import pandas as pd
import numpy as np
import pickle
from tqdm import tqdm
import os
# from tensorflow.keras.models import model_from_json
from spec2vec import Spec2Vec
from rdkit import Chem
from matchms.importing import load_from_msp
from tools.loader import load_from_mgf
from joblib import Parallel, delayed
from spec2vec import SpectrumDocument
import sys
sys.path.append(str(pathlib.Path(__file__).resolve().parent / "model/deepei"))
from PyFingerprint.fingerprint import get_fingerprint
from .model.deepei.training_pytorch import MLP,ModelManager

import itertools
from matchms.filtering import normalize_intensities
import torch
import gc

# 以下方法加载库文件
def load_general_lib(dataset_name: str):
    load_path = pathlib.Path(__file__).resolve().parent / "database" / dataset_name / "general"
    print("==========general:load smi_list==========")
    smi_list = load_npy(str(load_path / "lib_smi_newly_added_data.npy"))
    print("==========general:load mz_list==========")
    mz_list = load_npy(str(load_path / "lib_mz.npy"))
    print("==========general:load intensities_list==========")
    intensities_list = load_npy(str(load_path / "lib_intensities.npy"))
    print("==========general:load mw_list==========")
    mw_list = load_npy(str(load_path / "lib_mw.npy"))
    print("==========general:load inchikey_list==========")
    inchikey_list = load_npy(str(load_path / "lib_inchikey_newly_added_data.npy"))

    return (
        smi_list,
        mz_list,
        intensities_list,
        mw_list,
        inchikey_list,
    )

# # 测试FL
# def load_deepei_lib(dataset_name: str):
    
#     num_fl = 6
    
#     load_path = (
#         pathlib.Path(__file__).resolve().parent / "database" / dataset_name / "deepei"
#     )
    
#     mlp = pd.read_csv(f"/data2/changsihao/project_pms/deepei_train/Fingerprint/results/torch_FL_fold_with_1_10_parm_20_5/{num_fl}.txt",
#         sep="\t",
#         header=None,
#     )
#     mlp.columns = ["id", "accuracy", "precision", "recall", "f1"]
#     fpkeep = mlp["id"][mlp["f1"] > 0.5]
    
    
#     print("==========deepei:load model_list==========")
#     models_path = f"/data2/changsihao/project_pms/deepei_train/Fingerprint/mlp_models/torch_FL_fold_with_1_10_parm_20_5/{num_fl}"

#     files = os.listdir(str(models_path))
#     rfp = np.array([int(f.split(".")[0]) for f in files if ".pth" in f])
#     rfp = np.sort(rfp)

#     rfp = set(rfp).intersection(set(fpkeep))
#     rfp = np.sort(list(rfp)).astype(int)

#     files = [str(f) + ".pth" for f in rfp]

#     device = torch.device("cuda:6")
#     model_list = []
#     for f in tqdm(files):
#         model = MLP(2000)
#         mm = ModelManager(model,device=device)
#         mm.load_state_dict(str(models_path) + "/" + f)
#         model_list.append(mm)

#     print("==========deepei:load hnswlib_index==========")
#     # load hnswlib.Index
#     dim = len(model_list)
#     # num_elements = 2343378
#     hnsw_index = hnswlib.Index(space="l2", dim=dim)
#     hnsw_index.load_index(
#         str(load_path / f"references_index_fl_fold_{num_fl}_parm_20_5.bin"),
#         # max_elements=num_elements,
#     )
    
#     return (
#         None,
#         model_list,
#         fpkeep,
#         hnsw_index,
#     )
    
def load_deepei_lib(dataset_name: str):
    load_path = (
        pathlib.Path(__file__).resolve().parent / "database" / dataset_name / "deepei"
    )
    
    mlp = pd.read_csv(
        str(
            pathlib.Path(__file__).resolve().parent
            / "model"
            / "deepei"
            / "Fingerprint"
            / "results"
            / "mlp_result_torch.txt"
        ),
        sep="\t",
        header=None,
    )
    mlp.columns = ["id", "accuracy", "precision", "recall", "f1"]
    fpkeep = mlp["id"][mlp["f1"] > 0.5]
    
    print("==========deepei:load fp_list==========")
    fp_list = load_npy(str(load_path / "lib_fp.npy"))
    
    print("==========deepei:load model_list==========")
    models_path = (
        pathlib.Path(__file__).resolve().parent / "model" / "deepei" / "Fingerprint" / "mlp_models_torch"
    )
    files = os.listdir(str(models_path))
    rfp = np.array([int(f.split(".")[0]) for f in files if ".pth" in f])
    rfp = np.sort(rfp)

    rfp = set(rfp).intersection(set(fpkeep))
    rfp = np.sort(list(rfp)).astype(int)

    files = [str(f) + ".pth" for f in rfp]

    device = torch.device("cuda:6")
    model_list = []
    for f in tqdm(files):
        model = MLP(2000)
        mm = ModelManager(model,device=device)
        mm.load_state_dict(str(models_path) + "/" + f)
        model_list.append(mm)

    print("==========deepei:load hnswlib_index==========")
    # load hnswlib.Index
    dim = len(model_list)
    # num_elements = 2343378
    hnsw_index = hnswlib.Index(space="l2", dim=dim)
    hnsw_index.load_index(
        str(load_path / "references_index_newly_added_data.bin"),
        # max_elements=num_elements,
    )
    
    return (
        fp_list,
        model_list,
        fpkeep,
        hnsw_index,
    )


def load_fastei_lib(dataset_name: str):
    load_path = (
        pathlib.Path(__file__).resolve().parent / "database" / dataset_name / "fastei"
    )
    print("==========fastei:load spectovec==========")
    spec2vec_path = str(
        pathlib.Path(__file__).resolve().parent / "model" / "fastei" / "references_word2vec.model"
    )
    model = gensim.models.Word2Vec.load(spec2vec_path)
    spectovec = Spec2Vec(model=model, intensity_weighting_power=0.5,allowed_missing_percentage=50.0)

    print("==========fastei:load hnsw_index==========")
    # load hnswlib.Index
    dim = 500
    # num_elements = 2343378
    hnsw_index = hnswlib.Index(space="l2", dim=dim)
    hnsw_index.load_index(
        str(load_path / "references_index_newly_added_data.bin"),
        # max_elements=num_elements,
    )
    print("==========fastei:load lib_vector_list==========")
    vector_list = load_npy(str(load_path / "lib_word_vector.npy"))
    return (
        spectovec,
        hnsw_index,
        vector_list
    )


# 以下方法生成库文件
def get_inchikey(smi):
    try:
        mol = Chem.MolFromSmiles(smi)
        inchikey = Chem.MolToInchiKey(mol)
        return inchikey
    except:
        return None


def gen_general_lib(spectrum_list: list, dataset_name: str):
    
    save_path = pathlib.Path(__file__).resolve().parent / "database" / dataset_name / "general"

    # 检查文件夹是否存在，如果不存在则创建它
    if not save_path.exists():
        save_path.mkdir(parents=True)
    else:
        raise "数据库同名！"

    smi_list = []
    mz_list = []
    intensities_list = []
    mw_list = []
    for spec in tqdm(spectrum_list):
        try:
            smi = spec.metadata["smiles"]
            mz = spec.mz
            intensities = spec.intensities
            mw = spec.metadata["exactmolwt"]

            smi_list.append(smi)
            mz_list.append(mz)
            intensities_list.append(intensities)
            mw_list.append(mw)
        except Exception as e:
            print(f"gen lib except:{e}")

    inchikey_list = Parallel(n_jobs=-1)(
        delayed(get_inchikey)(s) for s in tqdm(smi_list)
    )
    print(np.array(inchikey_list).shape)


    save_npy(str(save_path / "lib_smi.npy"), np.array(smi_list))
    save_npy(str(save_path / "lib_mz.npy"), np.array(mz_list))
    save_npy(str(save_path / "lib_intensities.npy"), np.array(intensities_list))
    save_npy(str(save_path / "lib_mw.npy"), np.array(mw_list))
    save_npy(str(save_path / "lib_inchikey.npy"), np.array(inchikey_list))
    


def get_fp(smi):
    try:
        types = ["standard", "pubchem", "klekota-roth", "maccs", "estate", "circular"]
        fp = [get_fingerprint(smi, t).to_numpy() for t in types]
        fp = list(itertools.chain(*fp))

        return fp
    except:
        return None


def gen_deepei_lib(spectrum_list: list, dataset_name: str):
    save_path = (
        pathlib.Path(__file__).resolve().parent / "database" / dataset_name / "deepei"
    )
    
    # 检查文件夹是否存在，如果不存在则创建它
    if not save_path.exists():
        save_path.mkdir(parents=True)
    else:
        raise "数据库同名！"

    with open(save_path.parent / "general" / "lib_smi.npy","rb") as f:
        smi_list = np.load(f,allow_pickle=True)
    
    print(f"len(smi_list):{len(smi_list)}")
    
    # 计算fpkeep
    mlp = pd.read_csv(
        str(
            pathlib.Path(__file__).resolve().parent
            / "model"
            / "deepei"
            / "Fingerprint"
            / "results"
            / "mlp_result_torch.txt"
        ),
        sep="\t",
        header=None,
    )
    mlp.columns = ["id", "accuracy", "precision", "recall", "f1"]
    fpkeep = mlp["id"][np.where(mlp["f1"] > 0.5)[0]]
        
    # 计算fp，并保存
    smi_step = 50000
    for i,index in enumerate(tqdm(range(0,len(smi_list),smi_step))):
        if (save_path / f"lib_fp_{i}.npy").exists():
            continue
        
        end_index = min(index+smi_step,len(smi_list))
        delayeds = [delayed(get_fp)(s) for s in smi_list[index:end_index]]
        fp_list = Parallel(n_jobs=32,batch_size=100)(delayeds)

        print(f"len(fp_list):{len(fp_list)}")
        fp_list = np.array(fp_list)[:,np.sort(list(fpkeep)).astype(int)]
        save_npy(str(save_path / f"lib_fp_{i}.npy"), fp_list)
        del fp_list
        gc.collect()
        
    files = list(save_path.glob("lib_fp_*"))
    sorted_files = sorted(files, key=lambda f: int(f.stem.split("_")[2]))
    merged_data = []
    for file_path in sorted_files:
        data = load_npy(file_path)
        merged_data.append(data)
        del data
        gc.collect()
        
    merged_data = np.concatenate(merged_data,axis=0)
    print(f"merged_data.shape:{merged_data.shape}")
    save_npy(str(save_path / "lib_fp.npy"), merged_data)

    # 删除中间文件 
    for file_path in sorted_files:
        file_path.unlink()
    
    
    
    # gen references_index.bin
    dim = len(merged_data[0])
    num_elements = len(merged_data)
    ids = np.arange(num_elements)

    ## Declaring index
    p = hnswlib.Index(space="l2", dim=dim)  # possible options are l2, cosine or ip
    ## Initializing index - the maximum number of elements should be known beforehand
    p.init_index(max_elements=num_elements, ef_construction=800, M=64)

    ## Element insertion
    p.add_items(np.array(merged_data), ids)
    ## Controlling the recall by setting ef:
    p.set_ef(300)  # ef should always be > k   ##

    p.save_index(str(save_path / "references_index.bin"))
    



def gen_fastei_lib(spectrum_list: list, dataset_name: str):
    save_path = (
        pathlib.Path(__file__).resolve().parent / "database" / dataset_name / "fastei"
    )
    spec2vec_path = (
        pathlib.Path(__file__).resolve().parent / "model" / "fastei" / "references_word2vec.model"
    )
    
    # 检查文件夹是否存在，如果不存在则创建它
    if not save_path.exists():
        save_path.mkdir(parents=True)
    else:
        raise "数据库同名！"

    model = gensim.models.Word2Vec.load(str(spec2vec_path))

    spectovec = Spec2Vec(model=model, intensity_weighting_power=0.5)
    word2vectors = []
    for i in tqdm(range(len(spectrum_list))):
        spectrum_in = SpectrumDocument(normalize_intensities(spectrum_list[i]), n_decimals=0)
        try:
            vetors = spectovec._calculate_embedding(spectrum_in)

            word2vectors.append(vetors)
        except Exception as e:
            print(f"{e}")
    
    xb = np.array(word2vectors).astype("float32")

    xb_len = np.linalg.norm(xb, axis=1, keepdims=True)
    xb = xb / xb_len
    
    save_npy(str(save_path / "lib_word_vector.npy"), np.array(xb))
    
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
    save_path.mkdir(parents=True)
    p.save_index(str(save_path / "references_index.bin"))
    

