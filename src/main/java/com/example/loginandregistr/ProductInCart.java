package com.example.loginandregistr;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class ProductInCart {
    public int id;
    Product product;
    HBox amount;
    int kolvo = 1;
    public FloatProperty cost;
    public ProductInCart(Product product){
        this.product = product;
        this.cost = new SimpleFloatProperty(this.product.price);
        this.id = this.product.id;
    }
    public ImageView getImage() {
        return this.product.image;
    }
    public Float getPrice() {
        return this.cost.get();
    }
    public void setPrice(float price){
        this.cost.set(price);
    }
    public HBox getAmount() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("amount.fxml"));
        this.amount = loader.load();
        Amount a = loader.getController();
        a.amountText.setText(""+this.kolvo);
        return this.amount;
    }
}
