package org.jhcg.pms.client;

import junit.framework.TestCase;
import org.jhcg.pms.api.FileManageProto;

/**
 * @author Charles
 * @since JDK
 */
public class FileManagerTest extends TestCase {

    public String mgfPath = "D:/changsihao/workspace_idea/pms-parent/pms-api/src/main/resources/test_file/pesticides.mgf";
    public String mgf1_10Path = "D:/changsihao/workspace_idea/pms-parent/pms-api/src/main/resources/test_file/1-10.mgf";
    public String msp1_10Path = "D:/changsihao/workspace_idea/pms-parent/pms-api/src/main/resources/test_file/1-10.msp";
    public String mspPath = "D:/changsihao/workspace_idea/pms-parent/pms-api/src/main/resources/test_file/multiline_semicolon.msp";
    public String bigMgfPath = "D:/changsihao/info_work/ms/data/ALL_MSDatabase/GNPS_all/ALL_GNPS_NO_PROPOGATED.mgf";
    public String nistPath = "D:/changsihao/workspace_idea/pms-parent/pms-api/src/main/resources/test_file/1-10.msp";

    public void testSubmitFile() {
        FileManager.submitFile("127.0.0.1", 5577,nistPath, new CallableObserver<FileManageProto.SubmitFileResponse>() {
            @Override
            public void onNext(FileManageProto.SubmitFileResponse value) {
                System.out.printf("id:%d\ntitle:%s\nfile_name:%s\nmw:%f\ninchikey:%s\n"
                        , value.getResult().getId()
                        ,value.getResult().getTitle()
                        ,value.getResult().getFileName()
                        ,value.getResult().getMw()
                        ,value.getResult().getInchikey());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.println("completed");
            }
        });
    }
}