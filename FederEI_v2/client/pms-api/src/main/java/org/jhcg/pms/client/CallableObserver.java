package org.jhcg.pms.client;

/**
 * @author Charles
 * @since JDK
 */
public interface CallableObserver<V> {
    void onNext(V value);


    void onError(Throwable t);


    void onCompleted();
}
