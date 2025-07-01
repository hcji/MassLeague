package org.jhcg.pms.client;

import junit.framework.TestCase;

/**
 * @author Charles
 * @since JDK
 */
public class PlotMSAgainstTest extends TestCase {

    public byte[] origMz;
    public byte[] origIntensities;
    int candiIndex;

    public void testRun() {

        byte[] bytes = ModelService.call(new PlotMSAgainst(), "127.0.0.1", 5577, origMz, origIntensities, candiIndex);
    }
}