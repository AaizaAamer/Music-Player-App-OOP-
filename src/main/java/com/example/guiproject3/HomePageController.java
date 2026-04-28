package com.example.guiproject3;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HomePageController extends FrontNavigationController {

    @FXML
    private Button playButtonHomePg, viewPlaylistsButton;

    @FXML
    public void handlePlayButtonHomePg() {
        try {
            // Navigate to Genre and Mood Selection Screen
            Stage stage = (Stage) playButtonHomePg.getScene().getWindow();
            navigateTo("GenreAndMoodPageController-view.fxml", stage);
        } catch (Exception e) {
            showAlert("Navigation Error", "Unable to load Genre and Mood Selection screen.");
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void handleViewPlaylistsButton() {
        try {
            // Navigate to Playlists Screen
            Stage stage = (Stage) viewPlaylistsButton.getScene().getWindow();
            navigateTo("PlaylistSelectionController-view.fxml", stage);
        } catch (Exception e) {
            showAlert("Navigation Error", "Unable to load Playlists screen.");
            System.out.println(e.getMessage());
        }
    }

     private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
