import grpc
from concurrent import futures
from resources.global_var import init_var
from api import subnode_call_pb2,subnode_call_pb2_grpc
from service import subnode_call_service_impl

def run(dataset_name, port=50051):
    """Start gRPC server
    Args:
        dataset_name: The name of the library used
        port: Server port number, default 50051
    """
    # initialization variable
    init_var(dataset_name)
    
    # Create a server
    server = grpc.server(
        futures.ThreadPoolExecutor(max_workers=10),
        options=[
            ("grpc.max_send_message_length", 50 * 1024 * 1024),  # 50MB限制
            ("grpc.max_receive_message_length", 50 * 1024 * 1024),
        ],
    )

    # registration service
    subnode_call_pb2_grpc.add_SubNodeCallServiceServicer_to_server(
        subnode_call_service_impl.SubnodeCallServiceImpl(), 
        server
    )

    # Binding port
    server.add_insecure_port(f"0.0.0.0:{port}")

    # Start the service
    print(f"The service has been started | port: {port} | dataset: {dataset_name}")
    server.start()
    
    # Block the main thread
    server.wait_for_termination()
    

if __name__ == "__main__":
    run("distributed_2_0",50051)  # Default startup of a single node
    