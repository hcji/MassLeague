import pickle
from dataclasses import dataclass

@dataclass
class FLMessage:
    """The basic message structure of federated learning"""
    type: str       # 'init' | 'params' | 'gradients' | 'control'
    sender: str     # Sender identification
    data: object    # The actual data being transmitted
    round: int = 0  # Training rounds
    
    def serialize(self) -> bytes:
        """Serialize as a byte stream"""
        return pickle.dumps(self)
    
    @classmethod
    def deserialize(cls, data: bytes) -> 'FLMessage':
        """Deserialization from byte stream"""
        return pickle.loads(data)