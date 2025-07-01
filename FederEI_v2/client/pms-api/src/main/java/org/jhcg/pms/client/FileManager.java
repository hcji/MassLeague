package org.jhcg.pms.client;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.jhcg.pms.api.FileManageServiceGrpc;
import org.jhcg.pms.api.FileManageProto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author Charles
 * @since JDK
 */
public class FileManager {

    /**
     * 进行rpc调用，提交文件到服务端
     *
     * @param ip               服务器ip
     * @param port             服务端口号
     * @param path             文件路径
     * @param callableObserver 可调用模型的观察者
     */
    public static void submitFile(String ip, int port, String path, CallableObserver<FileManageProto.SubmitFileResponse> callableObserver) {
        FileInputStream fis = null;
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(ip, port).maxInboundMessageSize(50 * 1024 * 1024).usePlaintext().build();
        try {
            //0.创建 CountDownLatch 用于等待上传完成
            CountDownLatch countDownLatch = new CountDownLatch(1);
            //1.获取rpc代理
            FileManageServiceGrpc.FileManageServiceStub fileManageServiceStub = FileManageServiceGrpc.newStub(managedChannel);
            //2.进行rpc调用
            StreamObserver<FileManageProto.SubmitFileRequest> submitFileRequestStreamObserver = fileManageServiceStub.submitFile(new StreamObserver<FileManageProto.SubmitFileResponse>() {
                @Override
                public void onNext(FileManageProto.SubmitFileResponse value) {
                    callableObserver.onNext(value);
                }

                @Override
                public void onError(Throwable throwable) {
                    throwable.printStackTrace();
                    countDownLatch.countDown();
                }

                @Override
                public void onCompleted() {
                    callableObserver.onCompleted();
                    //上传完成时释放 latch
                    countDownLatch.countDown();
                }
            });


            File msFile = new File(path);

            if (msFile.exists() && msFile.isFile()) {
                //传输文件名
                FileManageProto.SubmitFileRequest submitFileRequest = FileManageProto.SubmitFileRequest.newBuilder().setName(msFile.getName()).build();
                submitFileRequestStreamObserver.onNext(submitFileRequest);
                //传输文件内容
                fis = new FileInputStream(msFile);
                byte[] buffer = new byte[1024 * 1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    ByteString chunk = ByteString.copyFrom(buffer, 0, len);
                    submitFileRequest = FileManageProto.SubmitFileRequest.newBuilder().setChunk(chunk).build();
                    submitFileRequestStreamObserver.onNext(submitFileRequest);
                }
                submitFileRequestStreamObserver.onCompleted();
            } else {
                throw new RuntimeException("文件不存在！");
            }


            //主线程等待服务器端返回完成
            countDownLatch.await();
            System.out.println("submitFile调用结束! pid:" + ProcessHandle.current().pid());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (managedChannel != null) {
                managedChannel.shutdown();
            }
        }
    }

    public static void clearCache(String ip, int port) {
        //1.创建通信的管道
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(ip, port).usePlaintext().build();
        try {
            //2.获取代理对象stub
            FileManageServiceGrpc.FileManageServiceBlockingStub fileManageServiceBlockingStub = FileManageServiceGrpc.newBlockingStub(managedChannel);
            //3.完成rpc调用
            //3.1准备参数
            FileManageProto.ClearCacheRequest clearCacheRequest = FileManageProto.ClearCacheRequest.newBuilder().setDesc("clear cache").build();
            //3.2进行rpc调用，获取相应的内容
            FileManageProto.ClearCacheResponse clearCacheResponse = fileManageServiceBlockingStub.clearCache(clearCacheRequest);
            System.out.println(clearCacheResponse.getDesc());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            managedChannel.shutdown();
        }
    }
}
