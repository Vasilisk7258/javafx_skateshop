package com.example.loginandregistr;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;

public class logController {
    @FXML
    public TextField loginText;
    @FXML
    public TextField passwordText;
    @FXML
    void registration(MouseEvent event) throws IOException {
        Scene stage = (Scene) ((Node) event.getSource()).getScene();
        stage.setRoot(FXMLLoader.load(getClass().getResource("registration.fxml")));
    }
    @FXML
    void logIn(MouseEvent event) throws ClassNotFoundException, SQLException, IOException {
        DB db = new DB();
        if (loginText.getText().isEmpty() || passwordText.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("все поля должны быть заполнены");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
        else {
            String res = db.logIn(loginText.getText(), passwordText.getText());
            if (res.equals("success")) {
                Scene stage = (Scene) ((Node) event.getSource()).getScene();
                stage.setRoot(FXMLLoader.load(getClass().getResource("main.fxml")));
            } else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Неверный пароль или имя пользователя");
                alert.showAndWait();
            }
        }
    }
}
