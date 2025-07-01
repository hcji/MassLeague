package org.jhcg.pms.util;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Charles
 * @since JDK
 */
public class testJavafx extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {


        try {
            URL url = getClass().getResource("/image/logo.png");
            File file = new File(url.toURI());

            FileInputStream fileInputStream = new FileInputStream(file);
            System.out.println(fileInputStream.available());
            byte[] b = new byte[fileInputStream.available()];
            fileInputStream.read(b);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(b);
            ImageView imageView = new ImageView(new Image(byteArrayInputStream));

            AnchorPane pane = new AnchorPane();
            pane.getChildren().add(imageView);
            Scene scene = new Scene(pane);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }


    }
}
