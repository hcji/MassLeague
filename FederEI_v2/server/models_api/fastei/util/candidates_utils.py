import numpy as np
from matchms.importing import load_from_msp
from tools.loader import load_from_mgf
from tqdm import tqdm
from matchms.filtering import normalize_intensities
from scipy.sparse import csr_matrix, save_npz, load_npz

from ..data_process import spec
from resources.global_var import general_var,fastei_var
from spec2vec import SpectrumDocument


def cosine_similarity(s,t):
    # 计算 s 和 t 的点积
    dot_product = np.dot(s, t).reshape(-1)

    # 计算 s 和 t 的范数
    norm_X = np.linalg.norm(s, axis=1)  # 计算每一行的范数
    norm_y = np.linalg.norm(t)          # 计算 y 的范数

    # 返回余弦相似度
    return dot_product / (norm_X * norm_y)

def get_result_from_specs(specs=None):

    print("==========fastei:get all vector of unknown_spectra==========")
    # 数据编码
    word2vectors = []
    for i in tqdm(range(len(specs))):
        spectrum_in = SpectrumDocument(normalize_intensities(specs[i]), n_decimals=0)
        vetors = fastei_var["spectovec"]._calculate_embedding(spectrum_in)

        word2vectors.append(vetors)

    # 搜索
    print("==========fastei:search==========")
    xq = np.array(word2vectors).astype("float32")

    xq_len = np.linalg.norm(xq, axis=1, keepdims=True)
    xq = xq / (xq_len + 1e-10)  # 防止除以0
    fastei_var["hnsw_index"].set_ef(300)  # ef should always be > k   ##
    k = 200
    I, D = fastei_var["hnsw_index"].knn_query(xq, k)

    # 计算与结果之间的cosine相似度
    print("==========fastei:calculate cosine==========")
    cosine_D = []
    for i in tqdm(range(len(I))):
        fps = fastei_var["hnsw_index"].get_items(I[i])
        cosine = (1 - cosine_similarity(fps,xq[i])) / 2
        cosine_D.append(cosine)
    # 返回结果
    print("==========fastei:return result==========")
    ans = []
    for i in tqdm(range(len(I))):
        oneresult = {}
        candidates = []

        for j in range(len(I[i])):
            onecandi = {}
            onecandi["candi_index"] = int(I[i][j])
            onecandi["distance"] = float(cosine_D[i][j])
            onecandi["inchikey"] = str(general_var["inchikey_list"][I[i][j]])
            
            # onecandi["smiles"] = str(general_var["smi_list"][I[i][j]])
            # onecandi["mw"] = float(general_var["mw_list"][I[i][j]])
            candidates.append(onecandi)
        oneresult["number"] = i
        oneresult["title"] = specs[i].metadata.get("compound_name", "Unknown")
        oneresult["candidates"] = candidates
        ans.append(oneresult)
    print("==========fastei:over==========")
    return ans
