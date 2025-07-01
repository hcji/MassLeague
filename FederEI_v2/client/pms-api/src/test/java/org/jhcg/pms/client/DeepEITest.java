package org.jhcg.pms.client;

import junit.framework.TestCase;
import org.jhcg.pms.api.ModelCallProto;

/**
 * @author Charles
 * @since JDK
 */
public class DeepEITest extends TestCase {
    public String mgfName = "pesticides.mgf";
    public String mspName = "multiline_semicolon.msp";
    public String bigMgfName = "ALL_GNPS_NO_PROPOGATED.mgf";
    public String nistName = "1-10.msp";

    public void testRun() {
        ModelService.call(new DeepEI(), "127.0.0.1", 5577, nistName, new CallableObserver<ModelCallProto.DeepEIResponse>() {
            @Override
            public void onNext(ModelCallProto.DeepEIResponse value) {
                System.out.println(value.getId());
                int i = 0;
                for (ModelCallProto.DeepEIResponse.Candidate candidate : value.getResult().getCandidatesList()) {
                    System.out.println(i++);
                    System.out.println(candidate.getSmiles());
                    System.out.println(candidate.getScore());
                }
                System.out.println("--------------------------------------------");
//                for (Float fingerprint : value.getResult().getFingerprintList()) {
//                    System.out.println(fingerprint);
//                }

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