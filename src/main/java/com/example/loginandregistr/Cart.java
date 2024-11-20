package com.example.loginandregistr;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Cart implements Initializable {
    @FXML
    private Label name;
    @FXML
    private Label price;
    @FXML
    private Label size;
    @FXML
    private Label description;
    @FXML
    private ImageView img;
    @FXML
    public Button cart;
    public static Product product;
    @FXML
    public HBox adminButtons;
    @FXML
    public TextArea nameField;
    @FXML
    public TextArea priceField;
    @FXML
    public TextArea sizeField;
    @FXML
    public TextArea descriptionField;
    @FXML
    public Button saveDifsB;
    @FXML
    public Button change;
    @FXML
    public TextField kolvoText;
    @FXML
    public Label discont;
    @FXML
    public TextArea discountField;
    @FXML
    public StackPane stackDiscount;
    DB db = new DB();
    @FXML
    public void initialize(URL location, ResourceBundle resources){
        if(HomePageController.user.isAdmin()){
            adminButtons.setVisible(true);
            kolvoText.setVisible(true);
        }
        if ((product.discount != 0) || (HomePageController.user.isAdmin())){
            stackDiscount.setVisible(true);
            discont.setText("Скидка: "+product.discount+ "%");
        }
        kolvoText.setText(""+product.amount);
        name.setText(product.name);
        price.setText(""+(product.price - product.price*product.discount/100));
        size.setText("Размер: "+product.size);
        description.setText(product.description);
        img.setImage(new Image(new ByteArrayInputStream(product.photo)));
    }
    public void setData(Product pr) {
        product = pr;
    }
    public void back(MouseEvent event) throws IOException {
        Scene stage =((Node) event.getSource()).getScene();
        stage.setRoot(FXMLLoader.load(getClass().getResource("main.fxml")));
    }
    public void addToCart(MouseEvent event) throws IOException{
        cart.setText("добавлен");
        cart.setDisable(true);
        HomePageController.user.cart.add(new ProductInCart(product));
        Scene stage = ((Node) event.getSource()).getScene();
        stage.setRoot(FXMLLoader.load(getClass().getResource("main.fxml")));
    }
    public void changeProduct(MouseEvent event) {
        nameField.setVisible(true);
        name.setVisible(false);
        nameField.setText(name.getText());
        priceField.setVisible(true);
        price.setVisible(false);
        priceField.setText(price.getText());
        sizeField.setVisible(true);
        size.setVisible(false);
        sizeField.setText(size.getText());
        descriptionField.setVisible(true);
        description.setVisible(false);
        descriptionField.setText(description.getText());
        change.setVisible(false);
        saveDifsB.setVisible(true);
        kolvoText.setEditable(true);
        discont.setVisible(false);
        discountField.setVisible(true);
        discountField.setText(discont.getText());
    }
    public void save(MouseEvent event) throws SQLException, ClassNotFoundException {
        nameField.setVisible(false);
        name.setVisible(true);
        name.setText(nameField.getText());
        product.name = name.getText();
        priceField.setVisible(false);
        price.setVisible(true);
        price.setText(priceField.getText());
        product.price = Float.parseFloat(price.getText());
        sizeField.setVisible(false);
        size.setVisible(true);
        size.setText(sizeField.getText().substring(sizeField.getText().indexOf(":")+2));
        product.size = size.getText();
        descriptionField.setVisible(false);
        description.setVisible(true);
        description.setText(descriptionField.getText());
        product.description = description.getText();
        product.amount = Integer.parseInt(kolvoText.getText());
        change.setVisible(true);
        saveDifsB.setVisible(false);
        kolvoText.setEditable(false);
        discont.setVisible(true);
        discountField.setVisible(false);
        discont.setText(discountField.getText());
        product.discount = Float.parseFloat(discont.getText().substring(discont.getText().indexOf(":")+2).replace("%", "").trim());
        db.refreshProduct(product);
    }
    public void updatePhoto(MouseEvent event) throws SQLException, ClassNotFoundException, FileNotFoundException, IOException {
        if(HomePageController.user.isAdmin()){
            final File[] selectedFile = new File[1];
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите изображение");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            selectedFile[0] = fileChooser.showOpenDialog(name.getScene().getWindow());
            if (selectedFile[0] != null) {
                File file = new File(selectedFile[0].toURI());
                db.updateImage(file, product);
                Image image = new Image(selectedFile[0].toURI().toString());
                img.setImage(image);
                img.setPreserveRatio(true);
            }
        }
    }
    public void deleteProduct(MouseEvent event) throws SQLException, ClassNotFoundException, IOException{
        db.deleteProduct(product);
        Scene stage = ((Node) event.getSource()).getScene();
        stage.setRoot(FXMLLoader.load(getClass().getResource("main.fxml")));
    }
}