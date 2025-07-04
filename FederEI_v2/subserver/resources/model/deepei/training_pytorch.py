# -*- coding: utf-8 -*-
"""
Created on Tue Mar 19 08:50:45 2024

@author: DELL
"""

import numpy as np

from tqdm import tqdm

import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader,Dataset,TensorDataset
import torch.nn.functional as F

from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, precision_score, recall_score,f1_score

import numpy as np


class MyDataset(Dataset):
    def __init__(self, X, Y):
        self.X_data = torch.tensor(X,dtype=torch.float32)
        self.Y_data = torch.tensor(np.vstack((Y, (1-Y))).transpose(),dtype=torch.float32)

    def __len__(self):
        """Returns the size of the dataset"""
        return self.X_data.shape[0]

    def __getitem__(self, idx):
        """Returns data for the specified index"""
        return self.X_data[idx],self.Y_data[idx]

class MLP(nn.Module):
    def __init__(self, input_size):
        super(MLP, self).__init__()
        
        # MLP model architecture
        n = input_size
        layers = []
        # Three hidden layers
        layers.append(nn.Linear(n, n))  # Linear layer
        layers.append(nn.ReLU())  # ReLU activation
        layers.append(nn.Linear(n, int(n * 0.5)))  # Linear layer
        layers.append(nn.ReLU())  # ReLU activation
        layers.append(nn.Linear(int(n * 0.5), int(n * 0.25)))  # Linear layer
        layers.append(nn.ReLU())  # ReLU activation
        # Output layer
        layers.append(nn.Linear(int(n * 0.25), 2))  # Output layer with 2 classes
        # layers.append(nn.Softmax(dim=1) )  
        
        self.model = nn.Sequential(*layers)
        
    def forward(self, x):
        return self.model(x)
    
class ModelManager:
    def __init__(self, model, optimizer=None, criterion=None,device=None, train_loader=None, test_loader=None):
        self.device = device if device else torch.device("cpu")
        self.model = model.to(self.device)
        self.train_loader = train_loader
        self.test_loader = test_loader
        self.optimizer = optimizer
        self.criterion = criterion

    def train_model(self,epochs=8):
        self.model.train()
        for epoch in range(epochs):
            running_loss = 0.0
            correct = 0
            total = 0
            for inputs, labels in tqdm(self.train_loader,desc=f'Epoch {epoch+1}/{epochs}'):
                inputs,labels = inputs.to(self.device),labels.to(self.device)
                self.optimizer.zero_grad()  # Zero the parameter gradients
                logits = self.model(inputs)
                loss = self.criterion(logits, labels)
                loss.backward()  # Backpropagation
                self.optimizer.step()  # Update parameters

                outputs = F.softmax(logits,dim=1)
                running_loss += loss.item()
                _, predicted = torch.max(outputs, 1)
                _,labels_index = torch.max(labels,1)
                total += labels.size(0)
                correct += (predicted == labels_index).sum().item()

            epoch_loss = running_loss / len(self.train_loader)
            epoch_acc = correct / total
            tqdm.write(f"loss: {epoch_loss:.4f} - acc: {epoch_acc:.4f}")

    def test_model(self):
        self.model.eval()  # Set model to evaluation mode
        all_outputs = []
        all_labels = []
        with torch.no_grad():
            for inputs, labels in self.test_loader:
                inputs = inputs.to(self.device)
                # labels = labels.to(self.device)
                outputs = torch.round(F.softmax(self.model(inputs),dim=1))
                
                all_outputs.extend(outputs.cpu().numpy()[:,0])
                all_labels.extend(labels.cpu().numpy()[:,0])
        accuracy = accuracy_score(all_labels, all_outputs)
        precision = precision_score(all_labels, all_outputs)
        recall = recall_score(all_labels, all_outputs)
        f1 = f1_score(all_labels, all_outputs)
        return accuracy,precision,recall,f1
    
    def predict(self,input_data):
        self.model.eval()  # Set model to evaluation mode

        dataset = TensorDataset(input_data)
        dataloader = DataLoader(dataset, batch_size=10240, shuffle=False)

        all_output =[]
        
        with torch.no_grad():
            for batch_data in dataloader:
                batch_input = batch_data[0].to(self.device)
                output = torch.round(F.softmax(self.model(batch_input),dim=1))
                all_output.extend(output.cpu().detach().numpy()[:,0])
            
            return np.array(all_output)
                
    
    def get_state_dict(self):
        return self.model.state_dict()
    
    def load_state_dict(self,weights):
        self.model.load_state_dict(torch.load(weights))
        

if __name__ == "__main__":
    # Data load
    X = np.load('spectra_mat.npy')
    fingerprint_mat = np.load('fingerprint_mat.npy')

    # check: 0.1 < bias < 0.9
    rate_is_1 = np.sum(fingerprint_mat,axis=0) / fingerprint_mat.shape[0]
    fp_mat_index_filted = np.where((rate_is_1 >= 0.1) & (rate_is_1 <= 0.9))[0]

    device = torch.device("cuda:4" if torch.cuda.is_available() else "cpu")

    for i in tqdm(fp_mat_index_filted):
        Y = fingerprint_mat[:, i]
            
        X_tr, X_ts, Y_tr, Y_ts = train_test_split(X, Y, test_size=0.1)
        tr_loader = DataLoader(MyDataset(X_tr,Y_tr),batch_size=256,shuffle=True)
        ts_loader = DataLoader(MyDataset(X_ts,Y_ts),batch_size=256,shuffle=False)
        
        # build model
        mlp = MLP(X.shape[1])
        # summary(mlp)
        # print(mlp)
        optimizer = optim.Adam(mlp.parameters())
        criterion = nn.CrossEntropyLoss()
        mm = ModelManager(mlp,optimizer,criterion,device,tr_loader,ts_loader)

        mm.train_model(epochs=8)
        
        accuracy,precision,recall,f1 = mm.test_model()

        mlp_result = open('Fingerprint/results/mlp_result_torch.txt', 'a+')
        mlp_result.write("\t".join([str(i)] + [str(j) for j in [accuracy, precision, recall, f1]]))
        mlp_result.write("\n")
        torch.save(mm.get_state_dict(), 'Fingerprint/mlp_models/torch/{}.pth'.format(i))
        