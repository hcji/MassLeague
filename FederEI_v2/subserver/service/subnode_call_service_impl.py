from api import subnode_call_pb2,subnode_call_pb2_grpc
import grpc
import pathlib
from models_api import models_interaction
import numpy as np
from io import BytesIO
import pickle

class SubnodeCallServiceImpl(subnode_call_pb2_grpc.SubNodeCallServiceServicer):
    def getCandidates(self,request_iterator,context):
        print("==========getCandidates==========")
        try:
            buffer = BytesIO()
            print("准备接收 chunk")
            for request in request_iterator:
                if not request.chunk:
                    print("收到空 chunk，跳过")
                    continue
                buffer.write(request.chunk)
            print("接收完成")

            buffer.seek(0)
            print("尝试加载 numpy 数组")
            numpy_array = np.load(buffer)
            print("数据形状:", numpy_array.shape)

            results = models_interaction.get_candidate_message(numpy_array)
            print("结果大小:", len(results), len(results[0]))

            serialized_results = pickle.dumps(results)
            chunk_size = 49 * 1024 * 1024

            print("开始返回结果")
            for i in range(0, len(serialized_results), chunk_size):
                chunk = serialized_results[i:i + chunk_size]
                yield subnode_call_pb2.GetCandidatesResponse(chunk=chunk)

        except Exception as e:
            import traceback
            print("服务端异常:", traceback.format_exc())
            context.set_details(f"服务器异常: {e}")
            context.set_code(grpc.StatusCode.INTERNAL)
        
        