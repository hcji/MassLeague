import numpy as np
import pickle

def load_npy(load_path:str):
    with open(load_path,"rb") as f:
        return np.load(f,allow_pickle=True)

def save_npy(save_path:str, obj):
    with open(save_path, "wb") as f:
        np.save(f, arr=obj)
        
def load_pickle(load_path: str):
    with open(load_path,"rb") as f:
        return pickle.load(f)
        
def save_pickle(save_path,obj):
    with open(save_path, "wb") as f:
        pickle.dump(obj,f)
        
