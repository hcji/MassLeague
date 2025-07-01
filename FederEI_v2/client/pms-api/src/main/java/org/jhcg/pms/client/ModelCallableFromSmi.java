package org.jhcg.pms.client;

/**
 * @author Charles
 * @since JDK
 */
public interface ModelCallableFromSmi {
    byte[] run(String ip, int port, String smiles);
}
