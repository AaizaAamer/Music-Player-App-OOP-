package com.example.guiproject3;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class PlaylistSelectionController  extends FrontNavigationController implements BackNavigationController {

    @FXML
    private Button backButton,likedSongsButton,myPlaylistButton;

    @FXML
    public void initialize() {

        // "Play Liked Songs Playlist"
        likedSongsButton.setOnAction(event -> handlePlaylistSelection("Liked Songs"));

        // "Play My Playlist"
        myPlaylistButton.setOnAction(event -> handlePlaylistSelection("My Playlist"));

//        // for Back Button
        backButton.setOnAction(event -> goBack());
    }

    private void handlePlaylistSelection(String playlistType) {
        try {
            // Load the Music Player scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MusicPlayerPage.fxml"));
            Scene musicPlayerScene = new Scene(loader.load());

            // Get the controller of the Music Player
            MusicPlayerController musicPlayerController = loader.getController();

            // Set the directory based on the playlist type
            if (playlistType.equals("Liked Songs")) {
                musicPlayerController.setLikedSongsDirectory("Liked Songs");
            } else if (playlistType.equals("My Playlist")) {
                musicPlayerController.setMyPlaylistDirectory("My Playlist");
            }

            // Switch to the Music Player scene
            Stage currentStage = (Stage) likedSongsButton.getScene().getWindow();
            currentStage.setScene(musicPlayerScene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load the Music Player.");
        }
    }

    @Override
    public void goBack() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            navigateTo("HomePageController-view.fxml",stage);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Error navigating back to the previous page.");
        }
    }

    private void showAlert(String title, String message) {
        // Display an alert in case of an error
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
