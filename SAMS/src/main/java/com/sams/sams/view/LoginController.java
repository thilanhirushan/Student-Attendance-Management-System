package com.sams.sams.view;

import com.sams.sams.dao.UserDAO;
import com.sams.sams.model.User;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController extends Application {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = userDAO.authenticate(username, password);
        if (user != null) {
            messageLabel.setText("✅ Welcome, " + user.getUsername() + " (" + user.getRole() + ")");

            // Close login window
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.close();

            // Open dashboard
            if (user.getRole().equalsIgnoreCase("admin")) {
                AdminDashboardView.launchDashboard();
            } else if (user.getRole().equalsIgnoreCase("teacher")) {
                TeacherDashboardView.launchDashboard();
            } else {
                showAlert("Unknown role: " + user.getRole());
            }

        } else {
            messageLabel.setText("❌ Invalid username or password.");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
