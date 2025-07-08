from commframe import FLClient,FLServer,FLMessage
import time
import numpy as np

class FedAvgServer(FLServer):
    def __init__(self,client_num ,port=5588):
        super().__init__(port=port)
        self.global_model = {'weights': np.random.rand(10)}  # Initial global model
        self.client_updates = {}
        self.current_round = 0
        self.total_rounds = 5
        self.client_num = client_num
########################
    def on_message_received(self, msg: FLMessage, sock):
        if msg.type == 'params':
            print(f"Round {msg.round}: Received params from {msg.sender}")
            self.client_updates[msg.sender] = msg.data
            
            # Check if sufficient updates have been collected
            if len(self.client_updates) >= self.client_num:
                self.aggregate_updates()
                
                # Prepare for the next round.
                if self.current_round < self.total_rounds:
                    self.current_round += 1
                    self.broadcast_new_round()
                else:
                    self.broadcast_stop()

    def aggregate_updates(self):
        """FedAvg"""
        print(f"\nAggregating at round {self.current_round}")
        avg_weights = {}
        
        
        for key in self.global_model.keys():
            avg_weights[key] = sum(update[key] for update in self.client_updates.values()) / len(self.client_updates)
        self.global_model = avg_weights
        self.client_updates.clear()
        print(self.global_model)
        print(f"New global weights: {self.global_model['weights']}")

    def broadcast_new_round(self):
        """The broadcast global model has initiated a new round."""
        msg = FLMessage(
            type='params',
            sender='server',
            data=self.global_model,
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
    server = FedAvgServer(3,port=5588)
    print("Server initialized with random weights:", server.global_model['weights'][:3], "...")
    
    # Start the server thread
    import threading
    server_thread = threading.Thread(target=server.start,args=[3])
    server_thread.daemon = True
    server_thread.start()
    
    
    
    # Keep the main thread running
    try:
        while server.running:
            time.sleep(1)
    except KeyboardInterrupt:
        server.stop()