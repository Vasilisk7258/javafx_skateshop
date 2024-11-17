package com.example.loginandregistr;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class Amount implements Initializable {
    @FXML
    public Button plusButton;
    @FXML
    public Button minusButton;
    @FXML
    public Label amountText;
    public HomePageController h;
    @FXML
    public void initialize(URL location, ResourceBundle resourceBundle){

    }
    public void minus(MouseEvent event){
        TableView<ProductInCart> f =  (TableView<ProductInCart>) ((Node) event.getSource()).getScene().lookup("#cartTable");
        ObservableList<ProductInCart> list = f.getItems();
        float sum = 0;
        int index = -1;
        Label sumT = (Label) ((Node) event.getSource()).getScene().lookup("#sumText");
        for (ProductInCart item:list){
            System.out.println(item);
            if (((Label) item.amount.getChildren().get(1)) == amountText) {
                item.kolvo-=1;
                if (item.kolvo == 0) {
//                    list.remove(item);
                    index = list.indexOf(item);
                    GridPane grid = (GridPane) ((Node) event.getSource()).getScene().lookup("#g");
                    for (Node node : grid.getChildren()) {
                        if (GridPane.getColumnIndex(node) == item.product.locationInGrid[0] && GridPane.getRowIndex(node) == item.product.locationInGrid[1]) {
                            Button b =  (Button) node.lookup("#addToCart");
                            b.setDisable(false);
                            b.setText("в корзину");
                        }
                    }
                }
                item.setPrice(item.kolvo * item.product.price);
                f.edit(list.indexOf(item), f.getColumns().get(2));
                f.edit(list.indexOf(item), f.getColumns().getFirst());
                amountText.setText(""+item.kolvo);
            }
        }
        try{
            list.remove(index);
            index = -1;
        }
        catch (Exception e) {

        }
        for (ProductInCart item: list) {
            sum += item.getPrice();
            System.out.println(sum);
        }
        sumT.setText("Общая стоимость: "+sum);
    }

    public void plus(MouseEvent event){
        TableView<ProductInCart> f =  (TableView<ProductInCart>) ((Node) event.getSource()).getScene().lookup("#cartTable");
        ObservableList<ProductInCart> list = f.getItems();

        float sum = 0;
        Label sumT = (Label) ((Node) event.getSource()).getScene().lookup("#sumText");
        for (ProductInCart item:list){
            if (((Label) item.amount.getChildren().get(1)) == amountText) {
                if (item.kolvo >= item.product.amount) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("большего количества нет на складе");
                    alert.setHeaderText(null);
                    alert.showAndWait();
                    break;
                }
                item.kolvo +=1;
                item.setPrice(item.kolvo * item.product.price);
                f.edit(list.indexOf(item), f.getColumns().get(2));
                f.edit(list.indexOf(item), f.getColumns().getFirst());
                amountText.setText(""+item.kolvo);
            }
            sum += item.getPrice();
            sumT.setText("Общая стоимость: "+sum);
        }

    }


}
