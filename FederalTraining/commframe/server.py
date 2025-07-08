import socket
import threading
from typing import Dict
from .protocol import FLMessage
from abc import ABC,abstractmethod

class FLServer(ABC):
    def __init__(self, host='0.0.0.0', port=5588):
        self.host = host
        self.port = port
        self.clients: Dict[str, socket.socket] = {}
        self.lock = threading.Lock()
        self.running = False

    def start(self,client_num):
        """start server"""
        self.running = True
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        sock.bind((self.host, self.port))
        sock.listen(client_num)
        print(f"FL Server listening on {self.host}:{self.port}")

        try:
            while self.running:
                client_sock, addr = sock.accept()
                threading.Thread(
                    target=self.handle_client,
                    args=(client_sock,),
                    daemon=True
                ).start()
        finally:
            sock.close()

    def handle_client(self, sock: socket.socket):
        """Handle client connections"""
        try:
            while self.running:
                # Receive message header (4byte)
                header = sock.recv(4)
                if not header: break
                length = int.from_bytes(header, 'big')

                # Receive message body
                chunks = []
                received = 0
                while received < length:
                    chunk = sock.recv(min(length - received, 4096))
                    if not chunk: break
                    chunks.append(chunk)
                    received += len(chunk)

                if received != length: break

                # Handle the message
                msg = FLMessage.deserialize(b''.join(chunks))
                with self.lock:
                    if msg.sender not in self.clients:
                        self.clients[msg.sender] = sock
                        print(f"New client: {msg.sender}")

                self.on_message_received(msg, sock)
        except ConnectionError:
            pass
        finally:
            with self.lock:
                for id, s in list(self.clients.items()):
                    if s == sock:
                        del self.clients[id]
                        break
            sock.close()

    @abstractmethod
    def on_message_received(self, msg: FLMessage, sock: socket.socket):
        """Message processing callback (needs to be rewritten)"""
        pass
    
    def send(self, sock: socket.socket, msg: FLMessage):
        """Send a message to the designated client."""
        try:
            data = msg.serialize()
            sock.sendall(len(data).to_bytes(4, 'big') + data)
        except ConnectionError:
            with self.lock:
                for id, s in list(self.clients.items()):
                    if s == sock:
                        del self.clients[id]
                        break

    def broadcast(self, msg: FLMessage):
        """Broadcast the message to all clients"""
        with self.lock:
            for client_id, sock in list(self.clients.items()):
                self.send(sock, msg)

    def stop(self):
        """stop server"""
        self.running = False
        with self.lock:
            for sock in self.clients.values():
                sock.close()
            self.clients.clear()