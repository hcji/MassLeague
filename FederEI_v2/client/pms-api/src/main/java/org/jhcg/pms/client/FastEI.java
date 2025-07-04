package org.jhcg.pms.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.jhcg.pms.api.ModelCallProto;
import org.jhcg.pms.api.ModelCallServiceGrpc;

import java.util.concurrent.CountDownLatch;

/**
 * @author Charles
 * @since JDK
 */
public class FastEI implements ModelCallableFromFile {

    /**
     * 调用fastEI
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
            ModelCallProto.FastEIRequest request = ModelCallProto.FastEIRequest.newBuilder().setFileName(fileName).build();
            //3.Make an RPC call
            modelCallServiceStub.fastEI(request, new StreamObserver<ModelCallProto.FastEIResponse>() {
                @Override
                public void onNext(ModelCallProto.FastEIResponse value) {
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
                    //5.Release the latch when the upload is completed.
                    countDownLatch.countDown();
                }

            });
            //4.The main thread waits for the server to return the completion.
            countDownLatch.await();
            System.out.println("fastEI call has ended! pid:" + ProcessHandle.current().pid());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (managedChannel != null) {
                managedChannel.shutdown();
            }
        }
    }
}
