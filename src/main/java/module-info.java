module ine.ufsc.intlin {
    requires java.sql;
    requires org.json;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires javafx.media;
    requires javafx.web;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j;
    requires xuggle.xuggler;

    opens ine.ufsc.intlin to javafx.fxml;
    exports ine.ufsc.intlin;
}
