# Generated by the gRPC Python protocol compiler plugin. DO NOT EDIT!
"""Client and server classes corresponding to protobuf-defined services."""
import grpc

from . import model_call_pb2 as model__call__pb2


class ModelCallServiceStub(object):
    """定义预测服务
    """

    def __init__(self, channel):
        """Constructor.

        Args:
            channel: A grpc.Channel.
        """
        self.getCandidateMessage = channel.unary_stream(
                '/ModelCallService/getCandidateMessage',
                request_serializer=model__call__pb2.GetCandidateMessageRequest.SerializeToString,
                response_deserializer=model__call__pb2.GetCandidateMessageResponse.FromString,
                )
        self.plotMS = channel.unary_unary(
                '/ModelCallService/plotMS',
                request_serializer=model__call__pb2.PlotMSRequest.SerializeToString,
                response_deserializer=model__call__pb2.PlotMSResponse.FromString,
                )
        self.plotMSAgainst = channel.unary_unary(
                '/ModelCallService/plotMSAgainst',
                request_serializer=model__call__pb2.PlotMSAgainstRequest.SerializeToString,
                response_deserializer=model__call__pb2.PlotMSAgainstResponse.FromString,
                )
        self.plotMolStruc = channel.unary_unary(
                '/ModelCallService/plotMolStruc',
                request_serializer=model__call__pb2.PlotMolStrucRequest.SerializeToString,
                response_deserializer=model__call__pb2.PlotMolStrucResponse.FromString,
                )
        self.mwByPIM = channel.unary_stream(
                '/ModelCallService/mwByPIM',
                request_serializer=model__call__pb2.MWByPIMRequest.SerializeToString,
                response_deserializer=model__call__pb2.MWByPIMResponse.FromString,
                )
        self.fastEI = channel.unary_stream(
                '/ModelCallService/fastEI',
                request_serializer=model__call__pb2.FastEIRequest.SerializeToString,
                response_deserializer=model__call__pb2.FastEIResponse.FromString,
                )
        self.deepEI = channel.unary_stream(
                '/ModelCallService/deepEI',
                request_serializer=model__call__pb2.DeepEIRequest.SerializeToString,
                response_deserializer=model__call__pb2.DeepEIResponse.FromString,
                )


class ModelCallServiceServicer(object):
    """定义预测服务
    """

    def getCandidateMessage(self, request, context):
        """Missing associated documentation comment in .proto file."""
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')

    def plotMS(self, request, context):
        """Missing associated documentation comment in .proto file."""
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')

    def plotMSAgainst(self, request, context):
        """Missing associated documentation comment in .proto file."""
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')

    def plotMolStruc(self, request, context):
        """Missing associated documentation comment in .proto file."""
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')

    def mwByPIM(self, request, context):
        """Missing associated documentation comment in .proto file."""
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')

    def fastEI(self, request, context):
        """Missing associated documentation comment in .proto file."""
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')

    def deepEI(self, request, context):
        """Missing associated documentation comment in .proto file."""
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')


