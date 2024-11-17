package com.example.loginandregistr;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class Registration{
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

    DB db = new DB();
    // Регулярное выражение для проверки правильности строки
    String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    Pattern pattern = Pattern.compile(emailRegex);
    
    public void signUp(MouseEvent event) throws SQLException, ClassNotFoundException, IOException{
        // Проверка на то, что все оля заполнены
        if((nameText.getText().isEmpty()) || (surnameText.getText().isEmpty()) ||(passwordText.getText().isEmpty()) ||(passwordTextOnce.getText().isEmpty()) ||(mailText.getText().isEmpty())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Все поля должны быть заполнены");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
        // проверка на корректность введенных данных
        else if (!nameText.getText().chars().allMatch(Character::isLetter)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Введите корректное имя");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
        else if (!surnameText.getText().chars().allMatch(Character::isLetter)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Введите корректную фамилию");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
        else if (!pattern.matcher(mailText.getText()).matches()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Введите корректную почту");
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
                stage.setRoot(FXMLLoader.load(getClass().getResource("main.fxml")));
            }
            else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(res);
                alert.setHeaderText(null);
                alert.showAndWait();
            }
        }
    }
    // переход на сцену со входом
    public void logIn(MouseEvent event) throws IOException {
        Scene stage = (Scene) ((Node) event.getSource()).getScene();
        stage.setRoot(FXMLLoader.load(getClass().getResource("login.fxml")));
    }
}