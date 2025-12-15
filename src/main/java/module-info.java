module com.example.batallanaval {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.batallanaval to javafx.fxml;
    exports com.example.batallanaval;
    exports com.example.batallanaval.view;
    opens com.example.batallanaval.view to javafx.fxml;
    exports com.example.batallanaval.controller;
    opens com.example.batallanaval.controller to javafx.fxml;
    exports com.example.batallanaval.model;
    opens com.example.batallanaval.model to javafx.fxml;
}