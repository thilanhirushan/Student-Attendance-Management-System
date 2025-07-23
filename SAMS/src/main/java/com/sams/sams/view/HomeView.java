package com.sams.sams.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

public class HomeView extends Application {

    @Override
    public void start(Stage stage) {
        // 👉 Load Logo
        Image logo = new Image(getClass().getResourceAsStream("/logo.png")); // path from resources
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(150);
        logoView.setPreserveRatio(true);

        // 🔄 Fade-in animation for logo
        FadeTransition fadeLogo = new FadeTransition(Duration.seconds(2), logoView);
        fadeLogo.setFromValue(0.0);
        fadeLogo.setToValue(1.0);
        fadeLogo.play();

        Label welcomeLabel = new Label("🎓 Welcome to Student Attendance Management System");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button proceedButton = new Button("🚀 Proceed to Login");
        proceedButton.setStyle("-fx-font-size: 14px;");

        // 🔄 Slide-up animation for button
        TranslateTransition slideIn = new TranslateTransition(Duration.seconds(1.5), proceedButton);
        slideIn.setFromY(50);
        slideIn.setToY(0);
        slideIn.play();

        proceedButton.setOnAction(e -> {
            try {
                new LoginView().start(new Stage());
                stage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox root = new VBox(20, logoView, welcomeLabel, proceedButton);
        root.setStyle("-fx-padding: 40; -fx-alignment: center;");

        Scene scene = new Scene(root, 700, 550);
        stage.setScene(scene);
        stage.setTitle("🏠 Home");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
