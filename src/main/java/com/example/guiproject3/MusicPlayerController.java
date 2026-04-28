package com.example.guiproject3;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayerController implements BackNavigationController {

    @FXML
    private Pane pane;
    @FXML
    private Label songLabel;
    @FXML
    private Button playButton, pauseButton, resetButton, previousButton, nextButton;
    @FXML
    private Button backButton;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ProgressBar songProgressBar;
    @FXML
    private ListView<String> songListView;

    private Media media;
    private MediaPlayer mediaPlayer;
    private Timer timer;
    private TimerTask task;

    private File directory;
    private File[] files;
    private File likedSongsDirectory;
    private File myPlaylistDirectory;

    private ArrayList<File> songs;
    private ArrayList<String> artists;
    private ArrayList<String> albums;
    private ArrayList<String> durations;

    private int songNumber = 0;
    private double[] speeds = {0.25, 0.5, 0.75, 1.0, 1.25, 1.5, 1.75, 2.0};

    private boolean running;   //for checking the progress bar
    private boolean isPlaying = false;  // for checking the song played or not

    @FXML
    public void initialize() {
        // Ensure pane has focus
        Platform.runLater(() -> {
            pane.requestFocus(); // Force focus on pane
            pane.setOnMouseClicked(event -> pane.requestFocus()); // Request focus on mouse click

            // Debug Scene attachment
            if (pane.getScene() != null) {
                System.out.println("Scene is ready: " + pane.getScene());

                // Set up keyboard shortcuts
                pane.getScene().setOnKeyPressed(event -> {
                    System.out.println("Key Pressed: " + event.getCode());
                    switch (event.getCode()) {
                        case SPACE:
                            if (isPlaying) {
                                pauseMedia();
                            } else {
                                playMedia();
                            }
                            break;
                        case ENTER:
                            int selectedIndex = songListView.getSelectionModel().getSelectedIndex();
                            if (selectedIndex != -1) {
                                songNumber = selectedIndex;
                                changeSong();
                            }
                            break;
                        case LEFT:
                            previousMedia();
                            break;
                        case RIGHT:
                            nextMedia();
                            break;
                        case UP:
                            volumeSlider.setValue(volumeSlider.getValue() + 10);
                            break;
                        case DOWN:
                            volumeSlider.setValue(volumeSlider.getValue() - 10);
                            break;
                        default:
                            break;
                    }
                });
            } else {
                System.out.println("Scene is not ready.");
            }
        });

        // Ensure other components don't steal focus
        playButton.setFocusTraversable(false);
        pauseButton.setFocusTraversable(false);
        resetButton.setFocusTraversable(false);
        previousButton.setFocusTraversable(false);
        nextButton.setFocusTraversable(false);
        speedBox.setFocusTraversable(false);
        volumeSlider.setFocusTraversable(false);

        // Initialize other components
        songs = new ArrayList<>();
        artists = new ArrayList<>();
        albums = new ArrayList<>();
        durations = new ArrayList<>();
        likedSongsDirectory = new File("Playlists/Liked Songs");
        myPlaylistDirectory = new File("Playlists/My Playlist");


        if (!likedSongsDirectory.exists()) {
            likedSongsDirectory.mkdirs();
        }
        if (!myPlaylistDirectory.exists()) {
            myPlaylistDirectory.mkdirs();
        }

        for (double speed : speeds) {
            speedBox.getItems().add(speed + "x");
        }
        speedBox.setOnAction(this::changeSpeed);
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> mediaPlayer.setVolume(newValue.doubleValue() / 100));
        songProgressBar.setOnMouseClicked(event -> {
            if (mediaPlayer != null) {
                double clickX = event.getX();
                double progress = clickX / songProgressBar.getWidth();
                mediaPlayer.seek(Duration.seconds(progress * media.getDuration().toSeconds()));
            }
        });

        songListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        songListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click to play
                int selectedIndex = songListView.getSelectionModel().getSelectedIndex();
                if (selectedIndex != -1) {
                    songNumber = selectedIndex;
                    changeSong();
                }
            }
        });
    }

    // The methods accepts the genre/mood/liked/Myplaylist directory as an argument
    public void setGenreDirectory(String genre) {
        // Assuming the genre folders are under the "music" directory
        this.directory = new File("music/" + genre);
        loadSongs(); // Load the songs for the specified genre
    }

    public void setMoodDirectory(String mood) {
        this.directory = new File("music/" + mood);
        loadSongs(); // Load the songs
    }

    public void setLikedSongsDirectory(String playlistType) {
        this.directory = new File("Playlists/" + playlistType);
        loadSongs();
    }

    public void setMyPlaylistDirectory(String playlistType) {
        this.directory = new File("Playlists/" + playlistType);
        loadSongs(); // Load the songs
    }

    // Load songs from the specific directory
    private void loadSongs() {
        if (!directory.exists()) {
            System.out.println("Directory not found: " + directory.getName());
            return;
        }

        files = directory.listFiles();
        if (files != null) {
            songs.clear();
            songListView.getItems().clear(); // Clear the ListView
            for (File file : files) {
                if (file.getName().endsWith(".mp3")) {
                    try {
                        Song song = new Song(file.getAbsolutePath());
                        songs.add(file);
                        songListView.getItems().add(file.getName()); // Add song name to ListView
                        artists.add(song.getArtist());
                        albums.add(song.getAlbum());
                        durations.add(song.getDuration());
                    } catch (Exception e) {
                        System.out.println("Error reading song metadata: " + e.getMessage());
                    }
                }
            }
        }

        if (!songs.isEmpty()) {
            loadSong(); // Load the first song
        } else {
            System.out.println("No songs found in the directory.");
        }
    }

    private void loadSong() {
        try {
            if (songs.isEmpty()) {
                songLabel.setText("No songs available.");
                return;
            }
            File currentFile = songs.get(songNumber);
            System.out.println("Loading song: " + currentFile.getName());

            media = new Media(currentFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setOnReady(() -> {
                try {
                    updateSongDetails(); // Update song details when ready
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                playMedia(); // Auto-play the loaded song
            });

            mediaPlayer.setOnError(() -> {
                System.out.println("Error loading song: " + currentFile.getName());
                showAlert("Load Error", "Failed to load song: " + currentFile.getName(), Alert.AlertType.ERROR);
                nextMedia(); // Automatically skip to the next song
            });

            mediaPlayer.setOnEndOfMedia(this::nextMedia); // Play next song when current ends
        } catch (Exception e) {
            System.out.println("Error loading song: " + e.getMessage());
            e.printStackTrace();
            nextMedia(); // Skip to the next song if loading fails
        }
    }

    private void updateSongDetails() throws Exception {
        if (songs.isEmpty()) {
            songLabel.setText("No song selected.");
            return;
        }

        File currentFile = songs.get(songNumber);

        try {
            Song currentSong = new Song(currentFile.getAbsolutePath());
            currentSong.displaySongDetails();
            songLabel.setText(
                    "Song: " +  songs.get(songNumber).getName() +
                            " | Artist: " + currentSong.getArtist() +
                            " | Album: " + currentSong.getAlbum() +
                            " | Duration: " + currentSong.getDuration()
            );
        } catch (Exception e) {
            System.out.println("Error updating song details: " + e.getMessage());
        }
    }

    private void addToPlaylist(File song, File playlistDirectory) {
        try {
            // Destination file in the target directory
            File destination = new File(playlistDirectory, song.getName());

            if (!destination.exists()) {
                Files.copy(song.toPath(), destination.toPath());
                System.out.println("Song added to " + playlistDirectory.getName());
                showAlert("AddToPlaylist","Song is added in the Playlist", Alert.AlertType.INFORMATION);
            } else {
                System.out.println("Song already exists in " + playlistDirectory.getName());
                showAlert("AddToPlaylist","Song is already in the Playlist", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            System.out.println("Error adding song to playlist: " + e.getMessage());
        }
    }

    private void removeFromPlaylist(File song, File playlistDirectory) {
        try {
            // Target file in the playlist
            File target = new File(playlistDirectory, song.getName());

            if (target.exists()) {
                // Delete the file
                if (target.delete()) {
                    System.out.println("Song removed from " + playlistDirectory.getName());
                    showAlert("RemoveFromPlaylist","Song is removed from the Playlist", Alert.AlertType.INFORMATION);
                } else {
                    System.out.println("Failed to remove song from " + playlistDirectory.getName());
                }
            } else {
                System.out.println("Song not found in " + playlistDirectory.getName());
                showAlert("RemoveFromPlaylist","Song is not found in the Playlist", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            System.out.println("Error removing song from playlist: " + e.getMessage());
        }
    }

    public void handleAddToLikedSongs() {
        if (!songs.isEmpty()) {
            File currentSong = songs.get(songNumber); // Current playing song
            addToPlaylist(currentSong, likedSongsDirectory);
        } else {
            System.out.println("No song selected to add.");
        }
    }

    public void handleAddToMyPlaylist() {
        if (!songs.isEmpty()) {
            File currentSong = songs.get(songNumber); // Current playing song
            addToPlaylist(currentSong, myPlaylistDirectory);
        } else {
            System.out.println("No song selected to add.");
        }
    }

    public void handleRemoveFromLikedSongs() {
        if (!songs.isEmpty()) {
            File currentSong = songs.get(songNumber); // Current playing song
            removeFromPlaylist(currentSong, likedSongsDirectory);
        } else {
            System.out.println("No song selected to remove.");
        }
    }

    public void handleRemoveFromMyPlaylist() {
        if (!songs.isEmpty()) {
            File currentSong = songs.get(songNumber); // Current playing song
            removeFromPlaylist(currentSong, myPlaylistDirectory);
        } else {
            System.out.println("No song selected to remove.");
        }
    }

    public void playMedia() {
        if (mediaPlayer == null) {
            System.out.println("MediaPlayer is not initialized. Cannot play media.");
            return;
        }

        isPlaying = true;  // Mark as playing
        beginTimer();
        changeSpeed(null);  // Adjust playback speed
        mediaPlayer.setVolume(volumeSlider.getValue() / 100);
        mediaPlayer.play();
    }

    public void pauseMedia() {
        if (mediaPlayer != null) {
            cancelTimer();
            mediaPlayer.pause();
            isPlaying = false;  // Mark as paused
        }
    }

    public void resetMedia() {
        if (mediaPlayer != null) {
            songProgressBar.setProgress(0);
            mediaPlayer.seek(Duration.seconds(0));
            isPlaying = false;  // Mark as reset
        }
    }

    public void previousMedia() {
        if (!songs.isEmpty()) {
            songNumber = (songNumber - 1 + songs.size()) % songs.size();
            changeSong();
        }
    }

    public void nextMedia() {
        if (!songs.isEmpty()) {
            songNumber = (songNumber + 1) % songs.size();
            changeSong();
        }
    }

    private void changeSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();  // Dispose of the old MediaPlayer for cleaning the memory
        }
        cancelTimer(); // Cancel any running timer
        loadSong(); // Load the new song
        playMedia(); // Start playing the new song
    }

    public void changeSpeed(ActionEvent event) {
        if (speedBox.getValue() != null && mediaPlayer != null) {
            String selectedSpeed = speedBox.getValue().replace("x", ""); // Remove "x" from the selection
            double playbackSpeed = Double.parseDouble(selectedSpeed); // Parse as a double
            mediaPlayer.setRate(playbackSpeed); // Set the rate on the media player
        }
    }

    public void beginTimer() {
        if (mediaPlayer == null || media == null) {
            System.out.println("MediaPlayer or Media is null. Timer will not start.");
            return;
        }
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        if (mediaPlayer != null && media.getDuration() != null) {
                            double current = mediaPlayer.getCurrentTime().toSeconds();
                            double end = media.getDuration().toSeconds();
                            songProgressBar.setProgress(current / end);

                            if (current / end >= 1) {
                                cancelTimer();
                                nextMedia();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error in TimerTask: " + e.getMessage());
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void cancelTimer() {
        running = false;
        if (timer != null) {
            timer.cancel();
            timer.purge();  // Clean up any pending tasks
        }
    }

    @FXML
    private void handleBackButton() {
        pauseMedia();
        goBack();
    }
    @FXML
    @Override
    public void goBack() {
        System.out.println("Back button clicked!");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HomePageController-view.fxml"));
            Scene homeScene = new Scene(loader.load());
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.setScene(homeScene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Error navigating back to the home page.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}