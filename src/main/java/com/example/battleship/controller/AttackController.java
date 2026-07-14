package com.example.battleship.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class AttackController {

    @FXML
    private GridPane seaMapGrid;

    private Button btn;


    public void initialize(){

        for (int i=0; i<10; i++) {

            for (int j=0; j<10; j++) {
                Button btn = new Button();
                btn.setPrefHeight(39);
                btn.setPrefWidth(39);
                btn.setStyle("-fx-background-radius: 0");
                btn.setOpacity(0.40);
                seaMapGrid.add(btn, j, i);
            }
        }
    }


}
