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
     * Make an RPC call and submit the file to the server.
     *
     * @param ip               server ip
     * @param port             server port
     * @param path             file path
     * @param callableObserver The observable of the callable model
     */
    public static void submitFile(String ip, int port, String path, CallableObserver<FileManageProto.SubmitFileResponse> callableObserver) {
        FileInputStream fis = null;
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(ip, port).maxInboundMessageSize(50 * 1024 * 1024).usePlaintext().build();
        try {
            //0.Create a CountDownLatch to wait for the upload to complete
            CountDownLatch countDownLatch = new CountDownLatch(1);
            //1.Obtain the RPC proxy
            FileManageServiceGrpc.FileManageServiceStub fileManageServiceStub = FileManageServiceGrpc.newStub(managedChannel);
            //2.Make an RPC call
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
                    //Release after upload is completed
                    countDownLatch.countDown();
                }
            });


            File msFile = new File(path);

            if (msFile.exists() && msFile.isFile()) {
                //Transfer file name
                FileManageProto.SubmitFileRequest submitFileRequest = FileManageProto.SubmitFileRequest.newBuilder().setName(msFile.getName()).build();
                submitFileRequestStreamObserver.onNext(submitFileRequest);
                //Transmit the content of the file
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
                throw new RuntimeException("file does not exist!");
            }


            //The main thread waits for the server to return the completion.
            countDownLatch.await();
            System.out.println("SubmitFile has been called successfully! pid:" + ProcessHandle.current().pid());
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
        //1.create the communication pipeline
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(ip, port).usePlaintext().build();
        try {
            //2.Obtain the stub of the proxy object
            FileManageServiceGrpc.FileManageServiceBlockingStub fileManageServiceBlockingStub = FileManageServiceGrpc.newBlockingStub(managedChannel);
            //3.Complete the RPC call
            //3.1Prepare parameters
            FileManageProto.ClearCacheRequest clearCacheRequest = FileManageProto.ClearCacheRequest.newBuilder().setDesc("clear cache").build();
            //3.2Make an RPC call to obtain the corresponding content
            FileManageProto.ClearCacheResponse clearCacheResponse = fileManageServiceBlockingStub.clearCache(clearCacheRequest);
            System.out.println(clearCacheResponse.getDesc());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            managedChannel.shutdown();
        }
    }
}
