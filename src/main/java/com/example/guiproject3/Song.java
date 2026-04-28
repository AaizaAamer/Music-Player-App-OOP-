package com.example.guiproject3;

import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.ID3v2;

public class Song {
    private String title;
    private String artist;
    private String duration;
    private String album;

    public Song(){
        setTitle("Unknown Title");
        setArtist("Unknown Artist");
        setAlbum("Unknown Album");
        setDuration("Unknown Duration");
    }

    public Song(String filePath) throws Exception {
        this();

        Mp3File mp3File = new Mp3File(filePath);
        ID3v2 id3v2Tag = mp3File.getId3v2Tag();

        if (id3v2Tag != null) {
            setTitle(id3v2Tag.getTitle());
            setArtist(id3v2Tag.getArtist());
            setAlbum(id3v2Tag.getAlbum());
        }

        // Get duration in seconds
        long durationInSeconds = mp3File.getLengthInSeconds();

        // Check for invalid duration and throw a custom exception
        if (durationInSeconds <= 0 || durationInSeconds > 600) { // 600 seconds = 10 minutes
            throw new InvalidSongDurationException("Invalid song duration: " + durationInSeconds + " seconds.");
        }

        // Convert duration to a formatted string (MM:SS)
        this.duration = String.format("%02d:%02d", durationInSeconds / 60, durationInSeconds % 60);
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setDuration(String duration) {this.duration = duration; }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return duration;
    }

    public String getAlbum() {return album; }


    public void displaySongDetails() {
        System.out.println("Title: " + getTitle());
        System.out.println("Artist: " + getArtist());
        System.out.println("Album: " + getAlbum());
        System.out.println("Duration: " + getDuration());
    }
}


