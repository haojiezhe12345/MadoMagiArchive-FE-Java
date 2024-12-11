package love.madohomu.madomagiarchive_fe_java;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

public class Utils {
    public static void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (IOException ignored) {
        }
    }

    public static List<File> chooseOpenFile() {
        FileChooser fileChooser = new FileChooser();
        return fileChooser.showOpenMultipleDialog(MainApplication.primaryStage);
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
}
