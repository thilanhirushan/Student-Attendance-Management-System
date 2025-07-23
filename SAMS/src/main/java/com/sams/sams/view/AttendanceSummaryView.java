package com.sams.sams.view;

import com.sams.sams.dao.AttendanceDAO;
import com.sams.sams.dao.CourseDAO;
import com.sams.sams.dao.StudentDAO;
import com.sams.sams.model.Course;
import com.sams.sams.model.Student;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class AttendanceSummaryView extends Application {

    private final CourseDAO courseDAO = new CourseDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    private final ComboBox<Course> courseBox = new ComboBox<>();
    private final TextArea reportArea = new TextArea();
    private final Button exportBtn = new Button("Export Report");

    @Override
    public void start(Stage stage) {
        courseBox.setItems(FXCollections.observableArrayList(courseDAO.getAllCourses()));
        courseBox.setPromptText("Select Course");

        Button generateReportBtn = new Button("Generate Report");

        generateReportBtn.setOnAction(e -> generateSummary());
        exportBtn.setOnAction(e -> exportReportToFile());

        VBox root = new VBox(10,
                new Label("📊 Attendance Summary"),
                courseBox,
                generateReportBtn,
                exportBtn,
                reportArea
        );

        root.setStyle("-fx-padding: 20");
        reportArea.setEditable(false);

        Scene scene = new Scene(root, 500, 500);
        stage.setScene(scene);
        stage.setTitle("Attendance Summary");
        stage.show();
    }

    private void generateSummary() {
        Course course = courseBox.getValue();
        if (course == null) {
            showAlert("Please select a course.");
            return;
        }

        List<Student> students = studentDAO.getStudentsByCourseId(course.getId());
        int totalSessions = attendanceDAO.getTotalSessionsByCourse(course.getId());

        StringBuilder sb = new StringBuilder();
        sb.append("Course: ").append(course.getName()).append("\n");
        sb.append("Total Sessions: ").append(totalSessions).append("\n\n");

        for (Student student : students) {
            int presentCount = attendanceDAO.getPresentCountByStudent(student.getId());
            double percentage = (totalSessions == 0) ? 0.0 : (presentCount * 100.0 / totalSessions);

            sb.append(student.getName())
                    .append(" (").append(student.getRegNumber()).append("): ")
                    .append(presentCount).append("/").append(totalSessions)
                    .append(" (").append(String.format("%.2f", percentage)).append("%)\n");
        }

        reportArea.setText(sb.toString());
    }

    private void exportReportToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Attendance Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(reportArea.getText());
                showAlert("✅ Report exported to:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                showAlert("❌ Error saving file: " + e.getMessage());
            }
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
