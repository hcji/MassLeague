package org.jhcg.pms.client;

/**
 * Provide services to users
 *
 * @author Charles
 * @since JDK
 */
public class ModelService {
    /**
     * Invoke the corresponding service through the file.
     *
     * @param model            model
     * @param ip               server ip
     * @param port             server port
     * @param fileName         file name
     * @param callableObserver The observable of the callable model
     */
    public static void call(ModelCallableFromFile model, String ip, int port, String fileName, CallableObserver<?> callableObserver) {
        model.run(ip, port, fileName, callableObserver);
    }

    /**
     * Invoke the corresponding service through Smi
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
