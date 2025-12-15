package com.example.batallanaval.controller;

import com.example.batallanaval.view.BNSecondStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * @author David AlejandroBravo Ospina
 * @version 1.0.0
 * @since 09-12-2025
 */

public class BNFirstController {

    @FXML
    private void ClickNewGame(ActionEvent event) throws IOException {

        Stage firstStage = (Stage) ((Node) event.getSource())
                .getScene().getWindow();
        firstStage.close();

        boolean cargar = preguntarCargar();
        String nickname = pedirNickname();

        new BNSecondStage(nickname, cargar);
    }

    private boolean preguntarCargar() {

        File save = new File("batallanaval.save");
        if (!save.exists()) return false;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Partida guardada");
        alert.setHeaderText("Se encontró una partida guardada");
        alert.setContentText("¿Deseas cargarla o iniciar una nueva?");

        ButtonType cargarBtn = new ButtonType("Cargar");
        ButtonType nuevoBtn = new ButtonType("Nuevo juego");

        alert.getButtonTypes().setAll(cargarBtn, nuevoBtn);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == cargarBtn;
    }

    private String pedirNickname() {

        TextInputDialog dialog = new TextInputDialog("Jugador");
        dialog.setTitle("Nuevo juego");
        dialog.setHeaderText("Ingresa tu nickname");
        dialog.setContentText("Nickname:");

        return dialog.showAndWait()
                .filter(s -> !s.trim().isEmpty())
                .orElse("Jugador");
    }
}
