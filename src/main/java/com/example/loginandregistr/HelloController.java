package com.example.loginandregistr;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class HelloController {
    @FXML
    private TextField nameText;
    @FXML
    private TextField surnameText;
    @FXML
    private TextField passwordText;
    @FXML
    private TextField passwordTextOnce;
    @FXML
    private TextField mailText;
    @FXML
    private Button logInButton;
    @FXML
    private Button signUpButton;
    @FXML
    public Label test;
    
    DB db = new DB();
    public void signUp(MouseEvent event) throws SQLException, ClassNotFoundException, IOException{
//        System.out.println(mailText.getText());

        // Font custom = Font.loadFont(getClass().getResourceAsStream("/Play/Play-Regular.ttf"), 33);
        // test.setFont(custom);
        if((nameText.getText().isEmpty()) || (surnameText.getText().isEmpty()) ||(passwordText.getText().isEmpty()) ||(passwordTextOnce.getText().isEmpty()) ||(mailText.getText().isEmpty())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Все поля должны быть заполнены");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
        else if(!passwordText.getText().equals(passwordTextOnce.getText())){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("пароли не совпадают");
            alert.setHeaderText(null);
            alert.showAndWait();

        }
        else{
            String res = db.registration(nameText.getText(), passwordText.getText(), mailText.getText(), surnameText.getText());
            if (res.equals("success")) {
                Scene stage = (Scene) ((Node) event.getSource()).getScene();
                stage.setRoot(FXMLLoader.load(getClass().getResource("hello-view.fxml")));
            }
            else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(res);
                alert.setHeaderText(null);
                alert.showAndWait();
            }
            }
    }
    @FXML
    public void logIn(MouseEvent event) throws IOException {
        Scene stage = (Scene) ((Node) event.getSource()).getScene();
        stage.setRoot(FXMLLoader.load(getClass().getResource("login.fxml")));
    }

}