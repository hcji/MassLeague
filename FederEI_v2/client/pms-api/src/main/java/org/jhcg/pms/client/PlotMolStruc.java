package org.jhcg.pms.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.jhcg.pms.api.ModelCallProto;
import org.jhcg.pms.api.ModelCallServiceGrpc;

/**
 * @author Charles
 * @since JDK
 */
public class PlotMolStruc implements ModelCallableFromSmi {
    /**
     * 进行mw by pim调用
     *
     * @param ip          服务器ip
     * @param port        服务端口号
     * @param smiles          质核比
     */
    public byte[] run(String ip, int port, String smiles) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(ip, port).maxInboundMessageSize(50 * 1024 * 1024).usePlaintext().build();
        try {

            //1.获取代理
            ModelCallServiceGrpc.ModelCallServiceBlockingStub modelCallServiceStub = ModelCallServiceGrpc.newBlockingStub(managedChannel);
            //2.创建请求参数
            ModelCallProto.PlotMolStrucRequest plotMolStrucRequest = ModelCallProto.PlotMolStrucRequest.newBuilder().setSmiles(smiles).build();

            //3.进行rpc调用
            ModelCallProto.PlotMolStrucResponse plotMolStrucResponse = modelCallServiceStub.plotMolStruc(plotMolStrucRequest);
            byte[] data = plotMolStrucResponse.getData().toByteArray();
            //4.返回完成
            System.out.println("plotMolStruc调用结束! pid:" + ProcessHandle.current().pid());
            return data;
        } finally {
            if (managedChannel != null) {
                managedChannel.shutdown();
            }
        }
    }
}
