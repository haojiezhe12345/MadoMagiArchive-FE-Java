package love.madohomu.madomagiarchive_fe_java.components;

import javafx.fxml.FXML;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import love.madohomu.madomagiarchive_fe_java.MainController;
import love.madohomu.madomagiarchive_fe_java.Utils;
import love.madohomu.madomagiarchive_fe_java.models.FileItem;
import love.madohomu.madomagiarchive_fe_java.net.ApiClient;


public class FileItemComponent {
    @FXML
    private HBox root;

    @FXML
    private ImageView image;

    private FileItem fileItem;
    private MainController parent;
    private boolean selected = false;

    public static FileItemComponent CreateInstance(FileItem fileItem, MainController parent) {
        FileItemComponent instance = Utils.createControllerFromFXML(FileItemComponent.class, "FileItem.fxml");

        instance.parent = parent;
        instance.setFileItem(fileItem);
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
        updateImageHeight();
    }

    public void updateImageHeight() {
        image.setFitHeight(parent.getImageHeight());
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
    private void onMouseDown(MouseEvent e) {
        MouseButton button = e.getButton();

        if (button == MouseButton.SECONDARY && selected) {
            return;
        }

        if (button == MouseButton.PRIMARY && e.getClickCount() == 2) {
            parent.viewFile();
        }

        if (parent.getMultiSelect()) {
            setSelected(!selected);
        } else {
            parent.deselectFiles();
            setSelected(true);
        }
    }
}
