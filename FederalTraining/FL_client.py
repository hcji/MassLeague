from commframe import FLClient,FLMessage
import numpy as np
import time
from FL_training_pytorch import MyDataset,MLP,ModelManager
import torch
import torch.nn as nn
import torch.optim as optim
from tqdm import tqdm
from torch.utils.data import DataLoader,Dataset,random_split


class FLTrainer(FLClient):
    def __init__(self, client_id, server_host, server_port,mm:ModelManager):
        super().__init__(client_id, server_host, server_port)
        self.local_mm = mm # Initial local model
        self.local_epochs = 5
        self.current_round = 0

    def on_message_received(self, msg: FLMessage):
        if msg.type == 'params':
            print(f"Client {self.client_id} received global model at round {msg.round}")
            self.current_round = msg.round
            for lmmp,msgd in zip(list(self.local_mm.model.parameters()),msg.data):# Update the local model
                lmmp.data = msgd.data
            
            self.local_mm.train_model(self.local_epochs)
            
            self.send(
                msg_type='params',
                data=list(self.local_mm.model.parameters()),
                round=self.current_round
            )
            print(f"Client {self.client_id} sent updates for round {self.current_round}")
            
        elif msg.type == 'control' and msg.data.get('command') == 'stop':
            print(f"Client {self.client_id} stopping...")
            self.close()

        

if __name__ == '__main__':
    
    # Data load
    X = np.load('spectra_mat.npy')
    fingerprint_mat = np.load('fingerprint_mat.npy')

    indexs_shuffle = np.load('indexs_shuffle.npy')
    # The first 10 pieces of data constitute the training set, while the last one is the test set.
    indexs_list = np.array_split(indexs_shuffle, 11)

    # check: 0.1 < bias < 0.9
    rate_is_1 = np.sum(fingerprint_mat,axis=0) / fingerprint_mat.shape[0]
    fp_mat_index_filted = np.where((rate_is_1 >= 0.1) & (rate_is_1 <= 0.9))[0]

    # Check if there are any available GPUs
    device = torch.device("cuda:4" if torch.cuda.is_available() else "cpu")    
    
    
    # Federal Learning Process
    i = fp_mat_index_filted[0]
    Y_client = fingerprint_mat[:,i]
    # Simulate three client devices
    clients = []
    for client_idx in range(1, 3):
    
        X_cl = X[indexs_list[client_idx-1],:]
        Y_cl = Y_client[indexs_list[client_idx-1]]
        tr_loader = DataLoader(MyDataset(X_cl,Y_cl),batch_size=256,shuffle=False)
        mlp_client = MLP(X.shape[1])  
        optimizer_client = optim.Adam(mlp_client.parameters())
        criterion = nn.CrossEntropyLoss()
        mm_client = ModelManager(mlp_client,optimizer=optimizer_client,criterion=criterion,device=device,train_loader=tr_loader)
        
        
        clients.append(FLTrainer(f"client_{client_idx}", "localhost", 5588,mm_client) )

    # connect to server
    for client in clients:
        client.connect()
        time.sleep(0.5)
    
    for client in clients:
        client.send(
            msg_type='params',
            data=list(client.local_mm.model.parameters()),
            round=0
        )
        time.sleep(0.3)
    
    try:
        while any(c.running for c in clients):
            time.sleep(1)
    except KeyboardInterrupt:
        for client in clients:
            client.close()
    
