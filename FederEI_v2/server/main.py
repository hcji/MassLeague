import grpc
from api import file_manage_pb2_grpc
from api import model_call_pb2_grpc
from service import file_manage_service_impl
from service import model_call_service_impl
from concurrent import futures
from resources.global_var import init_var
import gc 

gc.disable()


# 开启服务器，对外提供rpc调用
def run():
    init_var("base_dataset")
    # 1.创建服务器对象
    server = grpc.server(
        futures.ThreadPoolExecutor(max_workers=10),
        options=[
            ("grpc.max_send_message_length", 50 * 1024 * 1024),
            ("grpc.max_receive_message_length", 50 * 1024 * 1024),
        ],
    )

    # 2.注册实现的服务到服务器对象中
    file_manage_pb2_grpc.add_FileManageServiceServicer_to_server(
        file_manage_service_impl.FileManageServiceImpl(), server
    )
    model_call_pb2_grpc.add_ModelCallServiceServicer_to_server(
        model_call_service_impl.ModelCallServiceImpl(), server
    )

    # 3.为服务器设置地址
    server.add_insecure_port("0.0.0.0:5577")  # 0.0.0.0 通配符地址，用于服务器监听所有可用的 IP 地址

    # 4.启动服务器
    print("服务器已开启")
    server.start()

    # 5.阻塞主线程
    server.wait_for_termination()


if __name__ == "__main__":
    run()
