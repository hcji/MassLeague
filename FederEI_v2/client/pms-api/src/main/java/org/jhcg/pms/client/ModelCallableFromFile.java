package org.jhcg.pms.client;

/**
 * Server Stream
 *
 * @author Charles
 * @since JDK
 */
public interface ModelCallableFromFile {
    void run(String ip, int port, String fileName, CallableObserver callableObserver);

}
