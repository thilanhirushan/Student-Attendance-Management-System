package com.sams.sams.view;

import com.sams.sams.dao.CourseDAO;
import com.sams.sams.model.Course;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CourseView extends Application {

    private final CourseDAO courseDAO = new CourseDAO();
    private final ListView<Course> courseList = new ListView<>();
    private final TextField nameField = new TextField();
    private final Button addButton = new Button("Add Course");
    private final Button updateButton = new Button("Update Selected");
    private final Button deleteButton = new Button("Delete Selected");
    private Course selectedCourse = null;


    @Override
    public void start(Stage stage) {
        nameField.setPromptText("Enter course name");

        addButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                Course course = new Course(name);
                boolean success = courseDAO.addCourse(course);
                if (success) {
                    refreshCourses();
                    nameField.clear();
                } else {
                    showAlert("Error", "Could not add course");
                }
            }
        });

        updateButton.setOnAction(e -> {
            if (selectedCourse == null) {
                showAlert("Error", "No course selected to update.");
                return;
            }

            String newName = nameField.getText().trim();
            if (!newName.isEmpty()) {
                selectedCourse.setName(newName);
                if (courseDAO.updateCourse(selectedCourse)) {
                    refreshCourses();
                    nameField.clear();
                    selectedCourse = null;
                } else {
                    showAlert("Error", "Could not update course.");
                }
            }
        });

        deleteButton.setOnAction(e -> {
            Course course = courseList.getSelectionModel().getSelectedItem();
            if (course == null) {
                showAlert("Error", "No course selected to delete.");
                return;
            }

            if (courseDAO.deleteCourse(course.getId())) {
                refreshCourses();
                nameField.clear();
                selectedCourse = null;
            } else {
                showAlert("Error", "Could not delete course.");
            }
        });



        VBox root = new VBox(10, nameField, addButton, updateButton, deleteButton, courseList);
        root.setStyle("-fx-padding: 20");

        Scene scene = new Scene(root, 400, 400);
        stage.setTitle("Course Manager");
        stage.setScene(scene);
        stage.show();

        refreshCourses(); // load courses on start
        courseList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedCourse = newVal; // save the selected course object
                nameField.setText(newVal.getName()); // fill the text field with course name
            }
        });

    }

    private void refreshCourses() {
        courseList.getItems().setAll(courseDAO.getAllCourses());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
