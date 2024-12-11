package love.madohomu.madomagiarchive_fe_java;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import love.madohomu.madomagiarchive_fe_java.components.FileItemComponent;
import love.madohomu.madomagiarchive_fe_java.models.FileItem;
import love.madohomu.madomagiarchive_fe_java.net.ApiClient;

import java.io.File;
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
    private CheckBox multiSelect;
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

                fileItems.forEach(fileItem -> {
                    FileItemComponent fileItemComponent = FileItemComponent.createInstance(fileItem, this);
                    fileItemContainer.getChildren().add(fileItemComponent.getNode());
                    fileItemComponents.add(fileItemComponent);
                });
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
        Object target = e.getTarget();
        if (target instanceof ImageView || target instanceof HBox) {
            fileItemScrollContainer.setContextMenu(fileItemContentMenu);
        } else {
            deselectFiles();
            fileItemScrollContainer.setContextMenu(fileItemBackgroundContentMenu);
        }
    }

    @FXML
    public void onKeyDown(KeyEvent e) {
        multiSelect.setSelected(e.isControlDown());
        if (e.getCode() == KeyCode.DELETE) {
            showDeleteConfirmation();
        }
    }

    @FXML
    public void onKeyUp(KeyEvent e) {
        multiSelect.setSelected(e.isControlDown());
    }

    public boolean getMultiSelect() {
        return multiSelect.isSelected();
    }

    public List<FileItem> getSelectedFiles() {
        List<FileItem> selectedFiles = new ArrayList<>();
        fileItemComponents.forEach(fileItemComponent -> {
            if (fileItemComponent.getSelected()) {
                selectedFiles.add(fileItemComponent.getFileItem());
            }
        });
        return selectedFiles;
    }

    @FXML
    public void uploadFiles() {
        List<File> files = Utils.chooseOpenFile();
        if (files == null || files.isEmpty()) {
            return;
        }

        files.forEach(file -> {
            ApiClient.uploadFile(file, result -> {
                if (result.code == 1) {
                    reloadFiles();
                } else {
                    Platform.runLater(() -> Utils.showAlert(result.message));
                }
            });
        });
    }

    @FXML
    public void showDeleteConfirmation() {
        int count = getSelectedFiles().size();
        if (count == 0) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm delete");
        alert.setHeaderText("Delete the selected %d file(s)?".formatted(count));

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                getSelectedFiles().forEach(x -> {
                    ApiClient.deleteFile(x.id, result -> {
                        if (result.code == 1) {
                            reloadFiles();
                        } else {
                            Platform.runLater(() -> Utils.showAlert(result.message));
                        }
                    });
                });
            }
        });
    }

    @FXML
    public void gotoWebsite() {
        Utils.openUrl("https://haojiezhe12345.top:82/madohomu/archive/");
    }

    @FXML
    public void viewGitHub() {
        Utils.openUrl("https://github.com/haojiezhe12345/MadoMagiArchive-FE-Java");
    }

    @FXML
    public void showAbout() {
        Utils.showAlert("About", "JavaFX client for Archive of Madoka Magica");
    }

    @FXML
    public void exit() {
        Platform.exit();
    }
}
