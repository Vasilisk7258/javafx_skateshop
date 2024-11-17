package com.example.loginandregistr;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {
    DB db = new DB();
    @FXML
    GridPane g;
    @FXML
    TableView<ProductInCart> cartTable;
    @FXML
    public Label userLogo;
    @FXML
    public Label sumText;
    public static User user;
    @FXML
    ComboBox manufacturer;
    @FXML
    TextField search;
    @FXML
    TextField userMailField;
    @FXML
    Button addNew;
    private List<Product> pr = new ArrayList<>();
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        if (user.isAdmin()){
            addNew.setVisible(true);
            userMailField.setDisable(false);
            userMailField.setVisible(true);
            if (!user.userMail.isEmpty()) {
                userMailField.setText(user.userMail);
            }
//             ? userMailField.setText("") : userMailField.setText(user.userMail);

        }
        userLogo.setText(user.name + " " + user.surname);
//        System.out.println(user);
        try {
            updateTable();
        } catch (SQLException | ClassNotFoundException | IOException e) {
            System.out.println(e.getMessage());
        }
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        cartTable.setEditable(true);
        TableColumn<ProductInCart, ImageView> imageColumn = new TableColumn<>("Товар");
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
        imageColumn.setMinWidth(50); // Установка минимальной ширины в 50 пикселей
        imageColumn.setPrefWidth(50);
        imageColumn.setCellFactory(column -> new TableCell<ProductInCart, ImageView>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(ImageView item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(item.getImage());

                    // Установка размеров изображения в зависимости от ширины колонки
                    double columnWidth = imageColumn.getWidth();
                    imageView.setFitWidth(columnWidth);
                    imageView.setFitHeight(columnWidth);
                    imageView.setPreserveRatio(true);

                    setGraphic(imageView);
                }
            }
        });
        TableColumn<ProductInCart, HBox> infoColumn = new TableColumn<>("количество");
        infoColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        infoColumn.setEditable(true);
        TableColumn<ProductInCart, String> quantityColumn = new TableColumn<>("цена");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setEditable(true);

        cartTable.getColumns().addAll(imageColumn, infoColumn, quantityColumn);
        if(user.cart != null) {
            cartTable.setItems(user.cart);
            refreshSum(user.cart);
        }
        try {
            manufacturer.setItems(db.getManufacturer());
            if(user.manufacturer == "") {
                manufacturer.getSelectionModel().selectFirst();
            }
            else {
                manufacturer.getSelectionModel().select(user.manufacturer);
            }
            if (user.search != ""){
                search.setText(user.search);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        manufacturer.setOnAction(event -> {
            try {
                user.manufacturer = (String) manufacturer.getSelectionModel().getSelectedItem();
                updateTable();
            } catch (SQLException | ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                    user.search = search.getText();
                    updateTable();
            } catch (SQLException | ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        userMailField.textProperty().addListener((observable, oldValue, newValue) -> {
                user.userMail = userMailField.getText();
        });

    }
    @FXML
    public void refreshSum(ObservableList<ProductInCart> cart){
        float sum = 0;
        for(ProductInCart item: cart){
            sum += item.getPrice();
        }
            sumText.setText("Общая стоимость: "+sum);


        System.out.println(sumText.getText());
    }
    public void createOrder(MouseEvent event) throws  ClassNotFoundException, SQLException, IOException{
        DB db = new DB();
        boolean isInserted = db.createOrder(cartTable.getItems(), sumText.getText(), userMailField.getText());
        if (isInserted){
            cartTable.setItems(null);
            user.cart = null;
            sumText.setText("Общая стоимость: 0");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Заказ создан успешно");
            alert.showAndWait();
            }
        else {
            user.cart = cartTable.getItems();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Заказ не создан, пожалуйста повторите попытку");
            alert.showAndWait();
        }
        Scene stage = (Scene) ((Node) event.getSource()).getScene();
        stage.setRoot(FXMLLoader.load(getClass().getResource("main.fxml")));

    }
    public void showOrders(MouseEvent event) throws IOException{
        user.cart = cartTable.getItems();
        Scene stage = (Scene) ((Node) event.getSource()).getScene();
        stage.setRoot(FXMLLoader.load(getClass().getResource("Orders.fxml")));
    }
    private void updateTable() throws SQLException, ClassNotFoundException, IOException {
        String selectedManufacturer = user.manufacturer;
//            System.out.println(user.search);
            // Фильтрация данных и обновление таблицы
            pr = db.getAllProducts(selectedManufacturer, user.search);
            int column = 0;
            int row = 0;
            g.getChildren().clear();
            g.getRowConstraints().clear();
            for (int i = 0; i < pr.size(); i++) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("card.fxml"));
                AnchorPane anchor = loader.load();
                Card card = loader.getController();
                card.h = this;
                card.setData(pr.get(i));
                if (column == 3) {
                    column = 0;
                    row++;
                }
                Button isAdded = (Button) anchor.lookup("#addToCart");
                if(user.cart != null) {
                    for (ProductInCart item : user.cart) {
                        if (item.product.id == pr.get(i).id) {
                            isAdded.setText("Добавлено");
                            isAdded.setDisable(true);
                            if (item.kolvo > pr.get(i).amount){
                                item.kolvo = pr.get(i).amount;
                            }
                        }
                    }
                }
                g.add(anchor, column, row);
                Product p = pr.get(i);
                p.locationInGrid = new int[] {column++, row};
                GridPane.setMargin(anchor, new Insets(10));
            }
//            g.setItems(db.getAllProducts(selectedManufacturer,search.getText()));
//            col.setText(dbP.getProducts(selectedManufacturer,search.getText()).size() + "/" + dbP.getCount());
        }
        public void showSchedule(MouseEvent event) throws IOException {
            Scene stage = (Scene) ((Node) event.getSource()).getScene();
            stage.setRoot(FXMLLoader.load(getClass().getResource("schedule.fxml")));
        }
    public void addNewItem(MouseEvent event) {
        Dialog<ButtonType> addDialog = new Dialog<>();
        addDialog.setTitle("Добавить новый продукт");

        VBox addDialogPane = new VBox();
        addDialogPane.setSpacing(10);
        addDialogPane.setPadding(new Insets(10)); // Отступы для аккуратного вида

        // Поля для ввода данных
//        TextField articleNumberField = new TextField();
        TextField nameField = new TextField();
        TextArea descriptionField = new TextArea();
        TextField categoryField = new TextField();
        TextField manufacturerField = new TextField();
        TextField priceField = new TextField();
        TextField discountField = new TextField();
        TextField quantityField = new TextField();
        ImageView productImageView = new ImageView();

        Button chooseImageButton = new Button("Выбрать изображение");
        // Переменная для хранения изображения
        final File[] selectedFile = new File[1];

        chooseImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите изображение");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            selectedFile[0] = fileChooser.showOpenDialog(addDialog.getDialogPane().getScene().getWindow());
            if (selectedFile[0] != null) {
                // Загружаем изображение и отображаем его
                Image image = new Image(selectedFile[0].toURI().toString());
                productImageView.setImage(image);
                productImageView.setFitWidth(150);
                productImageView.setFitHeight(150);
                productImageView.setPreserveRatio(true);
            }
        });

        // Добавление всех элементов в диалог
        addDialogPane.getChildren().addAll(
//                new Label("Артикул товара:"), articleNumberField,
                new Label("Наименование товара:"), nameField,
                new Label("Описание товара:"), descriptionField,
                new Label("Размер:"), categoryField,
                new Label("Производитель:"), manufacturerField,
                new Label("Цена:"), priceField,
                new Label("Скидка:"), discountField,
                new Label("Кол-во на складе:"), quantityField,
                chooseImageButton,
                new Label("Выбранное изображение:"),
                productImageView // Отображаемое изображение
        );

        addDialog.getDialogPane().setContent(addDialogPane);
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        addDialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

        Optional<ButtonType> result = addDialog.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeOk) {
            if (nameField.getText().isEmpty() || descriptionField.getText().isEmpty()
            || categoryField.getText().isEmpty() || manufacturerField.getText().isEmpty()|| priceField.getText().isEmpty() ||
            discountField.getText().isEmpty() || quantityField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Все поля должны быть заполнены");
            }
            // Обработка данных нового продукта
            try {
//                String articleNumber = articleNumberField.getText();
                String name = nameField.getText();
                String description = descriptionField.getText();
                String category = categoryField.getText();
                String manufacturer = manufacturerField.getText();
                float price = Float.parseFloat(priceField.getText());
                int discount = Integer.parseInt(discountField.getText());
                int quantity = Integer.parseInt(quantityField.getText());

                // Получение изображения в массив байтов
                byte[] imageBytes = null;
                if (selectedFile[0] != null) {
                    imageBytes = Files.readAllBytes(selectedFile[0].toPath()); // Чтение файла в массив байтов
                }

                // Создание нового объекта Product
                Product newProduct = new Product();
//                newProduct.setProductArticleNumber(articleNumber);
                newProduct.name = name;
                newProduct.description = description;
                newProduct.size = category;
                newProduct.manufacturer = manufacturer;
                newProduct.price = price;
                newProduct.discount = discount;
                newProduct.amount = quantity;
                newProduct.photo = imageBytes; // Установка массива байтов как фотографии

                // Вызов метода для добавления нового продукта в базу данных
//                dbP.addProductToDatabase(newProduct);
                db.addNewProduct(newProduct);
                updateTable(); // Обновляем таблицу, чтобы отразить изменения

            } catch (NumberFormatException e) {
                // Обработка некорректного ввода данных
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка ввода");
                alert.setHeaderText("Некорректные данные");
                alert.setContentText("Пожалуйста, введите числовые значения для цены, скидки и количества.");
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Не удалось загрузить изображение");
                alert.setContentText("Пожалуйста, убедитесь, что файл изображения доступен.");
                alert.showAndWait();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Не удалось добавить продукт");
                alert.setContentText("Произошла ошибка при добавлении продукта в базу данных.");
                alert.showAndWait();
            }
        }
    }

}
