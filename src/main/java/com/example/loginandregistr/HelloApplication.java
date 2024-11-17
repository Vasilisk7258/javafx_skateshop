package com.example.loginandregistr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, ClassNotFoundException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("registration.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 550);
        stage.setTitle("Hello!");
//        DB db = new DB();
//        db.r();
        stage.setScene(scene);
        // Font.loadFont(getClass().getResourceAsStream("/fonts/Play/Play-Regular.ttf"), 33);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}
