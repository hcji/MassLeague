import grpc
from concurrent import futures
from resources.global_var import init_var
from api import subnode_call_pb2,subnode_call_pb2_grpc
from service import subnode_call_service_impl

def run(dataset_name, port=50051):
    """启动 gRPC 服务
    Args:
        dataset_name: 数据集名称（传递给init_var）
        port: 服务端口号，默认50051
    """
    # 初始化变量
    init_var(dataset_name)
    
    # 创建服务器
    server = grpc.server(
        futures.ThreadPoolExecutor(max_workers=10),
        options=[
            ("grpc.max_send_message_length", 50 * 1024 * 1024),  # 50MB限制
            ("grpc.max_receive_message_length", 50 * 1024 * 1024),
        ],
    )

    # 注册服务
    subnode_call_pb2_grpc.add_SubNodeCallServiceServicer_to_server(
        subnode_call_service_impl.SubnodeCallServiceImpl(), 
        server
    )

    # 绑定端口
    server.add_insecure_port(f"0.0.0.0:{port}")

    # 启动服务
    print(f"服务已启动 | 端口: {port} | 数据集: {dataset_name}")
    server.start()
    
    # 阻塞主线程
    server.wait_for_termination()
    

if __name__ == "__main__":
    run("distributed_2_0",50051)  # 默认启动单节点
    