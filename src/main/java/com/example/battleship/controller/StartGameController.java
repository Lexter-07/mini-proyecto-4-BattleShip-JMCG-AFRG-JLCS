package com.example.battleship.controller;

import com.example.battleship.view.Path;
import com.example.battleship.view.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import java.io.IOException;

public class StartGameController {

    @FXML
    private Rectangle Ship2;

    private double xOffset = 0;
    private double yOffset = 0;



    private void moveShip(MouseEvent event){
        Node node = (Node) event.getSource();
        node.setTranslateX(event.getSceneX() - xOffset);
        node.setTranslateY(event.getSceneY() - yOffset);
    }

    private void pressedShip(MouseEvent event){
        xOffset = event.getSceneX() - ((Node)event.getSource()).getTranslateX();
        yOffset = event.getSceneY() - ((Node)event.getSource()).getTranslateY();
    }


    @FXML
    public void onHandleDraggedShip2(MouseEvent event) {
       moveShip(event);
    }
    @FXML
    public void onHandlePressedShip2(MouseEvent event) {
        pressedShip(event);
    }


    @FXML
    void onHandleDragged(MouseEvent event) {
        moveShip(event);
    }
    @FXML
    void onHandlePressedShip(MouseEvent event) {
        pressedShip(event);
    }


    @FXML
    void onHandleConfirm(ActionEvent event) throws IOException {
        SceneManager.changeScene(Path.attackView);
    }

}
