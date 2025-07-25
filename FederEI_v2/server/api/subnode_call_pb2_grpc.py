# Generated by the gRPC Python protocol compiler plugin. DO NOT EDIT!
"""Client and server classes corresponding to protobuf-defined services."""
import grpc

from . import subnode_call_pb2 as subnode__call__pb2


class SubNodeCallServiceStub(object):
    """定义服务
    """

    def __init__(self, channel):
        """Constructor.

        Args:
            channel: A grpc.Channel.
        """
        self.getCandidates = channel.stream_stream(
                '/SubNodeCallService/getCandidates',
                request_serializer=subnode__call__pb2.GetCandidatesRequest.SerializeToString,
                response_deserializer=subnode__call__pb2.GetCandidatesResponse.FromString,
                )


class SubNodeCallServiceServicer(object):
    """定义服务
    """

    def getCandidates(self, request_iterator, context):
        """Missing associated documentation comment in .proto file."""
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')


def add_SubNodeCallServiceServicer_to_server(servicer, server):
    rpc_method_handlers = {
            'getCandidates': grpc.stream_stream_rpc_method_handler(
                    servicer.getCandidates,
                    request_deserializer=subnode__call__pb2.GetCandidatesRequest.FromString,
                    response_serializer=subnode__call__pb2.GetCandidatesResponse.SerializeToString,
            ),
    }
    generic_handler = grpc.method_handlers_generic_handler(
            'SubNodeCallService', rpc_method_handlers)
    server.add_generic_rpc_handlers((generic_handler,))


 # This class is part of an EXPERIMENTAL API.
class SubNodeCallService(object):
    """定义服务
    """

    @staticmethod
    def getCandidates(request_iterator,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            insecure=False,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.stream_stream(request_iterator, target, '/SubNodeCallService/getCandidates',
            subnode__call__pb2.GetCandidatesRequest.SerializeToString,
            subnode__call__pb2.GetCandidatesResponse.FromString,
            options, channel_credentials,
            insecure, call_credentials, compression, wait_for_ready, timeout, metadata)
