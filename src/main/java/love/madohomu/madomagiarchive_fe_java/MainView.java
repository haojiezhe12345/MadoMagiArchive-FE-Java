package love.madohomu.madomagiarchive_fe_java;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import love.madohomu.madomagiarchive_fe_java.components.FileItemComponent;
import love.madohomu.madomagiarchive_fe_java.components.FileProps;
import love.madohomu.madomagiarchive_fe_java.models.FileItem;
import love.madohomu.madomagiarchive_fe_java.net.ApiClient;
import love.madohomu.madomagiarchive_fe_java.views.FileViewer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainView {
    @FXML
    private TextField mainSearchInput;
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
    private Slider scaleSlider;
    @FXML
    public Label statusLabel;

    private final List<FileItemComponent> fileItemComponents = new ArrayList<>();

    public boolean ctrlKeyPressed = false;

    @FXML
    public void initialize() {
        fileItemContainer.prefWrapLengthProperty().bind(Bindings.subtract(fileItemScrollContainer.widthProperty(), 24));

        scaleSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            fileItemComponents.forEach(FileItemComponent::updateImageHeight);
        });

        reloadFiles();
    }

    public void beforeShow() {
        MainApplication.primaryScene.setOnKeyPressed(e -> {
            ctrlKeyPressed = e.isControlDown();
            multiSelect.setSelected(ctrlKeyPressed);

            if (ctrlKeyPressed) {
                switch (e.getCode()) {
                    case R:
                        reloadFiles();
                        break;
                }
            } else {
                switch (e.getCode()) {
                    case F5:
                        reloadFiles();
                        break;
                    case ENTER:
                        viewFile();
                        break;
                }
            }
        });

        MainApplication.primaryScene.setOnKeyReleased(e -> {
            ctrlKeyPressed = e.isControlDown();
            multiSelect.setSelected(ctrlKeyPressed);
        });
    }

    @FXML
    public void reloadFiles() {
        if (Platform.isFxApplicationThread()) {
            setStatus("Refreshing...");
        }

        ApiClient.getFileList(fileItems -> {
            Platform.runLater(() -> {
                fileItemContainer.getChildren().clear();
                fileItemComponents.clear();

                fileItems.forEach(fileItem -> {
                    FileItemComponent fileItemComponent = FileItemComponent.CreateInstance(fileItem, this);
                    fileItemContainer.getChildren().add(fileItemComponent.getNode());
                    fileItemComponents.add(fileItemComponent);
                });

                setStatus("Refresh completed");
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
    public void onScroll(ScrollEvent e) {
        if (ctrlKeyPressed) {
            if (e.getDeltaY() > 0) {
                scaleSlider.increment();
            } else {
                scaleSlider.decrement();
            }
        }
    }

    public boolean getMultiSelect() {
        return multiSelect.isSelected();
    }

    public List<FileItemComponent> getSelectedFileItemComponents() {
        List<FileItemComponent> selected = new ArrayList<>();

        fileItemComponents.forEach(fileItemComponent -> {
            if (fileItemComponent.getSelected()) {
                selected.add(fileItemComponent);
            }
        });

        return selected;
    }

    public List<FileItem> getSelectedFiles() {
        return getSelectedFileItemComponents().stream()
                .map(FileItemComponent::getFileItem)
                .toList();
    }

    public int getImageHeight() {
        return (int)scaleSlider.getValue();
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    @FXML
    public void viewFile() {
        getSelectedFiles().forEach(FileViewer::ViewFile);
    }

    @FXML
    public void editDetail() {
        getSelectedFiles().forEach(FileProps::EditFile);
    }

    @FXML
    public void downloadFiles() {
        String saveDir = Utils.chooseSaveDir();
        List<FileItem> files = getSelectedFiles();

        class ProgressCounter {
            public int success = 0;
            public int fail = 0;
            public final int total = files.size();

            final Lock lock = new ReentrantLock();

            public void addFail() {
                lock.lock();
                fail++;
                showProgress();
                lock.unlock();
            }

            public void addSuccess() {
                lock.lock();
                success++;
                showProgress();
                lock.unlock();
            }

            private void showProgress() {
                Platform.runLater(() -> {
                    setStatus("Downloading files: %d/%d completed, %d failed".formatted(success, total, fail));
                });
            }
        }

        ProgressCounter counter = new ProgressCounter();

        for (FileItem fileItem : files) {
            ApiClient.downloadFile(fileItem.id, stream -> {
                if (stream == null) {
                    counter.addFail();
                    return;
                }

                String savePath = null;

                for (int i = 0; ; i++) {
                    String path = "%s/MadoMagiArchive-%d%s%s".formatted(
                            saveDir,
                            fileItem.id,
                            i == 0 ? "" : " (%d)".formatted(i),
                            Utils.getFileExtension(fileItem.file)
                    );
                    if (!new File(path).isFile()) {
                        savePath = path;
                        break;
                    }
                }

                try {
                    Files.copy(stream, Paths.get(savePath), StandardCopyOption.REPLACE_EXISTING);
                    counter.addSuccess();
                } catch (IOException e) {
                    counter.addFail();
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @FXML
    public void uploadFiles() {
        List<File> files = Utils.chooseOpenFile();
        if (files == null || files.isEmpty()) {
            return;
        }

        files.forEach(file -> {
            setStatus("Uploading %s...".formatted(file.getName()));

            ApiClient.uploadFile(file, result -> {
                if (result.code == 1) {
                    Platform.runLater(() -> setStatus("Upload succeeded"));
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
                getSelectedFileItemComponents().forEach(x -> {
                    setStatus("Deleting file #%d...".formatted(x.getFileItem().id));

                    ApiClient.deleteFile(x.getFileItem().id, result -> {
                        if (result.code == 1) {
                            Platform.runLater(() -> {
                                setStatus("Delete succeeded");
                                fileItemContainer.getChildren().remove(x.getNode());
                                fileItemComponents.remove(x);
                            });
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
