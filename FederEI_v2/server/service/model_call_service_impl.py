import pathlib
import traceback
from api import model_call_pb2
from api import model_call_pb2_grpc
from models_api import models_interaction
from tools.util import check_path
import pickle
from resources.global_var import general_var
from tqdm import tqdm
from io import BytesIO
import sqlite3
import gc

class ModelCallServiceImpl(model_call_pb2_grpc.ModelCallServiceServicer):
    def getCandidateMessage(self, request, context):
        
        gc.disable()
        
        print("=====called:getCandidateMessage=====")
        try:
            ms_file_path = pathlib.Path.cwd() / "cache" / "ms_file" / request.fileName
            candi_db_path = pathlib.Path.cwd() / "cache" / "ms_file" / "candidate.db"
            # Path check
            check_path(ms_file_path)

            final_candis_list = models_interaction.get_candidate_message(ms_file_path)
            conn = sqlite3.connect(str(candi_db_path))
            cursor = conn.cursor()
            cursor.execute("DROP TABLE IF EXISTS candidate_message;")
            cursor.execute(
                """
                CREATE TABLE  candidate_message 
                (pid INTEGER PRIMARY KEY AUTOINCREMENT,
                id INTEGER,
                file_name TEXT,
                inchikey TEXT,
                smiles TEXT,
                rank INTEGER,
                distance FLOAT,
                mw FLOAT,
                candi_index INTEGER
                );
                """
            )
            for id_, candis in enumerate(final_candis_list):
                for candi in candis:
                    inchikey = candi["inchikey"]
                    smiles = candi["smiles"]
                    rank = candi["rank"]
                    distance = candi["distance"]
                    mw = candi["mw"]
                    candi_index = candi["candi_index"]

                    cursor.execute(
                        """
                            INSERT INTO candidate_message (id,file_name,inchikey,smiles,rank,distance,mw,candi_index)
                            VALUES (?,?,?,?,?,?,?,?);
                        """,
                        (
                            id_,
                            request.fileName,
                            inchikey,
                            smiles,
                            rank,
                            distance,
                            mw,
                            candi_index,
                        ),
                    )
            # Submit the transaction
            conn.commit()
            # Close the database connection
            conn.close()
            print("=====cache:complete=====")
            
            gc.enable()
            
            """Generate the block data of the file"""
            with open(candi_db_path, "rb") as f:
                while True:
                    chunk = f.read(49 * 1024 * 1024)
                    if not chunk:
                        break
                    yield model_call_pb2.GetCandidateMessageResponse(chunk=chunk)

            # results = []
            # for i, candis in enumerate(final_candis_list):
            #     candidates = []
            #     for candi in candis:
            #         candidates.append(
            #             model_call_pb2.GetCandidateMessageResponse.Candidate(
            #                 inchikey=candi["inchikey"],
            #                 smiles=candi["smiles"],
            #                 rank=candi["rank"],
            #                 distance=candi["distance"],
            #                 mw=candi["mw"],
            #                 # molStruc=candidate["molStruc"],
            #                 # againstMS=candidate["againstMS"],
            #                 mz=pickle.dumps(candi["mz"]),
            #                 intensities=pickle.dumps(candi["intensities"]),
            #             )
            #         )
            #     results.append(
            #         model_call_pb2.GetCandidateMessageResponse.Result(
            #             id=i,
            #             fileName=request.fileName,
            #             candidates=candidates,
            #         )
            #     )

            # for i in tqdm(range(0, len(results), 100)):
            #     yield model_call_pb2.GetCandidateMessageResponse(
            #         results=results[i : min(i + 100, len(results))]
            #     )
            print("=====over:getCandidateMessage=====")
        except Exception as e:
            traceback.print_exc()

    def plotMS(self, request, context):
        print("=====called:plotMS=====")
        try:
            mz = pickle.loads(request.mz)
            intensities = pickle.loads(request.intensities)

            answer = models_interaction.get_plot_ms(mz, intensities)

            print("=====over:plotMS=====")
            return model_call_pb2.PlotMSResponse(data=answer)

        except Exception as e:
            traceback.print_exc()

    def plotMSAgainst(self, request, context):
        print("=====called:plotMSAgainst=====")
        try:
            print(f"{request.candi_index} in function plotMSAgainst():")
            orig_mz = pickle.loads(request.orig_mz)
            orig_intensities = pickle.loads(request.orig_intensities)
            mz = general_var["mz_list"][request.candi_index]
            intensities = general_var["intensities_list"][request.candi_index]
            # mz = pickle.loads(request.mz)
            # intensities = pickle.loads(request.intensities)

            answer = models_interaction.get_plot_ms_against(
                orig_mz, orig_intensities, mz, intensities
            )

            print("=====over:plotMSAgainst=====")
            return model_call_pb2.PlotMSAgainstResponse(data=answer)

        except Exception as e:
            traceback.print_exc()

    def plotMolStruc(self, request, context):
        print("=====called:plotMolStruc=====")
        try:
            smiles = request.smiles

            answer = models_interaction.get_plot_mol_struc(smiles)
            print("=====over:plotMolStruc=====")
            return model_call_pb2.PlotMolStrucResponse(data=answer)
        except Exception as e:
            traceback.print_exc()

    def mwByPIM(self, request, context):
        print("=====called:mwByPim=====")
        try:
            ms_file_path = pathlib.Path.cwd() / "cache" / "ms_file" / request.fileName

            # Path check
            check_path(ms_file_path)

            answers = models_interaction.get_mw_by_pim(ms_file_path)
            for ans in answers:
                result = model_call_pb2.MWByPIMResponse.Result(mw=ans["result"]["mw"])
                yield model_call_pb2.MWByPIMResponse(
                    id=ans["number"],
                    result=result,
                )
            print("=====over:mwByPim=====")
        except Exception as e:
            traceback.print_exc()

    def fastEI(self, request, context):
        print("=====called:fastEI=====")
        try:
            ms_file_path = pathlib.Path.cwd() / "cache" / "ms_file" / request.fileName

            # Path check
            check_path(ms_file_path)

            answers = models_interaction.get_FastEI(ms_file_path)
            for ans in answers:
                matchedItems = []
                for res in ans["result"]:
                    matchedItems.append(
                        model_call_pb2.FastEIResponse.MatchedItem(
                            smiles=res["SMILES"],
                            distance=res["Distance"],
                        )
                    )
                yield model_call_pb2.FastEIResponse(
                    id=ans["number"],
                    result=model_call_pb2.FastEIResponse.Result(
                        matchedItems=matchedItems
                    ),
                )
            print("=====over:fastEI=====")
        except Exception as e:
            traceback.print_exc()

    def deepEI(self, request, context):
        print("=====called:deepEI=====")
        try:
            ms_file_path = pathlib.Path.cwd() / "cache" / "ms_file" / request.fileName

            # Path check
            check_path(ms_file_path)

            answers = models_interaction.get_deepei(ms_file_path)
            for ans in answers:
                candidates = []
                for candidate in ans["result"]["candidates"]:
                    candidates.append(
                        model_call_pb2.DeepEIResponse.Candidate(
                            smiles=candidate["smiles"],
                            score=candidate["score"],
                        )
                    )
                result = model_call_pb2.DeepEIResponse.Result(
                    candidates=candidates,
                )
                yield model_call_pb2.DeepEIResponse(
                    id=ans["number"],
                    result=result,
                )
            print("=====over:deepEI=====")
        except Exception as e:
            traceback.print_exc()
