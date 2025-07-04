package org.jhcg.pms.ui;


import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.jhcg.pms.ui.sence.MainSence;

/**
 * @author Charles
 * @since JDK
 */
public class Director {
    public static final double WIDTH = 1600, HEIGHT = 900;//Main scene width and height
    private static volatile Director instance;//Director singleton class
    private Stage primaryStage;//The main window of the JavaFX application
    MainSence mainSence = new MainSence();

    private Director() {
    }

    public static Director getInstance() {
        if (instance == null) {
            synchronized (Director.class) {
                if (instance == null) {
                    instance = new Director();
                }
            }
        }
        return instance;
    }

    /**
     * Initialization: Director class and the main window of the JavaFX application
     *
     * @param primaryStage The main window of the JavaFX application
     */
    public void init(Stage primaryStage) {
        this.primaryStage = primaryStage;
        AnchorPane root = new AnchorPane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        //Set the properties of the main window
        this.primaryStage.setTitle("pms");
        this.primaryStage.getIcons().add(new Image("image/logo.png"));
        this.primaryStage.setResizable(false);
        this.primaryStage.setScene(scene);
        loadMainScene();
        //Show the main window
        this.primaryStage.show();

    }

    private void loadMainScene() {
        mainSence.load(primaryStage);
    }
}
