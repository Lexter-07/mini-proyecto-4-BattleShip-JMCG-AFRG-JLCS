module com.example.battleship {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.battleship to javafx.fxml;
    opens com.example.battleship.controller to javafx.fxml;
    opens com.example.battleship.view.fx to javafx.fxml;
    exports com.example.battleship;
}