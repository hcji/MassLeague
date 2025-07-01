package org.jhcg.pms.client;

import junit.framework.TestCase;
import org.jhcg.pms.api.ModelCallProto;

/**
 * @author Charles
 * @since JDK
 */
public class FastEITest extends TestCase {
    public String mgfName = "pesticides.mgf";
    public String mspName = "multiline_semicolon.msp";
    public String bigMgfName = "ALL_GNPS_NO_PROPOGATED.mgf";
    public String nistName = "1-10.msp";

    public void testRun() {
        ModelService.call(new FastEI(), "127.0.0.1", 5577, mspName, new CallableObserver<ModelCallProto.FastEIResponse>() {

            @Override
            public void onNext(ModelCallProto.FastEIResponse value) {
                System.out.println(value.getId());
                for (ModelCallProto.FastEIResponse.MatchedItem matchedItem : value.getResult().getMatchedItemsList()){
                    System.out.println(matchedItem.getSmiles());
                    System.out.println(matchedItem.getDistance());
                }
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