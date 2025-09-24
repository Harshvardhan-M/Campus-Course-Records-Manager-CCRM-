package edu.ccrm.domain;

import edu.ccrm.util.Grade;
import java.time.LocalDateTime;

/**
 * Enrollment class representing the relationship between Student and Course.
 * Demonstrates composition and association relationships.
 */
public class Enrollment {
    private String enrollmentId;
    private Student student;
    private Course course;
    private LocalDateTime enrollmentDate;
    private Grade grade;
    private String status; // ENROLLED, COMPLETED, WITHDRAWN, INCOMPLETE
    
    public Enrollment(String enrollmentId, Student student, Course course) {
        this.enrollmentId = enrollmentId;
        this.student = student;
        this.course = course;
        this.enrollmentDate = LocalDateTime.now();
        this.status = "ENROLLED";
        this.grade = null;
    }
    
    // Business logic methods
    public boolean isCompleted() {
        return "COMPLETED".equals(status) && grade != null;
    }
    
    public boolean isActive() {
        return "ENROLLED".equals(status);
    }
    
    public void complete(Grade grade) {
        this.grade = grade;
        this.status = "COMPLETED";
    }
    
    public void withdraw() {
        this.status = "WITHDRAWN";
        this.grade = Grade.F; // Default grade for withdrawal
    }
    
    // Getters and setters
    public String getEnrollmentId() { return enrollmentId; }
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public LocalDateTime getEnrollmentDate() { return enrollmentDate; }
    public Grade getGrade() { return grade; }
    public void setGrade(Grade grade) { this.grade = grade; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    @Override
    public String toString() {
        return String.format("Enrollment[%s] - Student: %s, Course: %s, Grade: %s, Status: %s",
                enrollmentId, student.getFullName(), course.getCourseId(),
                grade != null ? grade : "N/A", status);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Enrollment enrollment = (Enrollment) obj;
        return enrollmentId.equals(enrollment.enrollmentId);
    }
    
    @Override
    public int hashCode() {
        return enrollmentId.hashCode();
    }
}