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
public class MWByPIM implements ModelCallableFromFile {
    /**
     * Make an MWByPIM call
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
            //2.Create request parameters
            ModelCallProto.MWByPIMRequest mwByPIMRequest = ModelCallProto.MWByPIMRequest.newBuilder().setFileName(fileName).build();

            //3.Make an RPC call
            modelCallServiceStub.mwByPIM(mwByPIMRequest, new StreamObserver<ModelCallProto.MWByPIMResponse>() {

                @Override
                public void onNext(ModelCallProto.MWByPIMResponse value) {
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
                    countDownLatch.countDown();
                }

            });
            //4.The main thread waits for the server to return the completion.
            countDownLatch.await();
            System.out.println("The mwByPIM call has ended! pid:" + ProcessHandle.current().pid());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (managedChannel != null) {
                managedChannel.shutdown();
            }
        }
    }
}
