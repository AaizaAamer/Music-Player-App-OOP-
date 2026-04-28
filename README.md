****Music Player App (OOP)****
**Summary:**
This project is a Java-based Music Player Application developed using Object-Oriented Programming (OOP) principles and JavaFX for the graphical user interface (GUI). It provides a complete music experience where users can play songs, manage playlists, explore music by mood and genre, like songs, and authenticate through login/signup functionality.

**Features:**
1. Play, pause, next, previous, and reset music playback
2. Playlist management (Liked Songs & My Playlist)
3.  Genre-based music selection
4. Login and Signup system with validation
5. Add/remove songs from playlists
6. Song metadata display (Title, Artist, Album, Duration)
7. Playback speed control
8. Volume control slider
9. Keyboard shortcuts for music control (space, arrows, etc.)
10. Modern JavaFX GUI designed using SceneBuilder
    
**Technologies Used:**
1. Java 
2. JavaFX 
3. FXML (SceneBuilder UI Design)
4. File Handling 

**OOP Concepts Used:**
This project is built using the following Object-Oriented Programming principles:
1. Encapsulation: Song, LoginController, MusicPlayerController
2. Abstraction: LoginPage, BackNavigationController
3. Inheritance: All controllers extending FrontNavigationController
4. Polymorphism: goBack() method overridden in controllers
5. Exception Handling: InvalidSongDurationException
6. Composition: MusicPlayerController with MediaPlayer, Song, File

**Code Location**
The backend (Java logic) is located in:
src/main/java/com/example/guiproject3
The frontend (FXML UI designed using SceneBuilder) is located in:
src/main/resources/com/example/guiproject3

