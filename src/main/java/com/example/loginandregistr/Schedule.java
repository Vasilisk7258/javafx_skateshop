package com.example.loginandregistr;

import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import java.sql.SQLException;

public class Schedule {
    String time;
    Button addButton;
    boolean clicked = false;
    public int id;

    public Schedule(String time, int id){
        this.time = time;
        this.id = id;
        this.addButton = new Button("записаться");
        addButton.setPrefWidth(200);
        addButton.getStyleClass().add("buttons_pm");
        addButton.setPrefHeight(26);
        addButton.setOnAction(event -> {
           this.clicked = true;
           System.out.println(addButton.getScene().lookup("#tableSchedule"));
            System.out.println(addButton.getScene().lookup("#usersSchedule"));
            TableView<Schedule> t = (TableView<Schedule>) this.addButton.getScene().lookup("#tableSchedule");
            TableView<Schedule> u = (TableView<Schedule>) this.addButton.getScene().lookup("#usersSchedule");
            for (Schedule item: t.getItems()){
                if (item.clicked) {
                    u.getItems().add(item);
                    t.getItems().remove(item);
                    DB db = new DB();
                    try {
                        db.addSchedule(this);
                    }
                    catch (SQLException | ClassNotFoundException e){
                        e.printStackTrace();
                    }
                    break;
                }
            }
        });
    }
    public String getTime(){
        return  this.time;
    }
    public Button getAddButton(){
        return this.addButton;
    }
}
