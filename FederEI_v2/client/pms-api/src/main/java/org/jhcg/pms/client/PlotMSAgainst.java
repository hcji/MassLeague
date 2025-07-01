package org.jhcg.pms.client;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.jhcg.pms.api.ModelCallProto;
import org.jhcg.pms.api.ModelCallServiceGrpc;

/**
 * @author Charles
 * @since JDK
 */
public class PlotMSAgainst {
    public byte[] run(String ip, int port, byte[] origMz, byte[] origIntensities, int candiIndex) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(ip, port).maxInboundMessageSize(50 * 1024 * 1024).usePlaintext().build();
        try {
            //1.获取代理
            ModelCallServiceGrpc.ModelCallServiceBlockingStub modelCallServiceStub = ModelCallServiceGrpc.newBlockingStub(managedChannel);
            //2.创建请求参数
            ModelCallProto.PlotMSAgainstRequest plotMSAgainstRequest = ModelCallProto.PlotMSAgainstRequest.newBuilder()
                    .setOrigMz(ByteString.copyFrom(origMz))
                    .setOrigIntensities(ByteString.copyFrom(origIntensities))
                    .setCandiIndex(candiIndex)
                    .build();

            //3.进行rpc调用
            ModelCallProto.PlotMSAgainstResponse plotMSAgainstResponse = modelCallServiceStub.plotMSAgainst(plotMSAgainstRequest);
            byte[] data = plotMSAgainstResponse.getData().toByteArray();
            //4.返回完成
            System.out.println("plotMSAgainst调用结束! pid:" + ProcessHandle.current().pid());
            return data;
        } finally {
            if (managedChannel != null) {
                managedChannel.shutdown();
            }
        }
    }
}
