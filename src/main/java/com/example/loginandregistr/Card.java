package com.example.loginandregistr;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Card{
    @FXML
    private Label name;
    @FXML
    private Label price;
    @FXML
    private ImageView img;
    @FXML
    private Button addToCart;
    @FXML
    private Label discount;
    public HomePageController h;
    public Product product;


    public void setData(Product product) {
        this.product = product;
        if (product.discount != 0){
            discount.setText("Скидка: "+product.discount+ "%");
        }
        else discount.setVisible(false);

        name.setText(product.name);
        price.setText(""+(product.price - product.price*product.discount/100));
        img.setImage(new Image(new ByteArrayInputStream(product.photo)));
        if(product.amount == 0) {
            addToCart.setText("Нет на складе");
            addToCart.setDisable(true);
        }
    }
    public void onClick(MouseEvent event) throws IOException {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("homePage.fxml"));
        Scene scene = (Scene) ((Node) event.getSource()).getScene();
        Cart.product = product;
        HomePageController.user.cart = h.cartTable.getItems();
        HomePageController.user.manufacturer = (String) h.manufacturer.getSelectionModel().getSelectedItem();
        HomePageController.user.search = h.search.getText();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("homePage.fxml"));
        AnchorPane p = loader.load();
        Cart cart = loader.getController();
        cart.cart.setText(addToCart.getText());
        cart.cart.setDisable(addToCart.isDisable());
        scene.setRoot(p);
    }
    public void setAddToCart(MouseEvent event) throws IOException{
        addToCart.setText("добавлен");
        addToCart.setDisable(true);
        ((Node) event.getSource()).getScene().getWindow();
        System.out.println(product.locationInGrid[0]);
//        System.out.println(GridPane.getColumnIndex(addToCart));
        ProductInCart pc = new ProductInCart(product);
        TableView<ProductInCart> f =  (TableView<ProductInCart>) ((Node) event.getSource()).getScene().lookup("#cartTable");
        f.getItems().add(pc);
//        ((Node) event.getSource()).getScene().getWindow();
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
//        loader.load();
//        HomePageController controller = loader.getController();
        h.refreshSum(f.getItems());

    }

}
