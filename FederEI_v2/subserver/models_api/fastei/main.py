import pathlib

from matchms.importing import load_from_msp
import numpy as np
import matchms
from .util.candidates_utils import get_result_from_vectors

def predict(vectors=None):
    return get_result_from_vectors(vectors)
