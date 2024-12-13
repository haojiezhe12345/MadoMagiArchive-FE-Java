package love.madohomu.madomagiarchive_fe_java;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainApplication extends Application {
    public static Stage primaryStage;
    public static Scene primaryScene;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        Utils.createSceneFromFXML(MainController.class, "Main.fxml", (loader, scene, controller) -> {
            primaryScene = scene;
            controller.afterInit();

            stage.setTitle("Archive of Madoka Magica");
            stage.setScene(scene);
            stage.show();
        });
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}
