import pathlib
from . import fastei
from . import deepei
from tqdm import tqdm
from collections import defaultdict
import heapq
import numpy as np
from resources.global_var import general_var


def get_candidate_message(vectors) -> list:
    print("==========fastei start==========")
    results_fastei = get_FastEI(vectors[:,:500])
    print("==========deepei start==========")
    results_deepei = get_deepei(vectors[:,500:])
    
    weight_rate1 = 0.5  # fastei weight
    weight_rate2 = 1 - weight_rate1  # deepei weight
    
    final_candis_list = [] # Store all the results temporarily
    for i in tqdm(range(len(results_fastei))):

        res_fast = results_fastei[i]
        res_deep = results_deepei[i]
        # Comparison algorithm
        fast_dict = defaultdict(list)
        for candi in res_fast:
            fast_dict[candi["inchikey"][:14]].append(candi)

        deep_dict = defaultdict(list)
        for candi in res_deep:
            deep_dict[candi["inchikey"][:14]].append(candi)

        fast_keys = set(fast_dict.keys())
        deep_keys = set(deep_dict.keys())

        # Find the matching and non-matching fast_keys and deep_keys
        intersection_keys = deep_keys & fast_keys
        fast_unmatcheds = fast_keys - intersection_keys
        deep_unmatcheds = deep_keys - intersection_keys

        final_candis = []  # The list for storing the final results
        
        for intersection_key in intersection_keys:
            fast_candi = fast_dict[intersection_key]
            deep_candi = deep_dict[intersection_key]
            final_candis.append(
                {
                    "candi_index": fast_candi[0]["candi_index"],
                    "distance": fast_candi[0]["distance"] * weight_rate1 + deep_candi[0]["distance"] * weight_rate2,
                    # "smiles": fast_candi[0]["smiles"],
                    # "mw": fast_candi[0]["mw"],
                    # "inchikey": fast_candi[0]["inchikey"],
                }
            )

        for fast_unmatched in fast_unmatcheds:
            fast_candi = fast_dict[fast_unmatched]
            final_candis.append(
                {
                    "candi_index": fast_candi[0]["candi_index"],
                    "distance": fast_candi[0]["distance"] * weight_rate1 + weight_rate2,
                    # "smiles": fast_candi[0]["smiles"],
                    # "mw": fast_candi[0]["mw"],
                    # "inchikey": fast_candi[0]["inchikey"],
                }
            )

        for deep_unmatched in deep_unmatcheds:
            deep_candi = deep_dict[deep_unmatched]
            final_candis.append(
                {
                    "candi_index": deep_candi[0]["candi_index"],
                    "distance": deep_candi[0]["distance"] * weight_rate2 + weight_rate1,
                    # "smiles": deep_candi[0]["smiles"],
                    # "mw": deep_candi[0]["mw"],
                    # "inchikey": deep_candi[0]["inchikey"],
                }
            )


        final_candis = heapq.nsmallest(100, final_candis, key=lambda x: x["distance"])
        
        # # Process each candidate: add metadata
        # for j, candi in enumerate(final_candis):
        #     candi["smiles"] = str(general_var["smi_list"][candi["candi_index"]])
        #     candi["mw"] = float(general_var["mw_list"][candi["candi_index"]])
        #     candi["inchikey"] = str(general_var["inchikey_list"][candi["candi_index"]])
        # print(final_candis)
        final_candis_list.append(final_candis)

    return final_candis_list


def get_FastEI(vectors: list) -> list:

    return fastei.predict(vectors)


def get_deepei(vectors: list) -> list:

    return deepei.predict(vectors)
