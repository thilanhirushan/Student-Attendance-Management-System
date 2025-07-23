package com.sams.sams.model;

import java.time.LocalDate;

public class Attendance {
    private int id;
    private int studentId;
    private LocalDate date;
    private boolean present;

    public Attendance(int studentId, LocalDate date, boolean present) {
        this.studentId = studentId;
        this.date = date;
        this.present = present;
    }

    public Attendance(int id, int studentId, LocalDate date, boolean present) {
        this.id = id;
        this.studentId = studentId;
        this.date = date;
        this.present = present;
    }

    public int getId() {
        return id;
    }

    public int getStudentId() {
        return studentId;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isPresent() {
        return present;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }
}
