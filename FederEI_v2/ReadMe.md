# **FerderEI2 GUI**

FerderEI2 is a cross-platform graphical user interface software tool with a front-end/back-end separation architecture, supporting deep-learning-based metabolite annotation through similarity analysis of mass spectra. This approach can infer structurally related metabolites of unknown compounds by using molecular fingerprint information predicted from the mass spectra by the model. Taking the chemical space into account, these structurally related metabolites provide valuable information about the potential location of unknown metabolites and help rank candidate structures retrieved from molecular structure databases.



## Installation

Please follow the following installation steps:

1. Install [Anaconda](https://www.anaconda.com/)  or [Miniconda](https://docs.conda.io/en/latest/miniconda.html) 

2. Clone the repository and navigate into it:

   ```bash
   git clone https://github.com/hcji/MassLeague.git
   cd MassLeague/FederEI_v2
   ```

3. For the installation of dependencies

   Use the following step for installation:

   ```bash
   conda env create -f environment.yml
   conda activate federei_v2
   ```

4. The project supports both centralized and distributed query modes.
   The client is developed in Java and can be built using a one-click Maven build.
   Please configure the server according to the instructions provided below.

   1. Online file URL

       [Model](https://zenodo.org/uploads/15825968) 
       [Dataset](https://zenodo.org/uploads/15833011) 

   2. Put the following files into *the server/resources/model/deepei/Fingerprint/mlp_models_torch/* folder and extract them.:

      ```bash
      deepei.zip.0**
      ```
   
   3. Put the following files into *the server/resources/model/fastei/* folder and extract them:

      ```bash
      fastei.zip
      ```
   
   4. Put the following files into *the server/resources/dataset/* folder and extract them:

      ```bash
      base_dataset.zip.***
      ```
   
5. Run FederEI_v2 server

   ```bash
   python FederEI_v2/server/main.py
   ```

6. Run FederEI_v2 client

   ```bash
   cd *client/*
   mvn compile exec:java
   ```
   



# **FerderEI2 Distributed System**

## 1. Environment Dependencies(Subserver)

1. Clone the repository and navigate into it:

   ```bash
   git clone https://github.com/hcji/MassLeague.git
   cd MassLeague/FerderEI_v2
   ```

2. Create and activate the Conda environment:

   ```bash
   conda env create -f environment.yml
   conda activate ferder_v2
   ```

3. The model files are the same as those used by the server.

4. The dataset on each sub-server is generated independently by invoking the `gen_var` function located in `resource/global_var.py`.



------

## 2. Deployment Architecture

| Role           | Script                                                       | Startup Example            |
| -------------- | ------------------------------------------------------------ | -------------------------- |
| Central Server | `server/main.py`                                             | `python server/main.py`    |
| Subserver      | `subserver/main.py`                                          | `python subserver/main.py` |
| User Interface | `FederEI_v2/client/pms-ui/src/main/java/org/jhcg/pms/ui/Boot.java` | `mvn compile exec:java`    |

------

## 3. Usage Steps

### 1. Start the Central Server

**Run on the central server (e.g., at IP `0.0.0.0:5577`):**

```bash
cd MassLeague/Ferder_v2/server
python main.py
```

- Listening : `5577`

### 3. Start Subservers (Multiple Instances Supported)

**Run on the subserver (e.g., at IP `0.0.0.0:5577`):**

```bash
cd MassLeague/Ferder_v2/subserver
python main.py
```

*Subservers will automatically register with the central server upon startup.*

### 4. Start the User Interface

```bash
cd MassLeague/FerderEI_v2/client
mvn compile exec:java
```

1. Click **Open** to upload your mass spectrometry file.
2. Click **Run** to execute the database search process. Results will be automatically returned to the graphical user interface (GUI) upon completion.
3. To save results, click **Save** to export them as CSV files.



# **FederalTraining Distributed System**      

1. Usage details can be found in the example:
   ```
   MassLeague/FederalTraining/example_server.py
   MassLeague/FederalTraining/example_client.py
   ```

   

## Contact

Ji Hongchao   

E-mail: ji.hongchao@foxmail.com    

<div itemscope itemtype="https://schema.org/Person"><a itemprop="sameAs" content="https://orcid.org/0000-0002-7364-0741" href="https://orcid.org/0000-0002-7364-0741" target="orcid.widget" rel="me noopener noreferrer" style="vertical-align:top;"><img src="https://orcid.org/sites/default/files/images/orcid_16x16.png" style="width:1em;margin-right:.5em;" alt="ORCID iD icon">https://orcid.org/0000-0002-7364-0741</a></div>

WeChat public account: Chemocoder 

<img align="center" src="https://github.com/hcji/hcji/blob/main/img/qrcode.jpg" width="20%"/>