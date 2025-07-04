package org.jhcg.pms.client;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.jhcg.pms.api.ModelCallProto;
import org.jhcg.pms.api.ModelCallServiceGrpc;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Charles
 * @since JDK
 */
public class GetCandidateMessage implements ModelCallableFromFile {
    /**
     * 调用GetCandidateMessage
     *
     * @param ip               server ip
     * @param port             server port
     * @param fileName         file name
     * @param callableObserver The observable of the callable model
     */
    @Override
    public void run(String ip, int port, String fileName, CallableObserver callableObserver) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(ip, port).maxInboundMessageSize(50 * 1024 * 1024).usePlaintext().build();
        try {
            //0.Create a CountDownLatch object for thread synchronization in non-blocking RPC.
            CountDownLatch countDownLatch = new CountDownLatch(1);
            //1.Obtain an agent
            ModelCallServiceGrpc.ModelCallServiceStub modelCallServiceStub = ModelCallServiceGrpc.newStub(managedChannel);
            //2.Prepare parameters
            ModelCallProto.GetCandidateMessageRequest request = ModelCallProto.GetCandidateMessageRequest.newBuilder().setFileName(fileName).build();
            //3.Make an RPC call
            modelCallServiceStub.getCandidateMessage(request, new StreamObserver<ModelCallProto.GetCandidateMessageResponse>() {
                @Override
                public void onNext(ModelCallProto.GetCandidateMessageResponse value) {
                    callableObserver.onNext(value);
                }

                @Override
                public void onError(Throwable t) {
                    t.printStackTrace();
                    countDownLatch.countDown();
                }

                @Override
                public void onCompleted() {
                    callableObserver.onCompleted();
                    //5.Release after upload is completed
                    System.out.println("The getCandidateMessage call has ended.!");
                    countDownLatch.countDown();
                }
            });

            //4.主线程等待服务器端返回完成
            countDownLatch.await();
            System.out.println("The getCandidateMessage call has ended! pid:" + ProcessHandle.current().pid());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally{
            if (managedChannel != null) {
                managedChannel.shutdown();
            }
        }
    }
}
