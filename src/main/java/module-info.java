module love.madohomu.madomagiarchive_fe_java.madomagiarchivefejava {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens love.madohomu.madomagiarchive_fe_java to javafx.fxml;
    exports love.madohomu.madomagiarchive_fe_java;
}
