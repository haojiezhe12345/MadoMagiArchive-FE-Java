package love.madohomu.madomagiarchive_fe_java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    public static Stage primaryStage;
    public static Scene primaryScene;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("Main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryScene = scene;

        MainController mainController = fxmlLoader.getController();
        mainController.afterInit();

        stage.setTitle("Archive of Madoka Magica");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}
