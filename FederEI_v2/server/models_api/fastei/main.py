import pathlib

from matchms.importing import load_from_msp
from tools.loader import load_from_mgf
import numpy as np
import matchms
from .util.candidates_utils import get_result_from_specs

def predict(specs=None):
    return get_result_from_specs(specs)
