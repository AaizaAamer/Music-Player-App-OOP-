package com.example.guiproject3;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class MoodSelectionPage extends FrontNavigationController implements BackNavigationController {

    @FXML
    private Button happyButton,sadButton,energeticButton,calmButton;
    @FXML
    private Button backButton;

    @FXML
    public void initialize() {

        // Button click actions by using the Enum Mood
        happyButton.setOnAction(event -> handleMoodSelection(String.valueOf(Mood.HAPPY)));
        sadButton.setOnAction(event -> handleMoodSelection(String.valueOf(Mood.SAD)));
        energeticButton.setOnAction(event -> handleMoodSelection(String.valueOf(Mood.ENERGETIC)));
        calmButton.setOnAction(event -> handleMoodSelection(String.valueOf(Mood.CALM)));

        // Back button action using the implemented goBack method
        backButton.setOnAction(event -> goBack());
    }

    private void handleMoodSelection(String mood) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MusicPlayerPage.fxml"));
            Scene musicPlayerScene = new Scene(loader.load());
            MusicPlayerController controller = loader.getController();

            controller.setMoodDirectory(mood);

            Stage currentStage = (Stage) happyButton.getScene().getWindow();
            currentStage.setScene(musicPlayerScene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load music player for mood: " + mood);
        }
    }

    @Override
    public void goBack() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            navigateTo("GenreAndMoodPageController-view.fxml",stage);
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
