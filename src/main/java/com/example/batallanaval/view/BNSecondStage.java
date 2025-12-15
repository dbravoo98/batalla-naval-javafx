package com.example.batallanaval.view;

import com.example.batallanaval.controller.BNSecondController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author David AlejandroBravo Ospina
 * @version 1.0.0
 * @since 09-12-2025
 */

public class BNSecondStage extends Stage {

    public BNSecondStage(String nickname, boolean cargar) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/batallanaval/Second-view.fxml")
        );

        Parent root = loader.load();

        BNSecondController controller = loader.getController();
        controller.initGame(nickname, cargar);

        setScene(new Scene(root));
        setTitle("Batalla Naval");
        setResizable(false);

        getIcons().add(new Image(
                String.valueOf(getClass().getResource(
                        "/com/example/batallanaval/images/logoBN.png"))
        ));

        show();
    }
}
