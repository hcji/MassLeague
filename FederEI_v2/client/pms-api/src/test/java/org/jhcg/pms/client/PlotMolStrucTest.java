package org.jhcg.pms.client;

import com.google.protobuf.ByteString;
import junit.framework.TestCase;
import org.jhcg.pms.api.ModelCallProto;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Charles
 * @since JDK
 */
public class PlotMolStrucTest extends TestCase {
    public String mgfName = "pesticides.mgf";
    public String mspName = "multiline_semicolon.msp";
    public String bigMgfName = "ALL_GNPS_NO_PROPOGATED.mgf";
    public String nistName = "1-10.msp";
    public String cache_image = "D:/changsihao/workspace_idea/pms-parent/pms-api/src/main/resources/cache_image/";

    public String smiles ;

    public void testRun() {

        byte[] bytes = ModelService.call(new PlotMolStruc(), "127.0.0.1", 5577,smiles);
    }
}