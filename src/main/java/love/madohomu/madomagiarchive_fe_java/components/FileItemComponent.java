package love.madohomu.madomagiarchive_fe_java.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import love.madohomu.madomagiarchive_fe_java.MainController;
import love.madohomu.madomagiarchive_fe_java.models.FileItem;
import love.madohomu.madomagiarchive_fe_java.net.ApiClient;

import java.io.IOException;

public class FileItemComponent {
    @FXML
    private HBox root;

    @FXML
    private ImageView image;

    private FileItem fileItem;
    private MainController parent;
    private boolean selected = false;

    public static FileItemComponent createInstance(FileItem fileItem, MainController parent) {
        FXMLLoader loader = new FXMLLoader(FileItemComponent.class.getResource("FileItem.fxml"));

        FileItemComponent instance;
        try {
            loader.load();
            instance = loader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        instance.setFileItem(fileItem);
        instance.parent = parent;
        return instance;
    }

    public HBox getNode() {
        return root;
    }

    public FileItem getFileItem() {
        return fileItem;
    }

    private void setFileItem(FileItem fileItem) {
        this.fileItem = fileItem;

        image.setImage(new Image("%s/files/%d/thumb".formatted(ApiClient.BaseUrl, fileItem.id)));
        image.setFitHeight(200);
        image.setPreserveRatio(true);
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            if (!root.getStyleClass().contains("selected")) {
                root.getStyleClass().add("selected");
            }
        } else {
            root.getStyleClass().remove("selected");
        }
    }

    @FXML
    private void onClick(MouseEvent e) {
        if (e.getButton() == MouseButton.SECONDARY && selected) {
            return;
        }
        if (parent.getMultiSelect()) {
            setSelected(!selected);
        } else {
            parent.deselectFiles();
            setSelected(true);
        }
    }
}
