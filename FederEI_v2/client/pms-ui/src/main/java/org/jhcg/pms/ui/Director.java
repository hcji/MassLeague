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
    public static final double WIDTH = 1600, HEIGHT = 900;//主场景宽高
    private static volatile Director instance;//导演类单例
    private Stage primaryStage;//javafx应用主窗口
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
     * 初始化：导演类和javafx应用的主窗口
     *
     * @param primaryStage javafx应用的主窗口
     */
    public void init(Stage primaryStage) {
        this.primaryStage = primaryStage;
        AnchorPane root = new AnchorPane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        //设置主窗口属性
        this.primaryStage.setTitle("pms");
        this.primaryStage.getIcons().add(new Image("image/logo.png"));
        this.primaryStage.setResizable(false);
        this.primaryStage.setScene(scene);
        loadMainScene();
        //显示主窗口
        this.primaryStage.show();

    }

    private void loadMainScene() {
        mainSence.load(primaryStage);
    }
}
