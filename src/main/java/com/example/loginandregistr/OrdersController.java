package com.example.loginandregistr;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class OrdersController implements Initializable {
    @FXML
    public TableView<OrderProduct> ordersTable;
    @FXML
    public Label userLogo;
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userLogo.setText(HomePageController.user.name+ " " + HomePageController.user.surname);
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ordersTable.setEditable(true);
        TableColumn<OrderProduct, Integer> numberOrder = new TableColumn<>("Номер заказа");
        numberOrder.setCellValueFactory(new PropertyValueFactory<>("id"));
        numberOrder.setMinWidth(50); // Установка минимальной ширины в 50 пикселей
        numberOrder.setPrefWidth(150);
        TableColumn<OrderProduct, Integer> sumColumn = new TableColumn<>("сумма заказа");
        sumColumn.setCellValueFactory(new PropertyValueFactory<>("sum"));
        sumColumn.setEditable(true);
        TableColumn<OrderProduct, String> statusColumn = new TableColumn<>("Статус");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setEditable(true);
        ordersTable.setPlaceholder(new Label(""));
        ordersTable.getColumns().addAll(numberOrder, sumColumn, statusColumn);
        DB db = new DB();
        try {
            List<OrderProduct> l = db.getOrders();
            for (OrderProduct item : l) {
                ordersTable.getItems().add(item);
            }
        }
        catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    @FXML
    public void catalog(MouseEvent  event) throws IOException {
        Scene stage = (Scene) ((Node) event.getSource()).getScene();
        stage.setRoot(FXMLLoader.load(getClass().getResource("main.fxml")));
    }
    public void showSchedule(MouseEvent event) throws IOException{
        Scene stage = (Scene) ((Node) event.getSource()).getScene();
        stage.setRoot(FXMLLoader.load(getClass().getResource("schedule.fxml")));
    }
    @FXML
    public void logOut(MouseEvent event) throws IOException{
        Scene stage = (Scene) ((Node) event.getSource()).getScene();
        stage.setRoot(FXMLLoader.load(getClass().getResource("registration.fxml")));
        HomePageController.user = null;
    }
}
