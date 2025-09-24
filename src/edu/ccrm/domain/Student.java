package edu.ccrm.domain;

import edu.ccrm.util.Grade;
import edu.ccrm.util.Semester;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Student class extending Person with additional student-specific attributes.
 * Demonstrates inheritance and method overriding.
 */
public class Student extends Person {
    private String major;
    private int totalCredits;
    private double gpa;
    private List<Enrollment> enrollments;
    
    // Static nested class for Student Statistics
    public static class StudentStatistics {
        private int totalStudents;
        private double averageGPA;
        private int totalCreditsEarned;
        
        public StudentStatistics(int totalStudents, double averageGPA, int totalCreditsEarned) {
            this.totalStudents = totalStudents;
            this.averageGPA = averageGPA;
            this.totalCreditsEarned = totalCreditsEarned;
        }
        
        public int getTotalStudents() { return totalStudents; }
        public double getAverageGPA() { return averageGPA; }
        public int getTotalCreditsEarned() { return totalCreditsEarned; }
        
        @Override
        public String toString() {
            return String.format("Total Students: %d, Average GPA: %.2f, Total Credits: %d",
                    totalStudents, averageGPA, totalCreditsEarned);
        }
    }
    
    public Student(String id, String firstName, String lastName, String email, 
                   LocalDate birthDate, String major) {
        super(id, firstName, lastName, email, birthDate);
        this.major = major;
        this.totalCredits = 0;
        this.gpa = 0.0;
        this.enrollments = new ArrayList<>();
    }
    
    // Method overriding from abstract parent class
    @Override
    public String getRole() {
        return "Student";
    }
    
    @Override
    public String getDisplayInfo() {
        return String.format("%s - Major: %s, Credits: %d, GPA: %.2f",
                getFullName(), major, totalCredits, gpa);
    }
    
    // Student-specific methods
    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
    }
    
    public void calculateGPA() {
        if (enrollments.isEmpty()) {
            this.gpa = 0.0;
            return;
        }
        
        double totalGradePoints = 0.0;
        int totalCreditsAttempted = 0;
        
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getGrade() != null) {
                totalGradePoints += enrollment.getGrade().getGradePoints() * enrollment.getCourse().getCredits();
                totalCreditsAttempted += enrollment.getCourse().getCredits();
            }
        }
        
        this.gpa = totalCreditsAttempted > 0 ? totalGradePoints / totalCreditsAttempted : 0.0;
    }
    
    public int calculateTotalCredits() {
        totalCredits = enrollments.stream()
                .filter(e -> e.getGrade() != null && e.getGrade().isPassingGrade())
                .mapToInt(e -> e.getCourse().getCredits())
                .sum();
        return totalCredits;
    }
    
    // Getters and setters
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    
    public int getTotalCredits() { return totalCredits; }
    public void setTotalCredits(int totalCredits) { this.totalCredits = totalCredits; }
    
    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }
    
    public List<Enrollment> getEnrollments() { return new ArrayList<>(enrollments); }
    
    @Override
    public String toString() {
        return super.toString() + String.format(", Major: %s, Credits: %d, GPA: %.2f",
                major, totalCredits, gpa);
    }
}