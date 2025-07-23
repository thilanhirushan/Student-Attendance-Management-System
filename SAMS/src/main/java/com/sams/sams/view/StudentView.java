package com.sams.sams.view;

import com.sams.sams.dao.CourseDAO;
import com.sams.sams.dao.StudentDAO;
import com.sams.sams.model.Course;
import com.sams.sams.model.Student;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StudentView extends Application {

    private final StudentDAO studentDAO = new StudentDAO();
    private final CourseDAO courseDAO = new CourseDAO();

    private final TextField nameField = new TextField();
    private final TextField regField = new TextField();
    private final TextField contactField = new TextField();
    private final ComboBox<Course> courseBox = new ComboBox<>();
    private final ListView<Student> studentList = new ListView<>();
    private final TextField searchField = new TextField();

    private final Button addButton = new Button("Add Student");
    private final Button updateButton = new Button("Update Selected");
    private final Button deleteButton = new Button("Delete Selected");

    private Student selectedStudent = null;

    private final ObservableList<Student> masterStudentList = FXCollections.observableArrayList();
    private final FilteredList<Student> filteredStudents = new FilteredList<>(masterStudentList, p -> true);

    @Override
    public void start(Stage stage) {
        nameField.setPromptText("Name");
        regField.setPromptText("Registration Number");
        contactField.setPromptText("Contact");
        courseBox.getItems().setAll(courseDAO.getAllCourses());
        courseBox.setPromptText("Select Course");

        searchField.setPromptText("Search by student name...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredStudents.setPredicate(student -> {
                if (newVal == null || newVal.isEmpty()) return true;
                return student.getName().toLowerCase().contains(newVal.toLowerCase());
            });
        });

        studentList.setItems(filteredStudents);

        addButton.setOnAction(e -> handleAdd());
        updateButton.setOnAction(e -> handleUpdate());
        deleteButton.setOnAction(e -> handleDelete());

        studentList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedStudent = newVal;
                nameField.setText(newVal.getName());
                regField.setText(newVal.getRegNumber());
                contactField.setText(newVal.getContact());

                for (Course course : courseBox.getItems()) {
                    if (course.getId() == newVal.getCourseId()) {
                        courseBox.setValue(course);
                        break;
                    }
                }
            }
        });

        VBox root = new VBox(10, nameField, regField, contactField, courseBox,
                searchField, addButton, updateButton, deleteButton, studentList);

        root.setStyle("-fx-padding: 20");

        Scene scene = new Scene(root, 400, 550);
        stage.setScene(scene);
        stage.setTitle("Student Manager");
        stage.show();

        refreshStudentList();
    }

    private void handleAdd() {
        if (courseBox.getValue() == null) {
            showAlert("Course not selected");
            return;
        }

        Student student = new Student(
                nameField.getText().trim(),
                regField.getText().trim(),
                courseBox.getValue().getId(),
                contactField.getText().trim()
        );

        if (studentDAO.addStudent(student)) {
            refreshStudentList();
            clearFields();
        } else {
            showAlert("Failed to add student.");
        }
    }

    private void handleUpdate() {
        if (selectedStudent == null || courseBox.getValue() == null) {
            showAlert("Select a student and course first.");
            return;
        }

        selectedStudent.setName(nameField.getText().trim());
        selectedStudent.setRegNumber(regField.getText().trim());
        selectedStudent.setContact(contactField.getText().trim());
        selectedStudent.setCourseId(courseBox.getValue().getId());

        if (studentDAO.updateStudent(selectedStudent)) {
            refreshStudentList();
            clearFields();
        } else {
            showAlert("Failed to update student.");
        }
    }

    private void handleDelete() {
        if (selectedStudent == null) {
            showAlert("Select a student first.");
            return;
        }

        if (studentDAO.deleteStudent(selectedStudent.getId())) {
            refreshStudentList();
            clearFields();
        } else {
            showAlert("Failed to delete student.");
        }
    }

    private void refreshStudentList() {
        masterStudentList.setAll(studentDAO.getAllStudents());
    }

    private void clearFields() {
        nameField.clear();
        regField.clear();
        contactField.clear();
        courseBox.setValue(null);
        selectedStudent = null;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
