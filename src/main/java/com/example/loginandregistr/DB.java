package com.example.loginandregistr;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.*;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DB {

    String DBURL = "jdbc:postgresql://localhost:5432/postgres";
    String USER = "postgres";
    String PASSWORD = "1234";
    Connection conn = null;
    public void geDBConnection() throws ClassNotFoundException, SQLException{
        Class.forName("org.postgresql.Driver");
        try{
            conn = DriverManager.getConnection(DBURL, USER, PASSWORD);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String registration(String name, String password, String mail, String surname) throws SQLException, ClassNotFoundException{
        geDBConnection();
        String sql = "Select * from users where mail_user = ?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, mail);
        ResultSet res = statement.executeQuery();
        if(res.next()) {
            conn = null;
            return "Пользователь с таким адресом электронной почты уже существует";
        }
        sql = "Insert into users (name_user, password_user, mail_user, id_role, surname_user) values (?,?,?,2,?)";
        statement = conn.prepareStatement(sql);
        statement.setString(1, name);
        statement.setString(2, password);
        statement.setString(3, mail);
        statement.setString(4, surname);
        statement.executeUpdate();
        sql = "Select * from users where mail_user = ?";
        statement = conn.prepareStatement(sql);
        statement.setString(1, mail);
        res = statement.executeQuery();
        if(res.next()) {
            HomePageController.user = new User(name, surname, res.getInt("id_user"), res.getInt("id_role"));
        }
        conn = null;
        return "success";
    }
    public String logIn(String mail, String password) throws SQLException, ClassNotFoundException{
        geDBConnection();
        String sql = "Select * from users where mail_user = ?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, mail);
        ResultSet res = statement.executeQuery();
        if(res.next()) {
            if(res.getString("password_user").equals(password)){
                HomePageController.user = new User(res.getString("name_user"), res.getString("surname_user"), res.getInt("id_user"), res.getInt("id_role"));
                conn = null;
                return "success";
            }
            conn = null;
            return "password";
        }
        conn = null;
        return "error";
    }
    public List<Product> getAllProducts(String manufacturere, String search) throws SQLException, ClassNotFoundException, IOException {
        geDBConnection();
        String sql;
        sql = "select * from select_products(?, ?)";
        PreparedStatement statement = conn.prepareStatement(sql);
        if(manufacturere.contains("все ")){
            manufacturere = "";
        }
        statement.setString(1, manufacturere);
        statement.setInt(2, HomePageController.user.id);
        ResultSet res = statement.executeQuery();
        List<Product> prlist = new ArrayList<>();
        while(res.next()){
            if(search.isEmpty() || res.getString("name_product").toUpperCase().contains(search.toUpperCase())) {
                Product product = new Product();
                product.id = res.getInt("id_product");
                product.name = res.getString("name_product");
                product.description = res.getString("description_product");
                product.price = res.getFloat("price_product");
                product.manufacturer = res.getString("manufacturer");
                product.discount = res.getFloat("discount");
                product.photo = res.getBytes("photo_product");
                product.size = res.getString("size");
                product.amount = res.getInt("quantity");
                product.image = new ImageView(new Image(new ByteArrayInputStream(product.photo)));
                prlist.add(product);
            }
        }
        conn = null;
        return prlist;
    }
    public void updateImage(File file, Product product) throws SQLException, ClassNotFoundException, FileNotFoundException, IOException {
        geDBConnection();
        try(FileInputStream fis = new FileInputStream(file)) {
            String sql = "Update products set photo_product = ? where id_product = ?";
            PreparedStatement p = conn.prepareStatement(sql);
            p.setBinaryStream(1, fis, (int) file.length());
            p.setInt(2, product.id);
            p.executeUpdate();
            conn = null;
        }
    }
    public boolean createOrder(ObservableList<ProductInCart> pr, String sum, String mail) throws ClassNotFoundException, SQLException{
        geDBConnection();
        conn.setAutoCommit(false);
        String json = "'{";
        for(ProductInCart item: pr){
            json += ("\""+item.product.id+ "\": " );
            json += ("\""+item.kolvo+ "\",");
        }
        json = json.substring(0, json.length()-1);
        json+="}'";
        String sql;
        PreparedStatement statement;
        if ((HomePageController.user.isAdmin()) && (!mail.isEmpty())){
            sql = "insert into orders (id_user, id_status_order, content_order, cost_order)\n" +
                    "values ((select id_user from users where mail_user = ? limit 1), ?,"+json+ ",?)";
            statement = conn.prepareStatement(sql);
            statement.setString(1, mail);
            statement.setInt(2, 1);
            statement.setInt(3, (int) Double.parseDouble(sum.substring(sum.indexOf(": ")+2)));
        }
        else {
            sql = "Insert into orders (id_user, id_status_order, content_order, cost_order) values (?,?," + json + ",?)";
            statement = conn.prepareStatement(sql);
            statement.setInt(1, HomePageController.user.id);
            statement.setInt(2, 1);
            statement.setInt(3, (int) Double.parseDouble(sum.substring(sum.indexOf(": ")+2)));
        }
        try {
            statement.executeUpdate();
            conn.commit();
            conn = null;
            return true;
        }
        catch (SQLException e) {
            conn.rollback();
            conn = null;
            return false;
        }
    }
    public List<OrderProduct> getOrders() throws SQLException, ClassNotFoundException{
        geDBConnection();
        String sql = "select * from orders \n" +
                "left join\n" +
                "status_of_orders \n" +
                "on orders.id_status_order = status_of_orders.id_status \n" +
                "where orders.id_user = ?";
        PreparedStatement prst = conn.prepareStatement(sql);
        prst.setInt(1, HomePageController.user.id);
        ResultSet res = prst.executeQuery();
        List<OrderProduct> l = new ArrayList<OrderProduct>();
        while(res.next()){
           l.add(new OrderProduct(res.getInt("id_order"), res.getString("status_name"), res.getInt("cost_order")));
        }
        conn = null;
        return l;
    }
    public ObservableList<String> getManufacturer() throws SQLException, ClassNotFoundException {
        ArrayList<String> manufacturerList = new ArrayList<>();
        String sql = "SELECT DISTINCT manufacturer FROM products";
        geDBConnection();
        Statement statement = conn.createStatement();
        ResultSet res = statement.executeQuery(sql);
        manufacturerList.add("все производителя");
        while (res.next()){
            manufacturerList.add(res.getString("manufacturer"));
        }
        conn = null;
        return FXCollections.observableArrayList(manufacturerList);
    }
    public ObservableList<Schedule> getSchedules() throws ClassNotFoundException, SQLException{
        geDBConnection();
        List<Schedule> sl = new ArrayList<Schedule>();
        String sql = "SELECT * \n" +
                "FROM schedule \n" +
                "where date_schedule > now() and\n" +
                "not exists (\n" +
                "select id_schedule \n" +
                "from test_drives \n" +
                "where test_drives.id_schedule = schedule.id_schedule)\n" +
                "order by date_schedule asc  limit 42;";
        Statement statement = conn.createStatement();
        ResultSet res = statement.executeQuery(sql);
        while (res.next()){
            java.sql.Timestamp sq= res.getTimestamp("date_schedule");
            DateTimeFormatter dt = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.forLanguageTag("ru-RU"));
            Schedule s = new Schedule((sq.toLocalDateTime()).format(dt), res.getInt("id_schedule"));
            sl.add(s);
        }
        conn = null;
        return FXCollections.observableArrayList(sl);
    }
    public ObservableList<Schedule> getUserSchedules() throws ClassNotFoundException, SQLException{
        geDBConnection();
        List<Schedule> sl = new ArrayList<Schedule>();
        String sql = "SELECT schedule.date_schedule, schedule.id_schedule FROM test_drives, schedule  where id_user = ? and schedule.id_schedule = test_drives.id_schedule";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, HomePageController.user.id);
        ResultSet res = statement.executeQuery();
        while (res.next()){
            java.sql.Timestamp sq= res.getTimestamp("date_schedule");
            DateTimeFormatter dt = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.forLanguageTag("ru-RU"));
            Schedule s = new Schedule((sq.toLocalDateTime()).format(dt), res.getInt("id_schedule"));
            sl.add(s);
        }
        conn = null;
        return FXCollections.observableArrayList(sl);
    }
    public void addSchedule(Schedule schedule) throws ClassNotFoundException, SQLException{
        geDBConnection();
        String sql = "insert into test_drives (id_user, id_schedule) values(?,?)";
        PreparedStatement prst = conn.prepareStatement(sql);
        prst.setInt(1,HomePageController.user.id);
        prst.setInt(2, schedule.id);
        prst.executeUpdate();
        conn = null;
    }
    public void refreshProduct(Product product) throws  SQLException, ClassNotFoundException{
        geDBConnection();
        String sql = "Update products set name_product = ?, description_product = ?, price_product = ?, size = ?, quantity = ? where id_product = ?";
        PreparedStatement p = conn.prepareStatement(sql);
        p.setString(1, product.name);
        p.setString(2, product.description);
        p.setInt(3, (int) product.price);
        p.setString(4, product.size);
        p.setInt(5, product.amount);
        p.setInt(6, product.id);
        p.executeUpdate();
        conn = null;
    }
    public void addNewProduct(Product product) throws  ClassNotFoundException, SQLException{
        String sql = "INSERT INTO products (name_product, description_product, price_product, manufacturer, discount, photo_product, size, quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        geDBConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, product.name);
        pstmt.setString(2, product.description);
        pstmt.setInt(3, (int) product.price);
        pstmt.setString(4, product.manufacturer);
        pstmt.setInt(5, (int) product.discount);
        pstmt.setBytes(6, product.photo);
        pstmt.setString(7, product.size);
        pstmt.setFloat(8, product.amount);
        pstmt.executeUpdate();
        conn = null;
    }
    public void deleteProduct(Product product) throws ClassNotFoundException, SQLException{
        geDBConnection();
        String sql = "delete from products where id_product = ?";
        PreparedStatement p = conn.prepareStatement(sql);
        p.setInt(1, product.id);
        p.executeUpdate();
        conn = null;
    }
}
