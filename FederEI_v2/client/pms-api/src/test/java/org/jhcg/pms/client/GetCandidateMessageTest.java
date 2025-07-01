package org.jhcg.pms.client;

import junit.framework.TestCase;
import org.jhcg.pms.api.ModelCallProto;

/**
 * @author Charles
 * @since JDK
 */
public class GetCandidateMessageTest extends TestCase {
    public String mgfName = "pesticides.mgf";
    public String mspName = "multiline_semicolon.msp";
    public String bigMgfName = "ALL_GNPS_NO_PROPOGATED.mgf";
    public String nistName = "1-10.msp";

    public void testTestRun() {
        ModelService.call(new GetCandidateMessage(), "127.0.0.1", 5577,nistName, new CallableObserver<ModelCallProto.GetCandidateMessageResponse>() {
            @Override
            public void onNext(ModelCallProto.GetCandidateMessageResponse value) {
//                System.out.println("====================");
//                System.out.println("file_name:" + value.getFileName());
//                System.out.println("id:" + value.getId());
//                for (ModelCallProto.GetCandidateMessageResponse.Candidate candidate : value.getCandidatesList()) {
//                    System.out.println("inchikey:" + candidate.getInchikey());
//                    System.out.println("smiles:" + candidate.getSmiles());
//                    System.out.println("rank:" + candidate.getRank());
//                    System.out.println("distance:" + candidate.getDistance());
//                    System.out.println("mw:" + candidate.getMw());
//                    System.out.println("mz:" + candidate.getMz());
//                    System.out.println("intensities:" + candidate.getIntensities());
////                    System.out.println("mol_struc:" + candidate.getMolStruc());
////                    System.out.println("against_ms:" + candidate.getAgainstMS());
//                }
//                System.out.println("====================");

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