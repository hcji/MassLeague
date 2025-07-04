package org.jhcg.pms.api;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 *Define the file management service
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.46.1)",
    comments = "Source: file_manage.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class FileManageServiceGrpc {

  private FileManageServiceGrpc() {}

  public static final String SERVICE_NAME = "FileManageService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.jhcg.pms.api.FileManageProto.SubmitFileRequest,
      org.jhcg.pms.api.FileManageProto.SubmitFileResponse> getSubmitFileMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "submitFile",
      requestType = org.jhcg.pms.api.FileManageProto.SubmitFileRequest.class,
      responseType = org.jhcg.pms.api.FileManageProto.SubmitFileResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<org.jhcg.pms.api.FileManageProto.SubmitFileRequest,
      org.jhcg.pms.api.FileManageProto.SubmitFileResponse> getSubmitFileMethod() {
    io.grpc.MethodDescriptor<org.jhcg.pms.api.FileManageProto.SubmitFileRequest, org.jhcg.pms.api.FileManageProto.SubmitFileResponse> getSubmitFileMethod;
    if ((getSubmitFileMethod = FileManageServiceGrpc.getSubmitFileMethod) == null) {
      synchronized (FileManageServiceGrpc.class) {
        if ((getSubmitFileMethod = FileManageServiceGrpc.getSubmitFileMethod) == null) {
          FileManageServiceGrpc.getSubmitFileMethod = getSubmitFileMethod =
              io.grpc.MethodDescriptor.<org.jhcg.pms.api.FileManageProto.SubmitFileRequest, org.jhcg.pms.api.FileManageProto.SubmitFileResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "submitFile"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.jhcg.pms.api.FileManageProto.SubmitFileRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.jhcg.pms.api.FileManageProto.SubmitFileResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FileManageServiceMethodDescriptorSupplier("submitFile"))
              .build();
        }
      }
    }
    return getSubmitFileMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.jhcg.pms.api.FileManageProto.ClearCacheRequest,
      org.jhcg.pms.api.FileManageProto.ClearCacheResponse> getClearCacheMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "clearCache",
      requestType = org.jhcg.pms.api.FileManageProto.ClearCacheRequest.class,
      responseType = org.jhcg.pms.api.FileManageProto.ClearCacheResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.jhcg.pms.api.FileManageProto.ClearCacheRequest,
      org.jhcg.pms.api.FileManageProto.ClearCacheResponse> getClearCacheMethod() {
    io.grpc.MethodDescriptor<org.jhcg.pms.api.FileManageProto.ClearCacheRequest, org.jhcg.pms.api.FileManageProto.ClearCacheResponse> getClearCacheMethod;
    if ((getClearCacheMethod = FileManageServiceGrpc.getClearCacheMethod) == null) {
      synchronized (FileManageServiceGrpc.class) {
        if ((getClearCacheMethod = FileManageServiceGrpc.getClearCacheMethod) == null) {
          FileManageServiceGrpc.getClearCacheMethod = getClearCacheMethod =
              io.grpc.MethodDescriptor.<org.jhcg.pms.api.FileManageProto.ClearCacheRequest, org.jhcg.pms.api.FileManageProto.ClearCacheResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "clearCache"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.jhcg.pms.api.FileManageProto.ClearCacheRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.jhcg.pms.api.FileManageProto.ClearCacheResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FileManageServiceMethodDescriptorSupplier("clearCache"))
              .build();
        }
      }
    }
    return getClearCacheMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static FileManageServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FileManageServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FileManageServiceStub>() {
        @java.lang.Override
        public FileManageServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FileManageServiceStub(channel, callOptions);
        }
      };
    return FileManageServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static FileManageServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FileManageServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FileManageServiceBlockingStub>() {
        @java.lang.Override
        public FileManageServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FileManageServiceBlockingStub(channel, callOptions);
        }
      };
    return FileManageServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static FileManageServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FileManageServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FileManageServiceFutureStub>() {
        @java.lang.Override
        public FileManageServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FileManageServiceFutureStub(channel, callOptions);
        }
      };
    return FileManageServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   *定义文件管理服务
   * </pre>
   */
  public static abstract class FileManageServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public io.grpc.stub.StreamObserver<org.jhcg.pms.api.FileManageProto.SubmitFileRequest> submitFile(
        io.grpc.stub.StreamObserver<org.jhcg.pms.api.FileManageProto.SubmitFileResponse> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getSubmitFileMethod(), responseObserver);
    }

    /**
     */
    public void clearCache(org.jhcg.pms.api.FileManageProto.ClearCacheRequest request,
        io.grpc.stub.StreamObserver<org.jhcg.pms.api.FileManageProto.ClearCacheResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getClearCacheMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSubmitFileMethod(),
            io.grpc.stub.ServerCalls.asyncBidiStreamingCall(
              new MethodHandlers<
                org.jhcg.pms.api.FileManageProto.SubmitFileRequest,
                org.jhcg.pms.api.FileManageProto.SubmitFileResponse>(
                  this, METHODID_SUBMIT_FILE)))
          .addMethod(
            getClearCacheMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.jhcg.pms.api.FileManageProto.ClearCacheRequest,
                org.jhcg.pms.api.FileManageProto.ClearCacheResponse>(
                  this, METHODID_CLEAR_CACHE)))
          .build();
    }
  }

  /**
   * <pre>
   *定义文件管理服务
   * </pre>
   */
  public static final class FileManageServiceStub extends io.grpc.stub.AbstractAsyncStub<FileManageServiceStub> {
    private FileManageServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FileManageServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FileManageServiceStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<org.jhcg.pms.api.FileManageProto.SubmitFileRequest> submitFile(
        io.grpc.stub.StreamObserver<org.jhcg.pms.api.FileManageProto.SubmitFileResponse> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncBidiStreamingCall(
          getChannel().newCall(getSubmitFileMethod(), getCallOptions()), responseObserver);
    }

    /**
     */
    public void clearCache(org.jhcg.pms.api.FileManageProto.ClearCacheRequest request,
        io.grpc.stub.StreamObserver<org.jhcg.pms.api.FileManageProto.ClearCacheResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getClearCacheMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   *定义文件管理服务
   * </pre>
   */
  public static final class FileManageServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<FileManageServiceBlockingStub> {
    private FileManageServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FileManageServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FileManageServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.jhcg.pms.api.FileManageProto.ClearCacheResponse clearCache(org.jhcg.pms.api.FileManageProto.ClearCacheRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getClearCacheMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   *定义文件管理服务
   * </pre>
   */
  public static final class FileManageServiceFutureStub extends io.grpc.stub.AbstractFutureStub<FileManageServiceFutureStub> {
    private FileManageServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FileManageServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FileManageServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.jhcg.pms.api.FileManageProto.ClearCacheResponse> clearCache(
        org.jhcg.pms.api.FileManageProto.ClearCacheRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getClearCacheMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CLEAR_CACHE = 0;
  private static final int METHODID_SUBMIT_FILE = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final FileManageServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(FileManageServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CLEAR_CACHE:
          serviceImpl.clearCache((org.jhcg.pms.api.FileManageProto.ClearCacheRequest) request,
              (io.grpc.stub.StreamObserver<org.jhcg.pms.api.FileManageProto.ClearCacheResponse>) responseObserver);
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
        case METHODID_SUBMIT_FILE:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.submitFile(
              (io.grpc.stub.StreamObserver<org.jhcg.pms.api.FileManageProto.SubmitFileResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class FileManageServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    FileManageServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.jhcg.pms.api.FileManageProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("FileManageService");
    }
  }

  private static final class FileManageServiceFileDescriptorSupplier
      extends FileManageServiceBaseDescriptorSupplier {
    FileManageServiceFileDescriptorSupplier() {}
  }

  private static final class FileManageServiceMethodDescriptorSupplier
      extends FileManageServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    FileManageServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (FileManageServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new FileManageServiceFileDescriptorSupplier())
              .addMethod(getSubmitFileMethod())
              .addMethod(getClearCacheMethod())
              .build();
        }
      }
    }
    return result;
  }
}
