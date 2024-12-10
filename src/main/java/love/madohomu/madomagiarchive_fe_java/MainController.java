package love.madohomu.madomagiarchive_fe_java;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import love.madohomu.madomagiarchive_fe_java.components.FileItemComponent;
import love.madohomu.madomagiarchive_fe_java.models.FileItem;
import love.madohomu.madomagiarchive_fe_java.net.ApiClient;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    @FXML
    private ScrollPane fileItemScrollContainer;
    @FXML
    private FlowPane fileItemContainer;
    @FXML
    private ContextMenu fileItemContentMenu;
    @FXML
    private ContextMenu fileItemBackgroundContentMenu;
    @FXML
    private TextField mainSearchInput;

    private List<FileItemComponent> fileItemComponents = new ArrayList<>();

    @FXML
    public void initialize() {
        fileItemContainer.prefWrapLengthProperty().bind(Bindings.add(-24, fileItemScrollContainer.widthProperty()));
        reloadFiles();
    }

    @FXML
    public void reloadFiles() {
        ApiClient.getFileList(fileItems -> {
            Platform.runLater(() -> {
                fileItemContainer.getChildren().clear();
                fileItemComponents.clear();

                for (FileItem fileItem : fileItems) {
                    FileItemComponent fileItemComponent = FileItemComponent.createInstance(fileItem, this);
                    fileItemContainer.getChildren().add(fileItemComponent.getNode());
                    fileItemComponents.add(fileItemComponent);
                }
            });
        });
    }

    @FXML
    public void selectAllFiles() {
        fileItemComponents.forEach(x -> {
            x.setSelected(true);
        });
    }

    @FXML
    public void deselectFiles() {
        fileItemComponents.forEach(x -> {
            x.setSelected(false);
        });
    }

    @FXML
    public void onFileItemScrollContainerClick(MouseEvent e) {
        if (e.getTarget() == fileItemScrollContainer || e.getTarget() == fileItemContainer) {
            deselectFiles();
            fileItemScrollContainer.setContextMenu(fileItemBackgroundContentMenu);
        } else {
            fileItemScrollContainer.setContextMenu(fileItemContentMenu);
        }
    }

    public List<FileItem> getSelectedFiles() {
        List<FileItem> selectedFiles = new ArrayList<>();
        for (FileItemComponent fileItemComponent : fileItemComponents) {
            if (fileItemComponent.getSelected()) {
                selectedFiles.add(fileItemComponent.getFileItem());
            }
        }
        return selectedFiles;
    }

    @FXML
    public void showDeleteConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm delete");
        alert.setHeaderText("Delete the selected %d file(s)?".formatted(getSelectedFiles().size()));
        alert.show();
    }

    @FXML
    public void gotoWebsite() {
        try {
            Desktop.getDesktop().browse(URI.create("https://haojiezhe12345.top:82/madohomu/archive/"));
        } catch (IOException ignored) {
        }
    }

    @FXML
    public void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("JavaFX client for Archive of Madoka Magica");
        alert.show();
    }

    @FXML
    public void exit() {
        Platform.exit();
    }

}
