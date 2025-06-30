import multiprocessing
import time
import gc 
from main import run  

gc.disable()

# ===== 可配置参数 =====
NODES = 2                # 要启动的节点数量
BASE_PORT = 50051          # 起始端口号
DATASET_PREFIX = "distributed"    # 数据集名前缀
# =====================

def start_cluster():
    """启动多节点集群"""
    
    processes = []
    
    # 启动所有节点进程
    for node_id in range(NODES):
        port = BASE_PORT + node_id
        dataset = f"{DATASET_PREFIX}_{NODES}_{node_id}"
        
        p = multiprocessing.Process(
            target=run,  # 直接使用导入的run函数
            args=(dataset, port),
            daemon=True
        )
        p.start()
        processes.append(p)

    # 主线程保持运行
    try:
        while True:
            time.sleep(1)
            # 可选：检查子进程状态
            if not all(p.is_alive() for p in processes):
                print("检测到有节点异常退出")
                break
    except KeyboardInterrupt:
        print("\n收到终止信号")
    finally:
        print("正在关闭所有节点...")
        for p in processes:
            p.terminate()
        print("所有节点已关闭")

if __name__ == "__main__":
    
    print(f"正在启动 {NODES} 个节点集群...")
    start_cluster()