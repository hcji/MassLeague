package org.jhcg.pms.client;

import com.google.protobuf.ByteString;
import junit.framework.TestCase;
import org.jhcg.pms.api.ModelCallProto;

import java.io.*;


/**
 * @author Charles
 * @since JDK
 */
public class PlotMSTest extends TestCase {
    public String mgfName = "pesticides.mgf";
    public String mspName = "multiline_semicolon.msp";
    public String bigMgfName = "ALL_GNPS_NO_PROPOGATED.mgf";
    public String nistName = "1-10.msp";
    public String cache_image = "D:/changsihao/workspace_idea/pms-parent/pms-api/src/main/resources/cache_image/";

    public byte[] mz ;
    public byte[] intensities ;

    public void testRun() {

        byte[] bytes = ModelService.call(new PlotMS(), "127.0.0.1", 5577, mz, intensities);
    }

}