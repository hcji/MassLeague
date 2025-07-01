# __init__.py

from . import file_manage_pb2
from . import file_manage_pb2_grpc
from . import model_call_pb2
from . import model_call_pb2_grpc

# __all__属性只对import *起效
__all__ = [
    "file_manage_pb2",
    "file_manage_pb2_grpc",
    "model_call_pb2",
    "model_call_pb2_grpc",
]
