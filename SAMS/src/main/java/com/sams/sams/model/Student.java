package com.sams.sams.model;

public class Student {
    private int id;
    private String name;
    private String regNumber;
    private int courseId;
    private String contact;

    public Student() {}

    public Student(int id, String name, String regNumber, int courseId, String contact) {
        this.id = id;
        this.name = name;
        this.regNumber = regNumber;
        this.courseId = courseId;
        this.contact = contact;
    }

    public Student(String name, String regNumber, int courseId, String contact) {
        this.name = name;
        this.regNumber = regNumber;
        this.courseId = courseId;
        this.contact = contact;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRegNumber() { return regNumber; }
    public void setRegNumber(String regNumber) { this.regNumber = regNumber; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    @Override
    public String toString() {
        return name + " (" + regNumber + ")";
    }
}
