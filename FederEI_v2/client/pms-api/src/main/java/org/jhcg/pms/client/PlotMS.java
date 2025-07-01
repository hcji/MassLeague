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
public class PlotMS {
    /**
     * 进行mw by pim调用
     *
     * @param ip          服务器ip
     * @param port        服务端口号
     * @param mz          质核比
     * @param intensities 强度
     */
    public byte[] run(String ip, int port, byte[] mz, byte[] intensities) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(ip, port).maxInboundMessageSize(50 * 1024 * 1024).usePlaintext().build();
        try {

            //1.获取代理
            ModelCallServiceGrpc.ModelCallServiceBlockingStub modelCallServiceStub = ModelCallServiceGrpc.newBlockingStub(managedChannel);
            //2.创建请求参数
            ModelCallProto.PlotMSRequest plotMSRequest = ModelCallProto.PlotMSRequest.newBuilder().setMz(ByteString.copyFrom(mz)).setIntensities(ByteString.copyFrom(intensities)).build();

            //3.进行rpc调用
            ModelCallProto.PlotMSResponse plotMSResponse = modelCallServiceStub.plotMS(plotMSRequest);
            byte[] data = plotMSResponse.getData().toByteArray();
            //4.返回完成
            System.out.println("plotMS调用结束! pid:" + ProcessHandle.current().pid());
            return data;
        } finally {
            if (managedChannel != null) {
                managedChannel.shutdown();
            }
        }
    }
}
