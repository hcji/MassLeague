# MassLeague

**MassLeague: A MS-based Compound Structure Annotation Framework Compatible with Federated Learning**

> A collaborative and privacy-preserving framework for compound annotation from mass spectrometry data using deep learning and federated computing.

---

## 🧬 Overview

**MassLeague** is a structural annotation framework for mass spectrometry (MS) that enables accurate, reproducible, and privacy-respecting compound identification across institutions. It integrates cutting-edge spectrum-centric and structure-centric deep learning engines with a decentralized, federated learning architecture.

MassLeague addresses challenges such as:
- Limited spectral libraries  
- Data silos across labs  
- Low reproducibility of models  

---


## 🚀 Features

- **Multi Annotation Engines**
  - `FederEI v2`: EI-MS engine with in-silico spectral generation + fingerprint prediction
  - `DeepMASS v2`: MS/MS engine using Spec2Vec + structure localization

- **Federated Learning**
  - Secure multi-party computation (SMPC)
  - HNSW-based private spectral searching
  - Distributed model optimization without data sharing

---

## 🧪 Use Cases

- Metabolomics: Annotation of unknown metabolites in large-scale untargeted LC/GC-MS datasets

- Toxicology: Identification of unknown compounds in complex mixtures

- Drug Discovery: Structure elucidation from MS/MS data of synthetic or natural products

- Environmental Chemistry: Compound identification in environmental samples across labs

---

## 📂 Repository Structure

    MassLeague/
    ├── DeepMASS v2/                   # DeepMASS v2 MS/MS annotation engine
    │   ├── (sources)                  # Configuration files for training/searching
    │   ├── DeepMASS2.py               # Standalone Running DeepMASS v2
    │   └── DistributedDeepMASS2.py    # Distributed Running DeepMASS v2
    │   └── ReadMe.md 
    │
    ├── FederEI v2/                    # FederEI v2 EI-MS annotation engine
    │   ├── (sources)                  # Configuration files for training/searching
    │   ├── FederEI2.py (.exe/.jar)    # Executable file of FederEI v2
    │   └── ReadMe.md
    │
    ├── FederalTraining/
    │   ├── (sources)                  # Configuration files for training/searching
    │   ├── (scripts)                  # Running scripts for training/searching
    │   └── ReadMe.md
    │
    └── ReadMe.md
    
---

## 📦 Installation and Example

Installation instructions and example usage can be found in the respective `federei` and `deepmass` folders.

Please refer to:  
- `federei/README.md`  
- `deepmass/README.md`

---

## 👥 Contributors

  > Sihao Chang (isihaochang@outlook.com)    
  > Ziyao Xiong (ziyaobear@163.com)      
  > Hongchao Ji (jihongchao@caas.cn)
