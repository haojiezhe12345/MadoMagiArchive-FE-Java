package love.madohomu.madomagiarchive_fe_java;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.function.BiConsumer;

public class Utils {
    public interface QuadConsumer<T, U, V, W> {
        void accept(T t, U u, V v, W w);
    }

    public interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }

    public static void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (IOException ignored) { }
    }

    public static List<File> chooseOpenFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        return fileChooser.showOpenMultipleDialog(MainApplication.primaryStage);
    }

    public static String chooseSaveDir() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = chooser.showDialog(MainApplication.primaryStage);
        return file != null ? file.getAbsolutePath() : null;
    }

    public static String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex >= 0) {
            return filename.substring(dotIndex);
        }
        return "";
    }

    public static void showAlert(String title, String text, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(text);
        alert.show();
    }

    public static void showAlert(String title, String text) {
        showAlert(title, text, Alert.AlertType.INFORMATION);
    }

    public static void showAlert(String text, Alert.AlertType alertType) {
        showAlert("", text, alertType);
    }

    public static void showAlert(String text) {
        showAlert("", text, Alert.AlertType.INFORMATION);
    }

    public static <T> FXMLLoader createLoaderFromFXML(Class<T> tClass) {
        String fxmlPath = tClass.getSimpleName() + ".fxml";
        FXMLLoader loader = new FXMLLoader(tClass.getResource(fxmlPath));
        try {
            loader.load();
        } catch (Exception e) {
            System.err.println("Could not load FXML file: " + fxmlPath);
            throw new RuntimeException(e);
        }
        return loader;
    }

    public static <T> T createControllerFromFXML(Class<T> tClass) {
        return createLoaderFromFXML(tClass).getController();
    }

    public static <T> void createControllerFromFXML(Class<T> tClass, BiConsumer<FXMLLoader, T> callback) {
        FXMLLoader loader = createLoaderFromFXML(tClass);
        callback.accept(loader, loader.getController());
    }

    public static <T> void createSceneFromFXML(Class<T> tClass, TriConsumer<FXMLLoader, Scene, T> callback) {
        createControllerFromFXML(tClass, (loader, controller) -> {
            Scene scene = new Scene(loader.getRoot());
            callback.accept(loader, scene, controller);
        });
    }

    public static <T> void createStageFromFXML(Class<T> tClass, QuadConsumer<FXMLLoader, Stage, Scene, T> callback) {
        createSceneFromFXML(tClass, (loader, scene, controller) -> {
            Stage stage = new Stage();
            stage.setScene(scene);
            callback.accept(loader, stage, scene, controller);
        });
    }

    public static String DefaultIfNullOrEmpty(String str, String defaultValue) {
        return (str == null || str.isEmpty()) ? defaultValue : str;
    }

    public static boolean FalseIfNull(Boolean b) {
        return (b == null) ? false : b;
    }

    public static double getScreenWidth() {
        return Screen.getPrimary().getBounds().getWidth();
    }

    public static double getScreenHeight() {
        return Screen.getPrimary().getBounds().getHeight();
    }
}
