# **DeepMASS2 GUI**

DeepMASS2 is a cross-platform GUI software tool, which enables deep-learning based metabolite annotation
 via semantic similarity analysis of mass spectral language. This approach enables the prediction 
 of structurally related metabolites for the unknown compounds. By considering the chemical space, these 
 structurally related metabolites provide valuable information about the potential location of the unknown 
 metabolites and assist in ranking candidates obtained from molecular structure databases. 

https://github.com/hcji/DeepMASS2_GUI/assets/17610691/16589373-3c8f-4e85-9e7b-41310dfb34b2

## Installation

Please follow the following installation steps:

1. Install [Anaconda](https://www.anaconda.com/)  or [Miniconda](https://docs.conda.io/en/latest/miniconda.html) 

2.  Clone the repository and navigate into it:

   ```bash
   git clone https://github.com/hcji/MassLeague.git
   cd MassLeague/DeepMASS_v2
   ```

3. For the installation of dependencies

   Use the following step for installation:

   ```bash
   conda env create -f environment.yml
   conda activate deepmass
   ```

   Or follow the steps below:

   ​     (1) Create a new conda environment and activate:

   ```bash
   conda create -n deepmass python=3.8.13
   conda activate deepmass
   ```

   ​     (2) Install dependency (note, for *MacOS* some dependency may install with conda manually):

   ```bash
   pip install -r requirements.txt
   ```

4. Download the [dependent data](https://github.com/hcji/DeepMASS2_GUI/releases/tag/v0.99.1).    

   1. put the following files into *data* folder:

      ```bash
      DeepMassStructureDB-v1.1.csv
      references_index_negative_spec2vec.bin
      references_index_positive_spec2vec.bin
      references_spectrums_negative.pickle
      references_spectrums_positive.pickle
      ```

   2. put the following files into *model* folder:

      ```bash
      Ms2Vec_allGNPSnegative.hdf5
      Ms2Vec_allGNPSnegative.hdf5.syn1neg.npy
      Ms2Vec_allGNPSnegative.hdf5.wv.vectors.npy
      Ms2Vec_allGNPSpositive.hdf5
      Ms2Vec_allGNPSpositive.hdf5.syn1neg.npy
      Ms2Vec_allGNPSpositive.hdf5.wv.vectors.npy
      ```

5. Run DeepMASS

   ```bash
   python DeepMASS2.py
   ```


## Release

* [Version 0.99.0](https://github.com/hcji/DeepMASS2_GUI/releases/tag/v0.99.0)
* [Version 0.99.1](https://github.com/hcji/DeepMASS2_GUI/releases/tag/v0.99.1)

## Documentation

For the details on how to use DeepMASS, please check [Ducomentation](https://hcji.github.io/DeepMASS2_GUI/).

# **DeepMASS2 Distributed System**

## 1. Environment Dependencies(Central Server、Subserver、User Interface)

1. Clone the repository and navigate into it:

   ```bash
   git clone https://github.com/hcji/MassLeague.git
   cd MassLeague/DeepMASS_v2
   ```

2. Create and activate the Conda environment:

   ```bash
   conda env create -f environment.yml
   conda activate deepmass
   ```

3. Install additional dependencies:

   ```bash
   pip install cryptography
   ```

4. Model and database files (only required when running `client.py` on subservers)

Download the following files and place them in the `model/` directory: [dependent data](https://github.com/hcji/DeepMASS2_GUI/releases/tag/v0.99.1)

```bash
Ms2Vec_allGNPSnegative.hdf5
Ms2Vec_allGNPSnegative.hdf5.syn1neg.npy
Ms2Vec_allGNPSnegative.hdf5.wv.vectors.npy
Ms2Vec_allGNPSpositive.hdf5
Ms2Vec_allGNPSpositive.hdf5.syn1neg.npy
Ms2Vec_allGNPSpositive.hdf5.wv.vectors.npy
```

Reference Database (Optional)

- If you **do not have a custom database**, place **all** the following files in the `data/` directory: [dependent data](https://github.com/hcji/DeepMASS2_GUI/releases/tag/v0.99.1)

  ```bash
  DeepMassStructureDB-v1.1.csv
  references_index_negative_spec2vec.bin
  references_index_positive_spec2vec.bin
  references_spectrums_negative.pickle
  references_spectrums_positive.pickle
  ```

- If you **use a custom database**:

  - Keep `DeepMassStructureDB-v1.1.csv` unchanged in the `data/` directory.
  - Replace the `.pickle` and `.bin` files with your own data (you may keep the original filenames).
  - Or update the paths in `client.py` to point to your custom files.

> [!IMPORTANT]
>
> Refer to [`vectorize_reference_by_ms2vec.py`](https://github.com/hcji/DeepMASS2_Data_Processing/blob/master/Scripts/training_models/vectorize_reference_by_ms2vec.py) to regenerate each `.bin` file from its corresponding `.pickle` file.

***Note**: Step 4 is only required when running `client.py`.*

------

## 2. Deployment Architecture

| Role           | Script                    | Startup Example                                              |
| -------------- | ------------------------- | ------------------------------------------------------------ |
| Central Server | `server.py`               | `python server.py <SERVER_IP> <REG_PORT> <FILE_PORT>`        |
| Subserver      | `client.py`               | `python client.py <SERVER_IP> <REG_PORT> <FILE_PORT> <CLIENT_IP> <CLIENT_FILE_PORT>` |
| User Interface | `DistributedDeepMASS2.py` | In the GUI prompt, enter `<SERVER_IP>:<FILE_PORT>` and run   |

- **CENTRAL_SERVER_IP**: IP address of the central server (e.g., `192.168.1.10`)
- **SUBSERVER_IP**:   IP address of each subserver (e.g., `192.168.1.3`, `192.168.1.4`, …)
- **REG_PORT**:       Port for subserver registration (e.g., `5001`)
- **FILE_PORT**:      Port for user file uploads on the central server (e.g., `5002`)
- **CLIENT_FILE_PORT**: Port on each subserver for listening to file requests (e.g., `6001`, `6002`, …)

------

## 3. Usage Steps

### 1. Check Local IP

- **Linux**:

  ```bash
  ip addr show   # or `hostname -I`
  ```

- **Windows**:

  ```bash
  ipconfig
  ```

*Look for the `inet` / `IPv4 Address` entry to find your LAN IP.*

### 2. Start the Central Server

**Run on the central server (e.g., at IP `192.168.1.10`):**

```bash
cd MassLeague/DeepMASS_v2/server
python server.py 192.168.1.10 5001 5002
```

- Listening for registrations: `5001`
- Listening for user file uploads: `5002`

### 3. Start Subservers (Multiple Instances Supported)

**Run on the subserver (e.g., at IP `192.168.1.3`):**

```bash
cd MassLeague/DeepMASS_v2
python client.py 192.168.1.10 5001 5002 192.168.1.3 6001
```

- `192.168.1.10`: Central server IP
- `5001`: Registration port
- `5002`: User file upload port
- `192.168.1.3`: This subserver’s IP
- `6001`: File service listening port on this subserver

*Subservers will automatically register with the central server upon startup.*

### 4. Start the User Interface

```bash
cd MassLeague/DeepMASS_v2
python DistributedDeepMASS2.py
```

1. Click **IP Address**, then enter the central server’s IP and file-port in the format
   `<SERVER_IP>:<FILE_PORT>` (for example, `192.168.1.10:5002`).
2. Click **Open** to upload your mass spectrometry file.
3. Click **Run DeepMASS** to encrypt and upload your file to the central server, which then distributes it to all subservers.
4. To save results, click **Save** to export them as CSV files.

## Citation

In preparation
        

## Contact

Ji Hongchao   

E-mail: ji.hongchao@foxmail.com    

<div itemscope itemtype="https://schema.org/Person"><a itemprop="sameAs" content="https://orcid.org/0000-0002-7364-0741" href="https://orcid.org/0000-0002-7364-0741" target="orcid.widget" rel="me noopener noreferrer" style="vertical-align:top;"><img src="https://orcid.org/sites/default/files/images/orcid_16x16.png" style="width:1em;margin-right:.5em;" alt="ORCID iD icon">https://orcid.org/0000-0002-7364-0741</a></div>

WeChat public account: Chemocoder 

<img align="center" src="https://github.com/hcji/hcji/blob/main/img/qrcode.jpg" width="20%"/>