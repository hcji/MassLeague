from matchms import Spectrum
from typing import Generator, Tuple, Iterator
import numpy as np
import pathlib


def load_from_mgf(filename: str) -> Generator[Spectrum, None, None]:
    for spectrum in parse_mgf_file(filename):
        metadata = spectrum.get("params", None)
        mz = spectrum["m/z array"]
        intensities = spectrum["intensity array"]
        # peak_comments = spectrum["peak comments"]
        # if peak_comments != {}:
        #     metadata["peak_comments"] = peak_comments

        # Sort by mz (if not sorted already)
        if not np.all(mz[:-1] <= mz[1:]):
            idx_sorted = np.argsort(mz)
            mz = mz[idx_sorted]
            intensities = intensities[idx_sorted]

        yield Spectrum(
            mz=mz,
            intensities=intensities,
            metadata=metadata,
            metadata_harmonization=False,
        )


def parse_mgf_file(filename: str) -> Generator[dict, None, None]:
    """Read mgf file and parse info in list of spectrum dictionaries."""
    params = {}
    masses = []
    intensities = []
    # peak_comments = {}

    # # Peaks counter. Used to track and count the number of peaks
    # peakscount = 0

    with open(filename, "r", encoding="utf-8") as f:
        for line in f:
            rline = line.strip()

            if rline == "BEGIN IONS" or rline == "":
                params = {}
                masses = []
                intensities = []
                # peak_comments = {}
                continue
            elif rline == "END IONS":
                yield {
                    "params": (params),
                    "m/z array": np.array(masses),
                    "intensity array": np.array(intensities),
                    # 'peak comments': peak_comments
                }
            else:
                if "=" in rline:
                    key, value = rline.split("=", 1)
                    params[key.strip().lower()] = value.strip()
                else:
                    # Obtaining the masses and intensities
                    peak_pairs = rline.split(maxsplit=1)
                    mz = float(peak_pairs[0].strip())
                    intensity = float(peak_pairs[1].strip())
                    # comment = get_peak_comment(peak)
                    # if comment is not None:
                    #     peak_comments.update({mz: comment})

                    masses.append(mz)
                    intensities.append(intensity)


if __name__ == "__main__":
    # mgf_file_path = pathlib.Path.cwd() / "cache" / "ms_file" / "1-10.mgf"
    # mgf_file = load_from_mgf(str(mgf_file_path))
    # for spec in mgf_file:
    #     print(spec.metadata["smiles"])
    
    mgf_path = pathlib.Path.cwd() / "cache" / "ms_file" / "1-10.mgf"
    for spec in load_from_mgf(str(mgf_path)):
        print(spec.metadata["compound_name"])
    