def add_ModelCallServiceServicer_to_server(servicer, server):
    rpc_method_handlers = {
            'getCandidateMessage': grpc.unary_stream_rpc_method_handler(
                    servicer.getCandidateMessage,
                    request_deserializer=model__call__pb2.GetCandidateMessageRequest.FromString,
                    response_serializer=model__call__pb2.GetCandidateMessageResponse.SerializeToString,
            ),
            'plotMS': grpc.unary_unary_rpc_method_handler(
                    servicer.plotMS,
                    request_deserializer=model__call__pb2.PlotMSRequest.FromString,
                    response_serializer=model__call__pb2.PlotMSResponse.SerializeToString,
            ),
            'plotMSAgainst': grpc.unary_unary_rpc_method_handler(
                    servicer.plotMSAgainst,
                    request_deserializer=model__call__pb2.PlotMSAgainstRequest.FromString,
                    response_serializer=model__call__pb2.PlotMSAgainstResponse.SerializeToString,
            ),
            'plotMolStruc': grpc.unary_unary_rpc_method_handler(
                    servicer.plotMolStruc,
                    request_deserializer=model__call__pb2.PlotMolStrucRequest.FromString,
                    response_serializer=model__call__pb2.PlotMolStrucResponse.SerializeToString,
            ),
            'mwByPIM': grpc.unary_stream_rpc_method_handler(
                    servicer.mwByPIM,
                    request_deserializer=model__call__pb2.MWByPIMRequest.FromString,
                    response_serializer=model__call__pb2.MWByPIMResponse.SerializeToString,
            ),
            'fastEI': grpc.unary_stream_rpc_method_handler(
                    servicer.fastEI,
                    request_deserializer=model__call__pb2.FastEIRequest.FromString,
                    response_serializer=model__call__pb2.FastEIResponse.SerializeToString,
            ),
            'deepEI': grpc.unary_stream_rpc_method_handler(
                    servicer.deepEI,
                    request_deserializer=model__call__pb2.DeepEIRequest.FromString,
                    response_serializer=model__call__pb2.DeepEIResponse.SerializeToString,
            ),
    }
    generic_handler = grpc.method_handlers_generic_handler(
            'ModelCallService', rpc_method_handlers)
    server.add_generic_rpc_handlers((generic_handler,))


 # This class is part of an EXPERIMENTAL API.
class ModelCallService(object):
    """定义预测服务
    """

    @staticmethod
    def getCandidateMessage(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            insecure=False,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_stream(request, target, '/ModelCallService/getCandidateMessage',
            model__call__pb2.GetCandidateMessageRequest.SerializeToString,
            model__call__pb2.GetCandidateMessageResponse.FromString,
            options, channel_credentials,
            insecure, call_credentials, compression, wait_for_ready, timeout, metadata)

    @staticmethod
    def plotMS(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            insecure=False,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_unary(request, target, '/ModelCallService/plotMS',
            model__call__pb2.PlotMSRequest.SerializeToString,
            model__call__pb2.PlotMSResponse.FromString,
            options, channel_credentials,
            insecure, call_credentials, compression, wait_for_ready, timeout, metadata)

    @staticmethod
    def plotMSAgainst(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            insecure=False,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_unary(request, target, '/ModelCallService/plotMSAgainst',
            model__call__pb2.PlotMSAgainstRequest.SerializeToString,
            model__call__pb2.PlotMSAgainstResponse.FromString,
            options, channel_credentials,
            insecure, call_credentials, compression, wait_for_ready, timeout, metadata)

    @staticmethod
    def plotMolStruc(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            insecure=False,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_unary(request, target, '/ModelCallService/plotMolStruc',
            model__call__pb2.PlotMolStrucRequest.SerializeToString,
            model__call__pb2.PlotMolStrucResponse.FromString,
            options, channel_credentials,
            insecure, call_credentials, compression, wait_for_ready, timeout, metadata)

    @staticmethod
    def mwByPIM(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            insecure=False,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_stream(request, target, '/ModelCallService/mwByPIM',
            model__call__pb2.MWByPIMRequest.SerializeToString,
            model__call__pb2.MWByPIMResponse.FromString,
            options, channel_credentials,
            insecure, call_credentials, compression, wait_for_ready, timeout, metadata)

    @staticmethod
    def fastEI(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            insecure=False,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_stream(request, target, '/ModelCallService/fastEI',
            model__call__pb2.FastEIRequest.SerializeToString,
            model__call__pb2.FastEIResponse.FromString,
            options, channel_credentials,
            insecure, call_credentials, compression, wait_for_ready, timeout, metadata)

    @staticmethod
    def deepEI(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            insecure=False,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_stream(request, target, '/ModelCallService/deepEI',
            model__call__pb2.DeepEIRequest.SerializeToString,
            model__call__pb2.DeepEIResponse.FromString,
            options, channel_credentials,
            insecure, call_credentials, compression, wait_for_ready, timeout, metadata)
