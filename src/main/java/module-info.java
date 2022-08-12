module com.example.projectf {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires sqlite.jdbc;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires json.simple;
    requires itextpdf;
    requires org.apache.commons.io;

    opens com.example.projectF to javafx.fxml;
    exports com.example.projectF;
}