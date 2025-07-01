package org.jhcg.pms.client;

import junit.framework.TestCase;
import org.jhcg.pms.api.ModelCallProto;

/**
 * @author Charles
 * @since JDK
 */
public class MWByPIMTest extends TestCase {

    public String mgfName = "pesticides.mgf";
    public String mspName = "multiline_semicolon.msp";
    public String bigMgfName = "ALL_GNPS_NO_PROPOGATED.mgf";
    public String nistName = "1-10.msp";
    public void testRun() {
        ModelService.call(new MWByPIM(), "127.0.0.1", 5577, nistName, new CallableObserver<ModelCallProto.MWByPIMResponse>() {
            @Override
            public void onNext(ModelCallProto.MWByPIMResponse value) {
                System.out.printf("%d %f\n",value.getId(),value.getResult().getMw());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }

}
