import multiprocessing
import time
import gc 
from main import run  

gc.disable()

# ===== Configurable parameters =====
NODES = 2                # The number of nodes to be started
BASE_PORT = 50051          # Starting port number
DATASET_PREFIX = "distributed"    # Dataset name prefix
# =====================

def start_cluster():
    """Start the multi-node cluster"""
    
    processes = []
    
    # Start all node processes
    for node_id in range(NODES):
        port = BASE_PORT + node_id
        dataset = f"{DATASET_PREFIX}_{NODES}_{node_id}"
        
        p = multiprocessing.Process(
            target=run,  # Directly use the imported "run" function
            args=(dataset, port),
            daemon=True
        )
        p.start()
        processes.append(p)

    # The main thread remains running.
    try:
        while True:
            time.sleep(1)
            # Check the status of child processes
            if not all(p.is_alive() for p in processes):
                print("An abnormal exit of a node has been detected.")
                break
    except KeyboardInterrupt:
        print("\nReceived the termination signal")
    finally:
        print("All nodes are being shut down....")
        for p in processes:
            p.terminate()
        print("All nodes have been shut down.")

if __name__ == "__main__":
    
    print(f"{NODES} nodes are being started...")
    start_cluster()