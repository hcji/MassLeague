package org.jhcg.pms.ui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author Charles
 * @since JDK
 */
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        Director.getInstance().init(primaryStage);
    }
}
