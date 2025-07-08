from commframe import FLClient,FLMessage
import numpy as np
import time

class FLTrainer(FLClient):
    def __init__(self, client_id, server_host, server_port, local_data_size=100):
        super().__init__(client_id, server_host, server_port)
        self.local_data = np.random.rand(local_data_size)  # Simulate local data
        self.local_model = {'weights': np.random.rand(10)}  # Initial local model
        self.current_round = 0

    def on_message_received(self, msg: FLMessage):
        if msg.type == 'params':
            print(f"Client {self.client_id} received global model at round {msg.round}")
            self.current_round = msg.round
            self.local_model = msg.data  # Update the local model
            
            # Simulated local training
            self.local_train()
            
            # Send the update back to the server
            self.send_updates()
            
        elif msg.type == 'control' and msg.data.get('command') == 'stop':
            print(f"Client {self.client_id} stopping...")
            self.close()

    def local_train(self):
        """Simulate the local training process"""
        print(f"Client {self.client_id} training at round {self.current_round}")
        time.sleep(1)  # Time spent on simulation training
        
        # Simulation parameter update
        noise = np.random.normal(0, 0.1, size=self.local_model['weights'].shape)
        self.local_model['weights'] += noise * np.mean(self.local_data)

    def send_updates(self):
        """Send local updates to the server"""
        self.send(
            msg_type='params',
            data=self.local_model,
            round=self.current_round
        )
        print(f"Client {self.client_id} sent updates for round {self.current_round}")

if __name__ == '__main__':
    # Simulate three client devices
    clients = [
        FLTrainer(f"client_{i}", "localhost", 5588) 
        for i in range(1, 4)
    ]
    
    # connect to server
    for client in clients:
        client.connect()
        time.sleep(0.5)
    
    for client in clients:
        client.send(
            msg_type='params',
            data=client.local_model,
            round=0
        )
        time.sleep(0.3)
    
    # 保持运行
    try:
        while any(c.running for c in clients):
            time.sleep(1)
    except KeyboardInterrupt:
        for client in clients:
            client.close()
    
