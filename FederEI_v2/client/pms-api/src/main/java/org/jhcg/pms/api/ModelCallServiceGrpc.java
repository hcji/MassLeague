package org.jhcg.pms.api;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 *Define the prediction service
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.46.1)",
    comments = "Source: model_call.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ModelCallServiceGrpc {

  private ModelCallServiceGrpc() {}

  public static final String SERVICE_NAME = "ModelCallService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.GetCandidateMessageRequest,
      org.jhcg.pms.api.ModelCallProto.GetCandidateMessageResponse> getGetCandidateMessageMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getCandidateMessage",
      requestType = org.jhcg.pms.api.ModelCallProto.GetCandidateMessageRequest.class,
      responseType = org.jhcg.pms.api.ModelCallProto.GetCandidateMessageResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.GetCandidateMessageRequest,
      org.jhcg.pms.api.ModelCallProto.GetCandidateMessageResponse> getGetCandidateMessageMethod() {
    io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.GetCandidateMessageRequest, org.jhcg.pms.api.ModelCallProto.GetCandidateMessageResponse> getGetCandidateMessageMethod;
    if ((getGetCandidateMessageMethod = ModelCallServiceGrpc.getGetCandidateMessageMethod) == null) {
      synchronized (ModelCallServiceGrpc.class) {
        if ((getGetCandidateMessageMethod = ModelCallServiceGrpc.getGetCandidateMessageMethod) == null) {
          ModelCallServiceGrpc.getGetCandidateMessageMethod = getGetCandidateMessageMethod =
              io.grpc.MethodDescriptor.<org.jhcg.pms.api.ModelCallProto.GetCandidateMessageRequest, org.jhcg.pms.api.ModelCallProto.GetCandidateMessageResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getCandidateMessage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.jhcg.pms.api.ModelCallProto.GetCandidateMessageRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.jhcg.pms.api.ModelCallProto.GetCandidateMessageResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ModelCallServiceMethodDescriptorSupplier("getCandidateMessage"))
              .build();
        }
      }
    }
    return getGetCandidateMessageMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.PlotMSRequest,
      org.jhcg.pms.api.ModelCallProto.PlotMSResponse> getPlotMSMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "plotMS",
      requestType = org.jhcg.pms.api.ModelCallProto.PlotMSRequest.class,
      responseType = org.jhcg.pms.api.ModelCallProto.PlotMSResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.PlotMSRequest,
      org.jhcg.pms.api.ModelCallProto.PlotMSResponse> getPlotMSMethod() {
    io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.PlotMSRequest, org.jhcg.pms.api.ModelCallProto.PlotMSResponse> getPlotMSMethod;
    if ((getPlotMSMethod = ModelCallServiceGrpc.getPlotMSMethod) == null) {
      synchronized (ModelCallServiceGrpc.class) {
        if ((getPlotMSMethod = ModelCallServiceGrpc.getPlotMSMethod) == null) {
          ModelCallServiceGrpc.getPlotMSMethod = getPlotMSMethod =
              io.grpc.MethodDescriptor.<org.jhcg.pms.api.ModelCallProto.PlotMSRequest, org.jhcg.pms.api.ModelCallProto.PlotMSResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "plotMS"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.jhcg.pms.api.ModelCallProto.PlotMSRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.jhcg.pms.api.ModelCallProto.PlotMSResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ModelCallServiceMethodDescriptorSupplier("plotMS"))
              .build();
        }
      }
    }
    return getPlotMSMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.PlotMSAgainstRequest,
      org.jhcg.pms.api.ModelCallProto.PlotMSAgainstResponse> getPlotMSAgainstMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "plotMSAgainst",
      requestType = org.jhcg.pms.api.ModelCallProto.PlotMSAgainstRequest.class,
      responseType = org.jhcg.pms.api.ModelCallProto.PlotMSAgainstResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.PlotMSAgainstRequest,
      org.jhcg.pms.api.ModelCallProto.PlotMSAgainstResponse> getPlotMSAgainstMethod() {
    io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.PlotMSAgainstRequest, org.jhcg.pms.api.ModelCallProto.PlotMSAgainstResponse> getPlotMSAgainstMethod;
    if ((getPlotMSAgainstMethod = ModelCallServiceGrpc.getPlotMSAgainstMethod) == null) {
      synchronized (ModelCallServiceGrpc.class) {
        if ((getPlotMSAgainstMethod = ModelCallServiceGrpc.getPlotMSAgainstMethod) == null) {
          ModelCallServiceGrpc.getPlotMSAgainstMethod = getPlotMSAgainstMethod =
              io.grpc.MethodDescriptor.<org.jhcg.pms.api.ModelCallProto.PlotMSAgainstRequest, org.jhcg.pms.api.ModelCallProto.PlotMSAgainstResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "plotMSAgainst"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.jhcg.pms.api.ModelCallProto.PlotMSAgainstRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.jhcg.pms.api.ModelCallProto.PlotMSAgainstResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ModelCallServiceMethodDescriptorSupplier("plotMSAgainst"))
              .build();
        }
      }
    }
    return getPlotMSAgainstMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.PlotMolStrucRequest,
      org.jhcg.pms.api.ModelCallProto.PlotMolStrucResponse> getPlotMolStrucMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "plotMolStruc",
      requestType = org.jhcg.pms.api.ModelCallProto.PlotMolStrucRequest.class,
      responseType = org.jhcg.pms.api.ModelCallProto.PlotMolStrucResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.PlotMolStrucRequest,
      org.jhcg.pms.api.ModelCallProto.PlotMolStrucResponse> getPlotMolStrucMethod() {
    io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.PlotMolStrucRequest, org.jhcg.pms.api.ModelCallProto.PlotMolStrucResponse> getPlotMolStrucMethod;
    if ((getPlotMolStrucMethod = ModelCallServiceGrpc.getPlotMolStrucMethod) == null) {
      synchronized (ModelCallServiceGrpc.class) {
        if ((getPlotMolStrucMethod = ModelCallServiceGrpc.getPlotMolStrucMethod) == null) {
          ModelCallServiceGrpc.getPlotMolStrucMethod = getPlotMolStrucMethod =
              io.grpc.MethodDescriptor.<org.jhcg.pms.api.ModelCallProto.PlotMolStrucRequest, org.jhcg.pms.api.ModelCallProto.PlotMolStrucResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "plotMolStruc"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.jhcg.pms.api.ModelCallProto.PlotMolStrucRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.jhcg.pms.api.ModelCallProto.PlotMolStrucResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ModelCallServiceMethodDescriptorSupplier("plotMolStruc"))
              .build();
        }
      }
    }
    return getPlotMolStrucMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.MWByPIMRequest,
      org.jhcg.pms.api.ModelCallProto.MWByPIMResponse> getMwByPIMMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "mwByPIM",
      requestType = org.jhcg.pms.api.ModelCallProto.MWByPIMRequest.class,
      responseType = org.jhcg.pms.api.ModelCallProto.MWByPIMResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.MWByPIMRequest,
      org.jhcg.pms.api.ModelCallProto.MWByPIMResponse> getMwByPIMMethod() {
    io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.MWByPIMRequest, org.jhcg.pms.api.ModelCallProto.MWByPIMResponse> getMwByPIMMethod;
    if ((getMwByPIMMethod = ModelCallServiceGrpc.getMwByPIMMethod) == null) {
      synchronized (ModelCallServiceGrpc.class) {
        if ((getMwByPIMMethod = ModelCallServiceGrpc.getMwByPIMMethod) == null) {
          ModelCallServiceGrpc.getMwByPIMMethod = getMwByPIMMethod =
              io.grpc.MethodDescriptor.<org.jhcg.pms.api.ModelCallProto.MWByPIMRequest, org.jhcg.pms.api.ModelCallProto.MWByPIMResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "mwByPIM"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.jhcg.pms.api.ModelCallProto.MWByPIMRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.jhcg.pms.api.ModelCallProto.MWByPIMResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ModelCallServiceMethodDescriptorSupplier("mwByPIM"))
              .build();
        }
      }
    }
    return getMwByPIMMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.FastEIRequest,
      org.jhcg.pms.api.ModelCallProto.FastEIResponse> getFastEIMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "fastEI",
      requestType = org.jhcg.pms.api.ModelCallProto.FastEIRequest.class,
      responseType = org.jhcg.pms.api.ModelCallProto.FastEIResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.FastEIRequest,
      org.jhcg.pms.api.ModelCallProto.FastEIResponse> getFastEIMethod() {
    io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.FastEIRequest, org.jhcg.pms.api.ModelCallProto.FastEIResponse> getFastEIMethod;
    if ((getFastEIMethod = ModelCallServiceGrpc.getFastEIMethod) == null) {
      synchronized (ModelCallServiceGrpc.class) {
        if ((getFastEIMethod = ModelCallServiceGrpc.getFastEIMethod) == null) {
          ModelCallServiceGrpc.getFastEIMethod = getFastEIMethod =
              io.grpc.MethodDescriptor.<org.jhcg.pms.api.ModelCallProto.FastEIRequest, org.jhcg.pms.api.ModelCallProto.FastEIResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "fastEI"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.jhcg.pms.api.ModelCallProto.FastEIRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.jhcg.pms.api.ModelCallProto.FastEIResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ModelCallServiceMethodDescriptorSupplier("fastEI"))
              .build();
        }
      }
    }
    return getFastEIMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.DeepEIRequest,
      org.jhcg.pms.api.ModelCallProto.DeepEIResponse> getDeepEIMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "deepEI",
      requestType = org.jhcg.pms.api.ModelCallProto.DeepEIRequest.class,
      responseType = org.jhcg.pms.api.ModelCallProto.DeepEIResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.DeepEIRequest,
      org.jhcg.pms.api.ModelCallProto.DeepEIResponse> getDeepEIMethod() {
    io.grpc.MethodDescriptor<org.jhcg.pms.api.ModelCallProto.DeepEIRequest, org.jhcg.pms.api.ModelCallProto.DeepEIResponse> getDeepEIMethod;
    if ((getDeepEIMethod = ModelCallServiceGrpc.getDeepEIMethod) == null) {
      synchronized (ModelCallServiceGrpc.class) {
        if ((getDeepEIMethod = ModelCallServiceGrpc.getDeepEIMethod) == null) {
          ModelCallServiceGrpc.getDeepEIMethod = getDeepEIMethod =
              io.grpc.MethodDescriptor.<org.jhcg.pms.api.ModelCallProto.DeepEIRequest, org.jhcg.pms.api.ModelCallProto.DeepEIResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "deepEI"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.jhcg.pms.api.ModelCallProto.DeepEIRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.jhcg.pms.api.ModelCallProto.DeepEIResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ModelCallServiceMethodDescriptorSupplier("deepEI"))
              .build();
        }
      }
    }
    return getDeepEIMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ModelCallServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ModelCallServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ModelCallServiceStub>() {
        @java.lang.Override
        public ModelCallServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ModelCallServiceStub(channel, callOptions);
        }
      };
    return ModelCallServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ModelCallServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ModelCallServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ModelCallServiceBlockingStub>() {
        @java.lang.Override
        public ModelCallServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ModelCallServiceBlockingStub(channel, callOptions);
        }
      };
    return ModelCallServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ModelCallServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ModelCallServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ModelCallServiceFutureStub>() {
        @java.lang.Override
        public ModelCallServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ModelCallServiceFutureStub(channel, callOptions);
        }
      };
    return ModelCallServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   *定义预测服务
   * </pre>
   */
  public static abstract class ModelCallServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void getCandidateMessage(org.jhcg.pms.api.ModelCallProto.GetCandidateMessageRequest request,
        io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.GetCandidateMessageResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetCandidateMessageMethod(), responseObserver);
    }

    /**
     */
    public void plotMS(org.jhcg.pms.api.ModelCallProto.PlotMSRequest request,
        io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.PlotMSResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPlotMSMethod(), responseObserver);
    }

    /**
     */
    public void plotMSAgainst(org.jhcg.pms.api.ModelCallProto.PlotMSAgainstRequest request,
        io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.PlotMSAgainstResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPlotMSAgainstMethod(), responseObserver);
    }

    /**
     */
    public void plotMolStruc(org.jhcg.pms.api.ModelCallProto.PlotMolStrucRequest request,
        io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.PlotMolStrucResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPlotMolStrucMethod(), responseObserver);
    }

    /**
     */
    public void mwByPIM(org.jhcg.pms.api.ModelCallProto.MWByPIMRequest request,
        io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.MWByPIMResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getMwByPIMMethod(), responseObserver);
    }

    /**
     */
    public void fastEI(org.jhcg.pms.api.ModelCallProto.FastEIRequest request,
        io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.FastEIResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getFastEIMethod(), responseObserver);
    }

    /**
     */
    public void deepEI(org.jhcg.pms.api.ModelCallProto.DeepEIRequest request,
        io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.DeepEIResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeepEIMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetCandidateMessageMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                org.jhcg.pms.api.ModelCallProto.GetCandidateMessageRequest,
                org.jhcg.pms.api.ModelCallProto.GetCandidateMessageResponse>(
                  this, METHODID_GET_CANDIDATE_MESSAGE)))
          .addMethod(
            getPlotMSMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.jhcg.pms.api.ModelCallProto.PlotMSRequest,
                org.jhcg.pms.api.ModelCallProto.PlotMSResponse>(
                  this, METHODID_PLOT_MS)))
          .addMethod(
            getPlotMSAgainstMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.jhcg.pms.api.ModelCallProto.PlotMSAgainstRequest,
                org.jhcg.pms.api.ModelCallProto.PlotMSAgainstResponse>(
                  this, METHODID_PLOT_MSAGAINST)))
          .addMethod(
            getPlotMolStrucMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.jhcg.pms.api.ModelCallProto.PlotMolStrucRequest,
                org.jhcg.pms.api.ModelCallProto.PlotMolStrucResponse>(
                  this, METHODID_PLOT_MOL_STRUC)))
          .addMethod(
            getMwByPIMMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                org.jhcg.pms.api.ModelCallProto.MWByPIMRequest,
                org.jhcg.pms.api.ModelCallProto.MWByPIMResponse>(
                  this, METHODID_MW_BY_PIM)))
          .addMethod(
            getFastEIMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                org.jhcg.pms.api.ModelCallProto.FastEIRequest,
                org.jhcg.pms.api.ModelCallProto.FastEIResponse>(
                  this, METHODID_FAST_EI)))
          .addMethod(
            getDeepEIMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                org.jhcg.pms.api.ModelCallProto.DeepEIRequest,
                org.jhcg.pms.api.ModelCallProto.DeepEIResponse>(
                  this, METHODID_DEEP_EI)))
          .build();
    }
  }

  /**
   * <pre>
   *定义预测服务
   * </pre>
   */
  public static final class ModelCallServiceStub extends io.grpc.stub.AbstractAsyncStub<ModelCallServiceStub> {
    private ModelCallServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ModelCallServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ModelCallServiceStub(channel, callOptions);
    }

    /**
     */
    public void getCandidateMessage(org.jhcg.pms.api.ModelCallProto.GetCandidateMessageRequest request,
        io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.GetCandidateMessageResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getGetCandidateMessageMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void plotMS(org.jhcg.pms.api.ModelCallProto.PlotMSRequest request,
        io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.PlotMSResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPlotMSMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void plotMSAgainst(org.jhcg.pms.api.ModelCallProto.PlotMSAgainstRequest request,
        io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.PlotMSAgainstResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPlotMSAgainstMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void plotMolStruc(org.jhcg.pms.api.ModelCallProto.PlotMolStrucRequest request,
        io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.PlotMolStrucResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPlotMolStrucMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void mwByPIM(org.jhcg.pms.api.ModelCallProto.MWByPIMRequest request,
        io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.MWByPIMResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getMwByPIMMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void fastEI(org.jhcg.pms.api.ModelCallProto.FastEIRequest request,
        io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.FastEIResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getFastEIMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void deepEI(org.jhcg.pms.api.ModelCallProto.DeepEIRequest request,
        io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.DeepEIResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getDeepEIMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   *定义预测服务
   * </pre>
   */
  public static final class ModelCallServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<ModelCallServiceBlockingStub> {
    private ModelCallServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ModelCallServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ModelCallServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public java.util.Iterator<org.jhcg.pms.api.ModelCallProto.GetCandidateMessageResponse> getCandidateMessage(
        org.jhcg.pms.api.ModelCallProto.GetCandidateMessageRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getGetCandidateMessageMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.jhcg.pms.api.ModelCallProto.PlotMSResponse plotMS(org.jhcg.pms.api.ModelCallProto.PlotMSRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPlotMSMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.jhcg.pms.api.ModelCallProto.PlotMSAgainstResponse plotMSAgainst(org.jhcg.pms.api.ModelCallProto.PlotMSAgainstRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPlotMSAgainstMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.jhcg.pms.api.ModelCallProto.PlotMolStrucResponse plotMolStruc(org.jhcg.pms.api.ModelCallProto.PlotMolStrucRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPlotMolStrucMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.jhcg.pms.api.ModelCallProto.MWByPIMResponse> mwByPIM(
        org.jhcg.pms.api.ModelCallProto.MWByPIMRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getMwByPIMMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.jhcg.pms.api.ModelCallProto.FastEIResponse> fastEI(
        org.jhcg.pms.api.ModelCallProto.FastEIRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getFastEIMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.jhcg.pms.api.ModelCallProto.DeepEIResponse> deepEI(
        org.jhcg.pms.api.ModelCallProto.DeepEIRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getDeepEIMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   *定义预测服务
   * </pre>
   */
  public static final class ModelCallServiceFutureStub extends io.grpc.stub.AbstractFutureStub<ModelCallServiceFutureStub> {
    private ModelCallServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ModelCallServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ModelCallServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.jhcg.pms.api.ModelCallProto.PlotMSResponse> plotMS(
        org.jhcg.pms.api.ModelCallProto.PlotMSRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPlotMSMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.jhcg.pms.api.ModelCallProto.PlotMSAgainstResponse> plotMSAgainst(
        org.jhcg.pms.api.ModelCallProto.PlotMSAgainstRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPlotMSAgainstMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.jhcg.pms.api.ModelCallProto.PlotMolStrucResponse> plotMolStruc(
        org.jhcg.pms.api.ModelCallProto.PlotMolStrucRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPlotMolStrucMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_CANDIDATE_MESSAGE = 0;
  private static final int METHODID_PLOT_MS = 1;
  private static final int METHODID_PLOT_MSAGAINST = 2;
  private static final int METHODID_PLOT_MOL_STRUC = 3;
  private static final int METHODID_MW_BY_PIM = 4;
  private static final int METHODID_FAST_EI = 5;
  private static final int METHODID_DEEP_EI = 6;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ModelCallServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ModelCallServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_CANDIDATE_MESSAGE:
          serviceImpl.getCandidateMessage((org.jhcg.pms.api.ModelCallProto.GetCandidateMessageRequest) request,
              (io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.GetCandidateMessageResponse>) responseObserver);
          break;
        case METHODID_PLOT_MS:
          serviceImpl.plotMS((org.jhcg.pms.api.ModelCallProto.PlotMSRequest) request,
              (io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.PlotMSResponse>) responseObserver);
          break;
        case METHODID_PLOT_MSAGAINST:
          serviceImpl.plotMSAgainst((org.jhcg.pms.api.ModelCallProto.PlotMSAgainstRequest) request,
              (io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.PlotMSAgainstResponse>) responseObserver);
          break;
        case METHODID_PLOT_MOL_STRUC:
          serviceImpl.plotMolStruc((org.jhcg.pms.api.ModelCallProto.PlotMolStrucRequest) request,
              (io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.PlotMolStrucResponse>) responseObserver);
          break;
        case METHODID_MW_BY_PIM:
          serviceImpl.mwByPIM((org.jhcg.pms.api.ModelCallProto.MWByPIMRequest) request,
              (io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.MWByPIMResponse>) responseObserver);
          break;
        case METHODID_FAST_EI:
          serviceImpl.fastEI((org.jhcg.pms.api.ModelCallProto.FastEIRequest) request,
              (io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.FastEIResponse>) responseObserver);
          break;
        case METHODID_DEEP_EI:
          serviceImpl.deepEI((org.jhcg.pms.api.ModelCallProto.DeepEIRequest) request,
              (io.grpc.stub.StreamObserver<org.jhcg.pms.api.ModelCallProto.DeepEIResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class ModelCallServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ModelCallServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.jhcg.pms.api.ModelCallProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ModelCallService");
    }
  }

  private static final class ModelCallServiceFileDescriptorSupplier
      extends ModelCallServiceBaseDescriptorSupplier {
    ModelCallServiceFileDescriptorSupplier() {}
  }

  private static final class ModelCallServiceMethodDescriptorSupplier
      extends ModelCallServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ModelCallServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ModelCallServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ModelCallServiceFileDescriptorSupplier())
              .addMethod(getGetCandidateMessageMethod())
              .addMethod(getPlotMSMethod())
              .addMethod(getPlotMSAgainstMethod())
              .addMethod(getPlotMolStrucMethod())
              .addMethod(getMwByPIMMethod())
              .addMethod(getFastEIMethod())
              .addMethod(getDeepEIMethod())
              .build();
        }
      }
    }
    return result;
  }
}
