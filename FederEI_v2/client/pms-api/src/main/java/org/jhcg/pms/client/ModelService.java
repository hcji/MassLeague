package org.jhcg.pms.client;

/**
 * 提供服务给用户
 *
 * @author Charles
 * @since JDK
 */
public class ModelService {
    /**
     * 通过文件调用对应服务
     *
     * @param model            模型
     * @param ip               服务器ip
     * @param port             服务端口号
     * @param fileName         文件名
     * @param callableObserver 可调用模型的观察者
     */
    public static void call(ModelCallableFromFile model, String ip, int port, String fileName, CallableObserver<?> callableObserver) {
        model.run(ip, port, fileName, callableObserver);
    }

    /**
     * 通过Smi调用对应服务
     *
     * @return byte[]
     */
    public static byte[] call(ModelCallableFromSmi model, String ip, int port, String smiles) {
        return model.run(ip, port, smiles);
    }

    public static byte[] call(PlotMS model, String ip, int port, byte[] mz, byte[] intensities) {
        return model.run(ip, port, mz, intensities);
    }

    public static byte[] call(PlotMSAgainst model, String ip, int port, byte[] origMz, byte[] origIntensities, int candiIndex) {
        return model.run(ip, port, origMz, origIntensities, candiIndex);
    }

}
