module com.example.loginandregistr {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    opens com.example.loginandregistr to javafx.fxml;
    exports com.example.loginandregistr;
}