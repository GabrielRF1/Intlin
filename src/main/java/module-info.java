module ine.ufsc.intlin {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;

    opens ine.ufsc.intlin to javafx.fxml;
    exports ine.ufsc.intlin;
}
