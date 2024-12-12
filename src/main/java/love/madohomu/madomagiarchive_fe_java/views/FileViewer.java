package love.madohomu.madomagiarchive_fe_java.views;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import love.madohomu.madomagiarchive_fe_java.models.FileItem;
import love.madohomu.madomagiarchive_fe_java.net.ApiClient;

import java.io.IOException;

public class FileViewer {
    @FXML
    public HBox root;
    @FXML
    public VBox viewer;
    @FXML
    public ImageView imageViewer;
    @FXML
    public VBox props;

    private Stage stage;
    private Scene scene;
    private FileItem fileItem;

    public static void ViewFile(FileItem fileItem) {
        FXMLLoader loader = new FXMLLoader(FileViewer.class.getResource("FileViewer.fxml"));
        Stage stage = new Stage();
        Scene scene;
        FileViewer fileViewer;

        try {
            scene = new Scene(loader.load());
            fileViewer = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        fileViewer.stage = stage;
        fileViewer.scene = scene;
        fileViewer.fileItem = fileItem;
        fileViewer.afterInit();

        stage.setTitle("Viewing file " + fileItem.id);
        stage.setScene(scene);
        stage.show();

        fileViewer.afterShow();
    }

    public void afterInit() {
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ESCAPE:
                    close();
                    break;
            }
        });

        imageViewer.setFitWidth(fileItem.width);
        imageViewer.setFitHeight(fileItem.height);
    }

    public void afterShow() {
        showFile(fileItem);

        imageViewer.fitWidthProperty().bind(Bindings.subtract(root.widthProperty(), props.prefWidthProperty()));
        imageViewer.fitHeightProperty().bind(root.heightProperty());
    }

    private void showFile(FileItem fileItem) {
        imageViewer.setImage(new Image("%s/files/%d".formatted(ApiClient.BaseUrl, fileItem.id)));
    }

    public void close() {
        stage.close();
    }
}
