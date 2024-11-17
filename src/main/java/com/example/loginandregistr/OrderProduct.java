package com.example.loginandregistr;

public class OrderProduct {
    int id;
    String status;
    int sum;

    public int getId(){
        return this.id;
    }
    public String getStatus(){
        return this.status;
    }
    public int getSum(){
        return this.sum;
    }
    public OrderProduct(int id, String status, int sum){
        this.id = id;
        this.sum = sum;
        this.status = status;
    }
}
