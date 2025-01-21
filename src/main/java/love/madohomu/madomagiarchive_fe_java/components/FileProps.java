package love.madohomu.madomagiarchive_fe_java.components;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import love.madohomu.madomagiarchive_fe_java.Utils;
import love.madohomu.madomagiarchive_fe_java.models.FileItem;
import love.madohomu.madomagiarchive_fe_java.models.FilesUpdateDTO;
import love.madohomu.madomagiarchive_fe_java.net.ApiClient;
import love.madohomu.madomagiarchive_fe_java.views.FileViewer;

import javax.swing.filechooser.FileView;
import java.util.Arrays;

public class FileProps {
    @FXML
    public TextField propTitle;
    @FXML
    public TextArea propDescription;
    @FXML
    public CheckBox propR18;
    @FXML
    public TextField propType;
    @FXML
    public TextArea propSource;
    @FXML
    public FlowPane propTags;

    @FXML
    public Button saveBtn;
    @FXML
    public Button closeBtn;

    private Stage stage;
    private Scene scene;
    private FileViewer parent;
    private FileItem fileItem;

    public static void EditFile(FileItem fileItem) {
        EditFile(fileItem, null);
    }

    public static void EditFile(FileItem fileItem, FileViewer parent) {
        Utils.createStageFromFXML(FileProps.class, (loader, stage, scene, controller) -> {
            controller.stage = stage;
            controller.scene = scene;
            controller.fileItem = fileItem;

            if (parent != null) {
                stage.initOwner(parent.stage);
                stage.initModality(Modality.WINDOW_MODAL);
                controller.parent = parent;
            }

            controller.beforeShowThen(() -> {
                Platform.runLater(() -> {
                    stage.show();
                });
            });
        });
    }

    public void beforeShowThen(Runnable callback) {
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ESCAPE:
                    close();
                    break;
            }
        });

        closeBtn.requestFocus();
        stage.setTitle("Editing file #" + fileItem.id);

        ApiClient.getFileDetail(fileItem.id, fileDetail -> {
            propTitle.setText(fileDetail.title);
            propDescription.setText(fileDetail.description);
            propR18.setSelected(Utils.FalseIfNull(fileDetail.r18));
            propType.setText(fileDetail.type);
            propSource.setText(fileDetail.source);

            callback.run();
        });
    }

    @FXML
    public void save() {
        saveBtn.setText("_Saving...");

        FilesUpdateDTO filesUpdateDTO = new FilesUpdateDTO() {{
            ids = new int[]{fileItem.id};
            title = propTitle.getText();
            description = propDescription.getText();
            r18 = propR18.isSelected();
            type = propType.getText();
            source = propSource.getText();
        }};

        ApiClient.updateFileDetail(filesUpdateDTO, response -> {
            Platform.runLater(() -> {
                if (response.code == 1) {
                    saveBtn.setText("_Saved!");
                } else {
                    Utils.showAlert("Save failed", "%s\n%s".formatted(response.message, Arrays.toString(response.data)));
                    saveBtn.setText("_Save");
                }
            });

            if (parent != null) {
                parent.loadFileDeatil();
            }
        });
    }

    @FXML
    public void close() {
        stage.close();
    }
}
