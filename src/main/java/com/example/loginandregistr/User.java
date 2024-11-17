package com.example.loginandregistr;

import javafx.collections.ObservableList;

import java.util.ArrayList;

public class User {
    public String name;
    public String surname;
    public ObservableList<ProductInCart> cart;
    public int id;
    public int idRole;
    public String search = "";
    public String manufacturer = "";
    public String userMail = "";

    public User(String name, String surname, int id, int idRole) {
        this.name = name;
        this.surname = surname;
        this.cart = null;
        this.id = id;
        this.idRole = idRole;
    }
    public ArrayList<Product> getCarts(){
        ArrayList<Product> pr = new ArrayList<Product>();
        for(ProductInCart item: cart){
            pr.add(item.product);
        }
        return pr;
    }
    public boolean isAdmin(){
        return this.idRole==1;
    }
}
