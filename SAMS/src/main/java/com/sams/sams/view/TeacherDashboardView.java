package com.sams.sams.view;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TeacherDashboardView {
    public static void launchDashboard() {
        Stage stage = new Stage();

        Label welcomeLabel = new Label("👩‍🏫 Welcome, Teacher");
        Button attendanceBtn = new Button("Mark/View Attendance");
        Button summaryBtn = new Button("View Attendance Summary");
        Button logoutBtn = new Button("Logout");

        attendanceBtn.setOnAction(e -> new AttendanceView().start(new Stage()));
        summaryBtn.setOnAction(e -> new AttendanceSummaryView().start(new Stage()));

        logoutBtn.setOnAction(e -> {
            if (showConfirmation("Are you sure you want to logout?")) {
                stage.close();
                try {
                    new LoginView().start(new Stage()); // 👈 Open LoginView properly
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        VBox root = new VBox(10, welcomeLabel, attendanceBtn, summaryBtn, logoutBtn);
        root.setStyle("-fx-padding: 20");

        Scene scene = new Scene(root, 300, 250);
        stage.setScene(scene);
        stage.setTitle("Teacher Dashboard");
        stage.show();
    }

    private static boolean showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.showAndWait();
        return alert.getResult() == ButtonType.YES;
    }
}
