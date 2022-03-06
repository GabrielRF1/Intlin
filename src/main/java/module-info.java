module ine.ufsc.intlin {
    requires java.sql;
    requires org.json;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires javafx.media;

    opens ine.ufsc.intlin to javafx.fxml;
    exports ine.ufsc.intlin;
}
