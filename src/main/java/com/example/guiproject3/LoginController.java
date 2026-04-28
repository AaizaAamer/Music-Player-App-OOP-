package com.example.guiproject3;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.*;
import java.util.regex.Pattern;

public class LoginController extends FrontNavigationController implements LoginPage {

    // Constants for file storage
    private static final String USER_DATA_FILE = "user_data.txt";
    private static final String CREDENTIALS_FILE = "credentials.txt";
// Stores login credentials for "Remember Me"

    // FXML fields for the UI elements
    @FXML
    private AnchorPane loginPane, signupPane;
    @FXML
    private TextField loginUsernameField,loginUserEmailField, signupUsernameField, signupEmailField, loginPasswordVisibleField;
    @FXML
    private PasswordField loginPasswordField, signupPasswordField, signupConfirmPasswordField;
    @FXML
    private CheckBox rememberMeCheckBox, showLoginPasswordCheckBox;

    // Called automatically
    @FXML
    public void initialize() {
        createFileIfNotExists(USER_DATA_FILE); // Ensure user data file exists
        createFileIfNotExists(CREDENTIALS_FILE); // Ensure credentials file exists

        loadUserCredentials(); // Load stored credentials on application startup
        showLoginPasswordCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> togglePasswordVisibility(isSelected));
        rememberMeCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> toggleRememberMe(newValue));
    }

    // Switches to the signup and the login view....
    @FXML
    private void switchToSignup() {
        setPaneVisibility(signupPane, loginPane);
    }

    @FXML
    private void switchToLogin() {
        setPaneVisibility(loginPane, signupPane);
    }

    //handle login...
    @Override
    @FXML
    public void handleLogin() {
        String username = loginUsernameField.getText();
        String userEmail = loginUserEmailField.getText();
        String password = loginPasswordField.getText();

        if (username.isEmpty() || userEmail.isEmpty() || password.isEmpty()) {
            showAlert("Login Error", "Username, UserEmail and Password cannot be empty!", Alert.AlertType.ERROR);
        } else if (isValidCredentials(username, userEmail , password)) {
            if (rememberMeCheckBox.isSelected()) {
                // Save credentials if "Remember Me" is checked
                saveUserCredentials(username, userEmail, password,true);
            } else {
                // Clear the credentials if "Remember Me" is unchecked
                clearUserCredentials();
            }
            showAlert("Login Successful", "Welcome, " + username + "!", Alert.AlertType.INFORMATION);

            // Navigate to next HomePage after successful login
            Stage stage = (Stage) loginUserEmailField.getScene().getWindow(); // Get current window
            navigateTo("HomePageController-view.fxml", stage);
        }
        else {
            showAlert("Login Error", "Invalid Username or Email or Password!", Alert.AlertType.ERROR);
        }
    }

    // Handles the signup..
    @Override
    @FXML
    public void handleSignup() {
        String username = signupUsernameField.getText();
        String email = signupEmailField.getText();
        String password = signupPasswordField.getText();
        String confirmPassword = signupConfirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Signup Error", "All fields are required!", Alert.AlertType.ERROR);
        } else if (username.length() < 3 || username.length() > 15) {
            showAlert("Signup Error", "Username not less than 3 or greater than 15!", Alert.AlertType.ERROR);
        } else if (!password.equals(confirmPassword)) {
            showAlert("Signup Error", "Passwords do not match!", Alert.AlertType.ERROR);
        } else if (!isValidEmail(email)) {
            showAlert("Signup Error", "Invalid Email Address!", Alert.AlertType.ERROR);
        } else if (!isValidPassword(password)) {
            showAlert("Signup Error", "Password must be 8-20 characters, include letters and digits.", Alert.AlertType.ERROR);
        } else if (saveUser(username, email, password)) {
            showAlert("Signup Successful", "Account created successfully!", Alert.AlertType.INFORMATION);
            // Automatically Redirect to login view after the successful signup
            switchToLogin();
        }
    }

    //Create file if already not exist
    private void createFileIfNotExists(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Handles forgot password .
    @Override
    @FXML
    public void handleForgotPassword() {
        String userEmail = loginUserEmailField.getText();

        if (userEmail.isEmpty()) {
            showAlert("Forgot Password Error", "Please enter your email or username!", Alert.AlertType.ERROR);
            return;
        }
        String password = retrievePassword(userEmail);
        if (password != null) {
            showAlert("Password Retrieved", "Your password is: " + password, Alert.AlertType.INFORMATION);
        } else {
            showAlert("Forgot Password Error", "No account found for the provided details!", Alert.AlertType.ERROR);
        }
    }

    @Override
    //Retrieves a password from the user data file based on the email or username.
    public String retrievePassword(String Useremail) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[1].trim().equals(Useremail)){
                    return parts[2]; // Return the password
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Saves user credentials to a file.
    private void saveUserCredentials(String username, String userEmail, String password, boolean rememberMe) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CREDENTIALS_FILE))) {
            writer.write(username + "," + userEmail + "," + password + "," + rememberMe);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Clears the user credentials by deleting the file.
    private void clearUserCredentials() {
        File file = new File(CREDENTIALS_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    // Loads user credentials from the file if available.
    private void loadUserCredentials() {
        File file = new File(CREDENTIALS_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                if (line != null) {
                    String[] credentials = line.split(",");
                    if (credentials.length == 4) {
                        loginUsernameField.setText(credentials[0].trim());
                        loginUserEmailField.setText(credentials[1].trim());
                        loginPasswordField.setText(credentials[2].trim());
                        rememberMeCheckBox.setSelected(Boolean.parseBoolean(credentials[3].trim()));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Validates user credentials from the user data file.
    private boolean isValidCredentials(String username, String userEmail, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    if (parts[0].trim().equals(username) && parts[1].trim().equals(userEmail) && parts[2].trim().equals(password)) {
                        System.out.println("Line from file: " + line);
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    //Saves a new user to the user data file.
    @Override
    public boolean saveUser(String username, String email, String password) {
        // Check for duplicates
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[1].trim().equals(email)) {
                    showAlert("Signup Error", "UserEmail already exists! Use another Email", Alert.AlertType.ERROR);
                    return false; // Duplicate found
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save the user if no duplicate found
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_DATA_FILE, true))) {
            writer.write(username + "," + email + "," + password);
            writer.newLine();
            System.out.println("User saved: " + username + ", " + email);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Validates email format using a regex.
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return Pattern.matches(emailRegex, email);
    }

    // Validates password for length and character requirements.

    private boolean isValidPassword(String password) {
        // Check for length (8-20 characters)
        if (password.length() < 8 || password.length() > 20) {
            return false;
        }
        // Check for at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }
        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }
        // Check for at least one digit
        if (!password.matches(".*\\d.*")) {
            return false;
        }
        // Check for at least one special character
        if (!password.matches(".*[!@#$%^&(),.?\":{}|<>].*")) {
            return false;
        }
        return true; // Password meets all criteria
    }


    //Toggles password visibility based on the checkbox state.
    private void togglePasswordVisibility(boolean showPassword) {
        if (showPassword) {
            loginPasswordVisibleField.setText(loginPasswordField.getText());
            loginPasswordVisibleField.setManaged(true);
            loginPasswordVisibleField.setVisible(true);

            loginPasswordField.setManaged(false);
            loginPasswordField.setVisible(false);
        } else {
            loginPasswordField.setText(loginPasswordVisibleField.getText());
            loginPasswordField.setManaged(true);
            loginPasswordField.setVisible(true);

            loginPasswordVisibleField.setManaged(false);
            loginPasswordVisibleField.setVisible(false);
        }
    }

    @FXML
    private void toggleRememberMe(boolean isSelected) {
        rememberMeCheckBox.setSelected(isSelected);

        if (isSelected) {
            // Automatically save credentials if "Remember Me" is checked
            String username = loginUsernameField.getText();
            String userEmail = loginUserEmailField.getText();
            String password = loginPasswordField.getText();
            if (!username.isEmpty() && !password.isEmpty()) {
                saveUserCredentials(username,userEmail, password, true);
            }
        } else {
            // Clear the credentials when "Remember Me" is unchecked
            clearUserCredentials();
        }
    }

    // Displays an alert with the specified title, message, and type.
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Sets the visibility of the login and signup panes.
    private void setPaneVisibility(AnchorPane showPane, AnchorPane hidePane) {
        showPane.setVisible(true);
        showPane.setManaged(true);
        hidePane.setVisible(false);
        hidePane.setManaged(false);
    }
}