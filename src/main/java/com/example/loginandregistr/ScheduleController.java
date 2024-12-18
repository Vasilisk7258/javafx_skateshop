package com.example.loginandregistr;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ScheduleController implements Initializable {
    @FXML
    public TableView<Schedule> tableSchedule;
    @FXML
    public TableView<Schedule> usersSchedule;
    @FXML
    public Label userLogo;
    DB db = new DB();
    public void showOrders(MouseEvent event) throws IOException {
        Scene stage = (Scene) ((Node) event.getSource()).getScene();
        stage.setRoot(FXMLLoader.load(getClass().getResource("Orders.fxml")));
    }
    @FXML
    public void catalog(MouseEvent  event) throws IOException {
        Scene stage = (Scene) ((Node) event.getSource()).getScene();
        stage.setRoot(FXMLLoader.load(getClass().getResource("main.fxml")));
    }
    public void initialize(URL url, ResourceBundle resourceBundle){
        userLogo.setText(HomePageController.user.name +" "+ HomePageController.user.surname);
        tableSchedule.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableSchedule.setEditable(true);
        tableSchedule.setPlaceholder(new Label(""));
        TableColumn<Schedule, String> time = new TableColumn<>("дата");
        time.setCellValueFactory(new PropertyValueFactory<>("time"));
        TableColumn<Schedule, Button> but = new TableColumn<>();
        but.setCellValueFactory(new PropertyValueFactory<>("addButton"));
        but.setEditable(true);
        tableSchedule.getColumns().addAll(time, but);
        try {
            tableSchedule.setItems(db.getSchedules());
        }
        catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        usersSchedule.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        usersSchedule.setEditable(true);
        TableColumn<Schedule, String> times = new TableColumn<>("дата");
        times.setCellValueFactory(new PropertyValueFactory<>("time"));
        usersSchedule.setPlaceholder(new Label(""));
        usersSchedule.getColumns().addAll(times);
        try {
            usersSchedule.setItems(db.getUserSchedules());
        }
        catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void logOut(MouseEvent event) throws IOException{
        Scene stage = (Scene) ((Node) event.getSource()).getScene();
        stage.setRoot(FXMLLoader.load(getClass().getResource("registration.fxml")));
        HomePageController.user = null;
    }
}
