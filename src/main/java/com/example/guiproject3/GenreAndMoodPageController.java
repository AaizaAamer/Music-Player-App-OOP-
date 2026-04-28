package com.example.guiproject3;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class GenreAndMoodPageController extends FrontNavigationController implements BackNavigationController {

    @FXML
    private Button chooseGenreButton , chooseMoodButton;
    @FXML
    private Button backButton;

    // Initialize method to style buttons
    @FXML
    public void initialize() {
        backButton.setOnAction(event -> goBack());
    }

    @FXML
    private void onChooseGenreClicked() {
        try {
            Stage stage = (Stage) chooseGenreButton.getScene().getWindow();
            navigateTo("GenreSelectionPage-view.fxml", stage);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Error in screen Navigation to GenrePage...", Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onChooseMoodClicked() {
        try {
            Stage stage = (Stage) chooseMoodButton.getScene().getWindow();
            navigateTo("MoodSelectionPage-view.fxml", stage);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Error in screen Navigation to MoodPage...", Alert.AlertType.ERROR);
        }
    }
    @FXML
    @Override
    public void goBack() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            navigateTo("HomePageController-view.fxml", stage);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to go back to HomePage.", Alert.AlertType.ERROR);
        }
    }

    // Shows an alert in case of errors
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
