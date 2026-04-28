package com.example.guiproject3;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class GenreSelectionPage extends FrontNavigationController implements BackNavigationController {

    @FXML
    private Button rockButton,jazzButton,classicButton,popButton;
    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        // Button click actions
        rockButton.setOnAction(event -> handleGenreSelection(String.valueOf(Genre.ROCK)));
        jazzButton.setOnAction(event -> handleGenreSelection(String.valueOf(Genre.JAZZ)));
        classicButton.setOnAction(event -> handleGenreSelection(String.valueOf(Genre.CLASSIC)));
        popButton.setOnAction(event -> handleGenreSelection(String.valueOf(Genre.POP)));

        // Back button action using the implemented goBack method
        backButton.setOnAction(event -> goBack());
    }

    private void handleGenreSelection(String genre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MusicPlayerPage.fxml"));
            Scene musicPlayerScene = new Scene(loader.load());
            MusicPlayerController controller = loader.getController();

            // Pass the selected genre to the MusicPlayerController
            controller.setGenreDirectory(genre);

            // Navigate to the Music Player screen
            Stage currentStage = (Stage) rockButton.getScene().getWindow();
            currentStage.setScene(musicPlayerScene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load music player for genre: " + genre);
        }
    }

    @Override
    public void goBack() {
        try{
            navigateTo("GenreAndMoodPageController-view.fxml" , (Stage) backButton.getScene().getWindow());
        }catch(Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to Go Back to HomePage: ");
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
