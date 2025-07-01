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
     * @param ip               服务器ip
     * @param port             服务端口号
     * @param fileName         文件名
     * @param callableObserver 可调用模型的观察者
     */
    @Override
    public void run(String ip, int port, String fileName, CallableObserver callableObserver) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(ip, port).maxInboundMessageSize(50 * 1024 * 1024).usePlaintext().build();
        try {
            //0.创建CountDownLatch对象，用于非阻塞式rpc的线程同步
            CountDownLatch countDownLatch = new CountDownLatch(1);
            //1.获取代理
            ModelCallServiceGrpc.ModelCallServiceStub modelCallServiceStub = ModelCallServiceGrpc.newStub(managedChannel);
            //2.准备参数
            ModelCallProto.GetCandidateMessageRequest request = ModelCallProto.GetCandidateMessageRequest.newBuilder().setFileName(fileName).build();
            //3.进行rpc调用
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
                    //5.上传完成时释放 latch
                    System.out.println("getCandidateMessage调用结束!");
                    countDownLatch.countDown();
                }
            });

            //4.主线程等待服务器端返回完成
            countDownLatch.await();
            System.out.println("getCandidateMessage调用结束! pid:" + ProcessHandle.current().pid());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally{
            if (managedChannel != null) {
                managedChannel.shutdown();
            }
        }
    }
}
