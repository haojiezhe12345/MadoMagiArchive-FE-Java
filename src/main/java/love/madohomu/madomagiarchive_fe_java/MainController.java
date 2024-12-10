package love.madohomu.madomagiarchive_fe_java;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import love.madohomu.madomagiarchive_fe_java.models.FileItem;
import love.madohomu.madomagiarchive_fe_java.net.ApiClient;

public class MainController {
    @FXML
    public ScrollPane fileItemScrollContainer;
    @FXML
    public FlowPane fileItemContainer;

    @FXML
    private TextField mainSearchInput;

    @FXML
    public void initialize() {
        fileItemContainer.prefWrapLengthProperty().bind(Bindings.add(-20, fileItemScrollContainer.widthProperty()));
    }

    @FXML
    protected void onFilesRefreshButtonClicked() {
        ApiClient.getFileList(fileItems -> {
            Platform.runLater(() -> {
                fileItemContainer.getChildren().clear();

                for (FileItem fileItem : fileItems) {
                    ImageView imageView = new ImageView(new Image("%s/files/%d/thumb".formatted(ApiClient.BaseUrl, fileItem.id)));
                    imageView.setFitHeight(200);
                    imageView.setPreserveRatio(true);
                    fileItemContainer.getChildren().add(imageView);
                }
            });
        });
    }
}
