package com.example.guiproject3;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class WelcomeController extends FrontNavigationController {

    @FXML
    private Button btnLetsGo;

    @FXML
    public void handleLetsGoClick() {
        System.out.println("Let's Go button clicked!");
        navigateTo("LoginController-view.fxml", (Stage) btnLetsGo.getScene().getWindow());
    }
}

