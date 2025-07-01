package org.jhcg.pms.ui.sence;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Charles
 * @since JDK
 */
public class MainSence {
    private static String currentFileName;
    private static int currentID;
    private static TabPane resultTabPane = null;

    public static TabPane getResultTabPane() {
        return resultTabPane;
    }

    public static void setResultTabPane(TabPane resultTabPane) {
        MainSence.resultTabPane = resultTabPane;
    }

    public static String getCurrentFileName() {
        return currentFileName;
    }

    public static void setCurrentFileName(String currentFile) {
        MainSence.currentFileName = currentFile;
    }

    public static int getCurrentID() {
        return currentID;
    }

    public static void setCurrentID(int currentID) {
        MainSence.currentID = currentID;
    }

    public static void load(Stage stage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(MainSence.class.getResource("/fxml/Main.fxml")));
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
