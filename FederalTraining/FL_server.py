from commframe import FLServer,FLMessage
import time
import numpy as np
from FL_training_pytorch import MyDataset,MLP,ModelManager
import torch
import torch.nn as nn
import torch.optim as optim
from tqdm import tqdm
from torch.utils.data import DataLoader,Dataset,random_split
import pathlib

class FedAvgServer(FLServer):
    def __init__(self,mm:ModelManager,client_num,port=5588):
        super().__init__(port=port)
        self.global_mm = mm
        self.client_num = client_num
        self.client_updates = {}
        self.current_round = 0
        self.total_rounds = 20

    def on_message_received(self, msg: FLMessage, sock):
        if msg.type == 'params':
            print(f"Round {msg.round}: Received params from {msg.sender}")
            self.client_updates[msg.sender] = msg.data
            
            # Check if sufficient updates have been collected
            if len(self.client_updates) >= self.client_num:
                with torch.no_grad():
                    for global_param in self.global_mm.model.parameters():
                        global_param.data.zero_() # Reset Global Model Parameters
                        
                    for cu in self.client_updates.values():
                        for global_param, client_param in zip(self.global_mm.model.parameters(), cu):
                            global_param.data += client_param.data / self.client_num  # 求平均
                    
                # Prepare for the next round.
                if self.current_round < self.total_rounds:
                    self.current_round += 1
                    self.broadcast_new_round()
                else:
                    self.broadcast_stop()


    def broadcast_new_round(self):
        """The broadcast global model has initiated a new round."""
        msg = FLMessage(
            type='params',
            sender='server',
            data=list(self.global_mm.model.parameters()),
            round=self.current_round
        )
        self.broadcast(msg)
        print(f"\nBroadcasting round {self.current_round} to all clients")

    def broadcast_stop(self):
        """Training Completion Notification"""
        msg = FLMessage(
            type='control',
            sender='server',
            data={'command': 'stop'},
            round=self.current_round
        )
        self.broadcast(msg)
        print("\nTraining completed! Stopping server...")
        self.stop()

if __name__ == '__main__':
    # Data load
    X = np.load('spectra_mat.npy')
    fingerprint_mat = np.load('fingerprint_mat.npy')

    indexs_shuffle = np.load('indexs_shuffle.npy')
    #The first 10 pieces of data constitute the training set, while the last one is the test set.
    indexs_list = np.array_split(indexs_shuffle, 11)

    # check: 0.1 < bias < 0.9
    rate_is_1 = np.sum(fingerprint_mat,axis=0) / fingerprint_mat.shape[0]
    fp_mat_index_filted = np.where((rate_is_1 >= 0.1) & (rate_is_1 <= 0.9))[0]

    # Check if there are any available GPUs
    device = torch.device("cuda:4" if torch.cuda.is_available() else "cpu")    
    
    # The process of federated learning
    X_ts = X[indexs_list[-1],:]
    Y_test = fingerprint_mat[indexs_list[-1],:]


    i = fp_mat_index_filted[0]
    Y_ts = Y_test[:,i]
    
    mlp_server = MLP(X.shape[1])  
    ts_loader = DataLoader(MyDataset(X_ts,Y_ts),batch_size=256,shuffle=False)
    optimizer_server = optim.Adam(mlp_server.parameters())
    mm_server = ModelManager(mlp_server,optimizer=optimizer_server,device=device,test_loader=ts_loader)


    server = FedAvgServer(mm_server,2,port=5588)
    print("Server initialized with random weights:")
    
    # Start the server thread
    import threading
    server_thread = threading.Thread(target=server.start,args=[2])
    server_thread.daemon = True
    server_thread.start()
    
    
    # Keep the main thread running
    try:
        while server.running:
            time.sleep(1)
    except KeyboardInterrupt:
        server.stop()
    
    accuracy,precision,recall,f1 = mm_server.test_model()
        
    pathlib.Path(f'./Fingerprint/mlp_models/torch_FL_fold/{i}/').mkdir(parents=True, exist_ok=True)
    pathlib.Path(f'./Fingerprint/results/torch_FL_fold/').mkdir(parents=True, exist_ok=True)
    mlp_result = open(f'Fingerprint/results/torch_FL_fold/result.txt', 'a+')
    mlp_result.write("\t".join([str(i)] + [str(j) for j in [accuracy, precision, recall, f1]]))
    mlp_result.write("\n")
    torch.save(mm_server.get_state_dict(), f'Fingerprint/mlp_models/torch_FL_fold/{i}.pth')