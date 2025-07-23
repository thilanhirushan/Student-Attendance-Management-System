package com.sams.sams.view;

import com.sams.sams.dao.UserDAO;
import com.sams.sams.model.User;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class LoginView extends Application {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public void start(Stage stage) {
        Label title = new Label("🔐 Login");
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");

        usernameField.setPromptText("Username");
        passwordField.setPromptText("Password");

        Label messageLabel = new Label();

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            User user = userDAO.authenticate(username, password);
            if (user != null) {
                messageLabel.setText("✅ Welcome, " + user.getUsername() + " (" + user.getRole() + ")");

                // Close login window
                stage.close();

                // Open dashboard based on role
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
        });

        VBox root = new VBox(10, title, usernameField, passwordField, loginButton, messageLabel);
        root.setStyle("-fx-padding: 20");

        Scene scene = new Scene(root, 400, 250);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
