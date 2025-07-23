package com.sams.sams;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.sams.sams.view.HomeView;


public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        Label label = new Label("Welcome to Student Attendance Management System");
        Scene scene = new Scene(label, 400, 200);
        stage.setScene(scene);
        stage.setTitle("SAMS");
        stage.show();
    }

    public static void main(String[] args) {

            Application.launch(HomeView.class, args);


    }
}
