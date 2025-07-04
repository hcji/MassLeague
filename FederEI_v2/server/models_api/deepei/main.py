import sys
import pathlib
sys.path.append(str(pathlib.Path(__file__).resolve().parent))

from matchms.importing import load_from_msp
from tools.loader import load_from_mgf
import numpy as np
import matchms
from .util.candidates_utils import get_result_from_specs

def predict(specs=None):
    return get_result_from_specs(specs)


if __name__ == "__main__":
    msp_file_path = pathlib.Path().cwd() / "cache" / "ms_file" / "1-10.msp"
    print("hh")
    # print(get_json_from_file("./raw_data/demo.msp"))
    print(predict(str(msp_file_path)))