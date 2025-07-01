from .lib_utils import load_general_lib,load_deepei_lib,load_fastei_lib
from .lib_utils import gen_general_lib,gen_deepei_lib,gen_fastei_lib
import pathlib
import gc

config = {
    "database_now": None,
}

is_loadeds = {
    "general": False,
    "deepei": False,
    "fastei": False,
}

general_var = {
    "smi_list": None,
    "mz_list": None,
    "intensities_list": None,
    "mw_list": None,
    "inchikey_list": None,
    "mwformer": None,
}

deepei_var = {
    "fp_list": None,
    "model_list": None,
    "fpkeep": None,
    "hnsw_index": None,
}

fastei_var = {
    "spectovec": None,
    "hnsw_index": None,
    "vector_list": None,
}


def init_var(dataset_name: str="base_dataset"):

    gc.disable()
    
    global config, is_loadeds, general_var, deepei_var, fastei_var

    if config["database_now"] is None :
        
        if not is_loadeds["general"]:
            print("==========load lib:general==========")
            (
                general_var["smi_list"],
                general_var["mz_list"],
                general_var["intensities_list"],
                general_var["mw_list"],
                general_var["inchikey_list"],
            ) = load_general_lib(dataset_name)
            is_loadeds["general"] =True
            
        if not is_loadeds["deepei"]:
            print("==========load lib:deepei==========")
            (
                deepei_var["fp_list"],
                deepei_var["model_list"],
                deepei_var["fpkeep"],
                deepei_var["hnsw_index"],
            ) = load_deepei_lib(dataset_name)
            is_loadeds["deepei"] = True

        if not is_loadeds["fastei"]:
            print("==========load lib:fastei==========")
            (
                fastei_var["spectovec"],
                fastei_var["hnsw_index"],
                fastei_var["vector_list"],
            ) = load_fastei_lib(dataset_name)
            is_loadeds["fastei"] = True
            
        config["database_now"] = dataset_name
    
    gc.enable()
        
def gen_var(spectrum_list: list, dataset_name: str):
    path = pathlib.Path(__file__).resolve().parent / "database" / dataset_name
    if not path.exists():
        path.mkdir(parents=True)
    else :
        raise f"\"{path}\" already exists"
        
    print("==========gen_lib:general==========")
    gen_general_lib(spectrum_list,dataset_name)
    print("==========gen_lib:deepei==========")
    gen_deepei_lib(spectrum_list,dataset_name)
    print("==========gen_lib:fastei==========")
    gen_fastei_lib(spectrum_list,dataset_name)
    
    
def change_var(dataset_name:str):
    pass