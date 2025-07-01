import pathlib
import grpc
from matchms.importing import load_from_msp
from tools.loader import load_from_mgf
import traceback
from api import file_manage_pb2
from api import file_manage_pb2_grpc
from tools.util import check_path, delete_files
from models_api import models_interaction
from rdkit import Chem
import pickle


class FileManageServiceImpl(file_manage_pb2_grpc.FileManageServiceServicer):
    def submitFile(self, request_iterator, context):
        print("=====called:submitFile=====")
        ms_file_dir_path = pathlib.Path.cwd() / "cache" / "ms_file"
        if not ms_file_dir_path.exists():
            ms_file_dir_path.mkdir(parents=True)
        file_name = None
        save_path = None
        # 保存上传的文件
        for request in request_iterator:
            if request.HasField("chunk"):
                with open(save_path, "ab") as f:
                    f.write(request.chunk)
            elif request.HasField("name"):
                file_name = request.name
                save_path = ms_file_dir_path / file_name
                if save_path.exists() and save_path.is_file():
                    print(save_path)
                    # 抛出重复上传异常
                    context.set_code(grpc.StatusCode.ALREADY_EXISTS)
                    context.set_details(f"{file_name} repeat upload")
                    raise
        # # ==========测试1==========
        # if file_suffix == ".mgf":
        #     for i, spec in enumerate([{'compound_name':f"{k}"} for k in range(10)]):
        #         result = file_manage_pb2.SubmitFileResponse.Result(
        #             id=i,
        #             title=spec["compound_name"],
        #             fileName=save_path.name,

        #         )
        #         response = file_manage_pb2.SubmitFileResponse(
        #             result=result,
        #         )
        #         yield response
        # # ==========测试1==========
        
        # 准备返回值
        file_suffix = save_path.suffix
        if file_suffix == ".mgf":
            spectrum_list = load_from_mgf(str(save_path))
        elif file_suffix == ".msp":
            spectrum_list = load_from_msp(str(save_path))
        for i, spec in enumerate(spectrum_list):
            if spec.metadata.get("inchikey") is not None:
                inchikey = spec.metadata["inchikey"]
            elif spec.metadata.get("smiles") is not None:
                smi = spec.metadata["smiles"]
                mol = Chem.MolFromSmiles(smi)
                inchikey = Chem.MolToInchiKey(mol)
            else:
                inchikey = None
            result = file_manage_pb2.SubmitFileResponse.Result(
                id=i,
                title=spec.metadata.get("compound_name"),
                fileName=save_path.name,
                mw=models_interaction.mw_by_pim.get_mw_by_pim_from_spectrum(spec),
                mz=pickle.dumps(spec.mz),
                intensities=pickle.dumps(spec.intensities),
                inchikey=inchikey,
            )
            response = file_manage_pb2.SubmitFileResponse(
                result=result,
            )
            yield response
        print("=====over:submitFile=====")


    def clearCache(self, request, context):
        print("=====called:clearCache=====")
        cache_dir_path = pathlib.Path.cwd() / "cache"
        print(request.desc)
        for item in cache_dir_path.iterdir():
            delete_files(item)

        return file_manage_pb2.ClearCacheResponse(
            desc="The server successfully deleted the cache."
        )
