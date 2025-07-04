import grpc
from api import file_manage_pb2_grpc
from api import model_call_pb2_grpc
from service import file_manage_service_impl
from service import model_call_service_impl
from concurrent import futures
from resources.global_var import init_var
import gc 

gc.disable()


# Start the server and provide RPC calls externally.
def run():
    init_var("base_dataset")
    # 1.Create a server object
    server = grpc.server(
        futures.ThreadPoolExecutor(max_workers=10),
        options=[
            ("grpc.max_send_message_length", 50 * 1024 * 1024),
            ("grpc.max_receive_message_length", 50 * 1024 * 1024),
        ],
    )

    # 2.Register the implemented service in the server object.
    file_manage_pb2_grpc.add_FileManageServiceServicer_to_server(
        file_manage_service_impl.FileManageServiceImpl(), server
    )
    model_call_pb2_grpc.add_ModelCallServiceServicer_to_server(
        model_call_service_impl.ModelCallServiceImpl(), server
    )

    # 3.Set the address for the server
    server.add_insecure_port("0.0.0.0:5577")  # 0.0.0.0 wildcard address, used by the server to listen to all available IP addresses

    # 4.Start the server
    print("The server has been activated.")
    server.start()

    # 5.Block the main thread
    server.wait_for_termination()


if __name__ == "__main__":
    run()
