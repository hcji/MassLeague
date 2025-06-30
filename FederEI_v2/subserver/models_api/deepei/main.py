import sys
import pathlib
sys.path.append(str(pathlib.Path(__file__).resolve().parent))

import numpy as np
import matchms
from .util.candidates_utils import get_result_from_vectors
# from .util.candidates_utils_withPIM import get_result_from_specs

def predict(vectors=None):
    return get_result_from_vectors(vectors)