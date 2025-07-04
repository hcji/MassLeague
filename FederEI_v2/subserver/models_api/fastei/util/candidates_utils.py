import numpy as np
from tqdm import tqdm
from resources.global_var import general_var,fastei_var
# from spec2vec import SpectrumDocument


def cosine_similarity(s,t):
    # Calculate the dot product of s and t
    dot_product = np.dot(s, t).reshape(-1)

    # Calculate the norms of s and t
    norm_X = np.linalg.norm(s, axis=1)  
    norm_y = np.linalg.norm(t)

    # Return Cosine Similarity
    return dot_product / (norm_X * norm_y)

def get_result_from_vectors(vectors=None):

    # print("==========fastei:get all vector of unknown_spectra==========")
    # # Data coding
    # word2vectors = []
    # for i in tqdm(range(len(specs))):
    #     spectrum_in = SpectrumDocument(normalize_intensities(specs[i]), n_decimals=0)
    #     vetors = fastei_var["spectovec"]._calculate_embedding(spectrum_in)

    #     word2vectors.append(vetors)

    # Search
    print("==========fastei:search==========")
    xq = np.array(vectors).astype("float32")

    xq_len = np.linalg.norm(xq, axis=1, keepdims=True)
    xq = xq / xq_len
    fastei_var["hnsw_index"].set_ef(300)  # ef should always be > k   ##
    k = 200
    I, D = fastei_var["hnsw_index"].knn_query(xq, k)

    # The cosine similarity between the calculation results
    print("==========fastei:calculate cosine==========")
    cosine_D = []
    for i in tqdm(range(len(I))):
        fps = fastei_var["hnsw_index"].get_items(I[i])
        cosine = (1 - cosine_similarity(fps,xq[i])) / 2
        cosine_D.append(cosine)
    # return result
    print("==========fastei:return result==========")
    ans = []
    for i in tqdm(range(len(I))):
        # oneresult = {}
        candidates = []

        for j in range(len(I[i])):
            onecandi = {}
            # onecandi["candi_index"] = int(I[i][j])
            onecandi["candi_index"] = general_var["indexs_list"][int(I[i][j])]
            
            onecandi["distance"] = float(cosine_D[i][j])
            onecandi["inchikey"] = str(general_var["inchikey_list"][I[i][j]])
            
            # onecandi["smiles"] = str(general_var["smi_list"][I[i][j]])
            # onecandi["mw"] = float(general_var["mw_list"][I[i][j]])
            candidates.append(onecandi)
        # oneresult["number"] = i
        # oneresult["title"] = specs[i].metadata["compound_name"]
        # oneresult["candidates"] = candidates
        # ans.append(oneresult)
        ans.append(candidates)
    print("==========fastei:over==========")
    return ans
