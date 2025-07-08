import socket
import threading
from .protocol import FLMessage
from abc import ABC,abstractmethod

class FLClient(ABC):
    def __init__(self, client_id: str, server_host: str, server_port: int):
        self.client_id = client_id
        self.server_host = server_host
        self.server_port = server_port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.running = False

    def connect(self):
        """Connect to the server"""
        self.sock.connect((self.server_host, self.server_port))
        self.running = True
        threading.Thread(
            target=self.receive_loop,
            daemon=True
        ).start()
        print(f"Connected to server as {self.client_id}")

    def send(self, msg_type: str, data: object, round: int = 0):
        """Send a message to the server"""
        msg = FLMessage(msg_type, self.client_id, data, round)
        try:
            serialized = msg.serialize()
            self.sock.sendall(len(serialized).to_bytes(4, 'big') + serialized)
        except ConnectionError:
            self.close()

    def receive_loop(self):
        """Message reception loop"""
        try:
            while self.running:
                # Receive message header
                header = self.sock.recv(4)
                if not header: break
                length = int.from_bytes(header, 'big')

                # Receive message body
                chunks = []
                received = 0
                while received < length:
                    chunk = self.sock.recv(min(length - received, 4096))
                    if not chunk: break
                    chunks.append(chunk)
                    received += len(chunk)

                if received != length: break

                # Handle the message
                msg = FLMessage.deserialize(b''.join(chunks))
                self.on_message_received(msg)
        except ConnectionError:
            pass
        finally:
            self.close()

    @abstractmethod
    def on_message_received(self, msg: FLMessage):
        """Message processing callback (needs to be rewritten)"""
        pass

    def close(self):
        """close connection"""
        if self.running:
            self.running = False
            self.sock.close()
            print("Connection closed")