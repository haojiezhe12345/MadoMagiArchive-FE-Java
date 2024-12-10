module love.madohomu.madomagiarchive_fe_java.madomagiarchivefejava {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires okhttp3;
    requires com.google.gson;
    requires annotations;

    opens love.madohomu.madomagiarchive_fe_java to javafx.fxml;
    opens love.madohomu.madomagiarchive_fe_java.models to com.google.gson;

    exports love.madohomu.madomagiarchive_fe_java;
}
