package com.sams.sams.view;

import com.sams.sams.dao.AttendanceDAO;
import com.sams.sams.dao.CourseDAO;
import com.sams.sams.dao.StudentDAO;
import com.sams.sams.model.Attendance;
import com.sams.sams.model.Course;
import com.sams.sams.model.Student;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class AttendanceView extends Application {

    private final CourseDAO courseDAO = new CourseDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    private final ComboBox<Course> courseBox = new ComboBox<>();
    private final DatePicker datePicker = new DatePicker(LocalDate.now());
    private final List<CheckBox> studentCheckboxes = new ArrayList<>();
    private final VBox studentListBox = new VBox(5);
    private final Button viewAttendanceButton = new Button("View Attendance");
    private final Button exportButton = new Button("Export to CSV");
    private final Button reportButton = new Button("📊 Course Report");
    private final Button exportAllButton = new Button("📁 Export All History");




    @Override
    public void start(Stage stage) {
        courseBox.setItems(FXCollections.observableArrayList(courseDAO.getAllCourses()));
        courseBox.setPromptText("Select Course");
        datePicker.setPromptText("Select Date");

        Button loadStudentsButton = new Button("Load Students");
        Button submitAttendanceButton = new Button("Submit Attendance");

        // 🌟 Label before checkboxes
        Label studentListLabel = new Label("⬇️ Students List");

        ScrollPane scrollPane = new ScrollPane(studentListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5;"); // 🌟 styled border

        // 🔹 Load students
        loadStudentsButton.setOnAction(e -> {
            studentListBox.getChildren().clear();
            studentCheckboxes.clear();

            Course selectedCourse = courseBox.getValue();
            if (selectedCourse == null) {
                showAlert("Please select a course.");
                return;
            }

            List<Student> students = studentDAO.getStudentsByCourseId(selectedCourse.getId());
            for (Student student : students) {
                CheckBox cb = new CheckBox(student.getName() + " (" + student.getRegNumber() + ")");
                cb.setUserData(student);
                studentCheckboxes.add(cb);
                studentListBox.getChildren().add(cb);
            }
        });

        // 🔹 Submit attendance
        submitAttendanceButton.setOnAction(e -> {
            LocalDate date = datePicker.getValue();
            Course selectedCourse = courseBox.getValue();
            if (date == null || selectedCourse == null) {
                showAlert("Please select both course and date.");
                return;
            }

            // 🌟 Prevent duplicates
            List<Attendance> existing = attendanceDAO.getAttendanceByDate(date);
            if (!existing.isEmpty()) {
                showAlert("⚠️ Attendance already exists for this date. Use 'View Attendance' instead.");
                return;
            }

            for (CheckBox cb : studentCheckboxes) {
                Student student = (Student) cb.getUserData();
                boolean present = cb.isSelected();
                Attendance a = new Attendance(student.getId(), date, present);
                attendanceDAO.addAttendance(a);
            }

            showAlert("✅ Attendance saved successfully.");

            // 🌟 Clear after submit
            studentListBox.getChildren().clear();
            studentCheckboxes.clear();
        });

        // 🔹 View attendance
        viewAttendanceButton.setOnAction(e -> {
            LocalDate date = datePicker.getValue();
            Course selectedCourse = courseBox.getValue();

            if (date == null || selectedCourse == null) {
                showAlert("Please select both course and date.");
                return;
            }

            studentListBox.getChildren().clear();
            studentCheckboxes.clear();

            List<Student> students = studentDAO.getStudentsByCourseId(selectedCourse.getId());
            List<Attendance> attendanceRecords = attendanceDAO.getAttendanceByDate(date);

            for (Student student : students) {
                CheckBox cb = new CheckBox(student.getName() + " (" + student.getRegNumber() + ")");
                cb.setUserData(student);

                boolean wasPresent = attendanceRecords.stream()
                        .anyMatch(a -> a.getStudentId() == student.getId() && a.isPresent());

                cb.setSelected(wasPresent);
                cb.setDisable(true);

                studentCheckboxes.add(cb);
                studentListBox.getChildren().add(cb);
            }
        });
        // 🔹 Export attendance to CSV
        exportButton.setOnAction(e -> {
            LocalDate date = datePicker.getValue();
            Course selectedCourse = courseBox.getValue();

            if (date == null || selectedCourse == null) {
                showAlert("Please select both course and date.");
                return;
            }

            List<Student> students = studentDAO.getStudentsByCourseId(selectedCourse.getId());
            List<Attendance> attendanceRecords = attendanceDAO.getAttendanceByDate(date);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Attendance Report");
            fileChooser.setInitialFileName("attendance_" + selectedCourse.getName() + "_" + date + ".csv");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("Student Name,Reg Number,Present\n");

                    for (Student student : students) {
                        boolean wasPresent = attendanceRecords.stream()
                                .anyMatch(a -> a.getStudentId() == student.getId() && a.isPresent());

                        writer.write(String.format("%s,%s,%s\n",
                                student.getName(),
                                student.getRegNumber(),
                                wasPresent ? "Yes" : "No"
                        ));
                    }

                    showAlert("✅ Attendance exported successfully.");

                } catch (IOException ex) {
                    ex.printStackTrace();
                    showAlert("❌ Failed to export attendance.");
                }
            }
        });
        // 🔹 Generate attendance report for selected course
        reportButton.setOnAction(e -> {
            Course selectedCourse = courseBox.getValue();

            if (selectedCourse == null) {
                showAlert("Please select a course.");
                return;
            }

            List<Student> students = studentDAO.getStudentsByCourseId(selectedCourse.getId());
            List<Attendance> allAttendance = attendanceDAO.getAllAttendance(); // You must add this method

            StringBuilder report = new StringBuilder("Student Name,Reg Number,Total Days,Present,Absent,Percentage\n");

            for (Student student : students) {
                List<Attendance> studentRecords = allAttendance.stream()
                        .filter(a -> a.getStudentId() == student.getId())
                        .toList();

                int total = studentRecords.size();
                long present = studentRecords.stream().filter(Attendance::isPresent).count();
                long absent = total - present;
                double percentage = (total == 0) ? 0.0 : ((double) present / total) * 100;

                report.append(String.format("%s,%s,%d,%d,%d,%.2f%%\n",
                        student.getName(),
                        student.getRegNumber(),
                        total,
                        present,
                        absent,
                        percentage
                ));
            }

            // Save report to file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Report");
            fileChooser.setInitialFileName("attendance_report_" + selectedCourse.getName() + ".csv");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(report.toString());
                    showAlert("✅ Report generated successfully.");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    showAlert("❌ Failed to save report.");
                }
            }
        });
        exportAllButton.setOnAction(e -> {
            List<Attendance> allRecords = attendanceDAO.getAllAttendance(); // already added earlier
            List<Student> allStudents = studentDAO.getAllStudents();
            List<Course> allCourses = courseDAO.getAllCourses();

            StringBuilder csv = new StringBuilder("Date,Student Name,Reg Number,Course,Present\n");

            for (Attendance a : allRecords) {
                Student student = allStudents.stream()
                        .filter(s -> s.getId() == a.getStudentId())
                        .findFirst()
                        .orElse(null);

                if (student == null) continue;

                Course course = allCourses.stream()
                        .filter(c -> c.getId() == student.getCourseId())
                        .findFirst()
                        .orElse(null);

                String courseName = (course != null) ? course.getName() : "Unknown";
                String presentStatus = a.isPresent() ? "Present" : "Absent";

                csv.append(String.format("%s,%s,%s,%s,%s\n",
                        a.getDate(),
                        student.getName(),
                        student.getRegNumber(),
                        courseName,
                        presentStatus
                ));
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export All Attendance History");
            fileChooser.setInitialFileName("all_attendance_history.csv");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(csv.toString());
                    showAlert("✅ All attendance history exported.");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    showAlert("❌ Failed to export.");
                }
            }
        });




        // 🌟 UI structure
        VBox root = new VBox(10,
                new Label("📘 Attendance Manager"),
                courseBox,
                datePicker,
                loadStudentsButton,
                submitAttendanceButton,
                viewAttendanceButton,
                exportButton,
                reportButton,
                exportAllButton,
                studentListLabel,
                scrollPane
        );

        root.setStyle("-fx-padding: 20");

        Scene scene = new Scene(root, 430, 550);
        stage.setScene(scene);
        stage.setTitle("Attendance Manager");
        stage.show();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
