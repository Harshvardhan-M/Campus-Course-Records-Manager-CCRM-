package edu.ccrm.domain;

import edu.ccrm.util.Semester;

/**
 * Course class implementing Builder pattern for flexible object creation.
 * Demonstrates Builder pattern and method chaining.
 */
public class Course {
    private String courseId;
    private String title;
    private String description;
    private int credits;
    private String department;
    private String prerequisites;
    private Instructor instructor;
    private Semester semester;
    private int year;
    private int maxEnrollment;
    private int currentEnrollment;
    
    // Private constructor for Builder pattern
    private Course(Builder builder) {
        this.courseId = builder.courseId;
        this.title = builder.title;
        this.description = builder.description;
        this.credits = builder.credits;
        this.department = builder.department;
        this.prerequisites = builder.prerequisites;
        this.instructor = builder.instructor;
        this.semester = builder.semester;
        this.year = builder.year;
        this.maxEnrollment = builder.maxEnrollment;
        this.currentEnrollment = 0;
    }
    
    /**
     * Builder class for Course construction.
     * Implements Builder pattern with method chaining.
     */
    public static class Builder {
        // Required parameters
        private String courseId;
        private String title;
        private int credits;
        
        // Optional parameters with default values
        private String description = "";
        private String department = "";
        private String prerequisites = "";
        private Instructor instructor;
        private Semester semester = Semester.FALL;
        private int year = 2024;
        private int maxEnrollment = 30;
        
        public Builder(String courseId, String title, int credits) {
            this.courseId = courseId;
            this.title = title;
            this.credits = credits;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder department(String department) {
            this.department = department;
            return this;
        }
        
        public Builder prerequisites(String prerequisites) {
            this.prerequisites = prerequisites;
            return this;
        }
        
        public Builder instructor(Instructor instructor) {
            this.instructor = instructor;
            return this;
        }
        
        public Builder semester(Semester semester) {
            this.semester = semester;
            return this;
        }
        
        public Builder year(int year) {
            this.year = year;
            return this;
        }
        
        public Builder maxEnrollment(int maxEnrollment) {
            this.maxEnrollment = maxEnrollment;
            return this;
        }
        
        public Course build() {
            return new Course(this);
        }
    }
    
    // Business logic methods
    public boolean canEnroll() {
        return currentEnrollment < maxEnrollment;
    }
    
    public void incrementEnrollment() {
        if (canEnroll()) {
            currentEnrollment++;
        }
    }
    
    public void decrementEnrollment() {
        if (currentEnrollment > 0) {
            currentEnrollment--;
        }
    }
    
    // Getters and setters
    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getCredits() { return credits; }
    public String getDepartment() { return department; }
    public String getPrerequisites() { return prerequisites; }
    public Instructor getInstructor() { return instructor; }
    public void setInstructor(Instructor instructor) { this.instructor = instructor; }
    public Semester getSemester() { return semester; }
    public int getYear() { return year; }
    public int getMaxEnrollment() { return maxEnrollment; }
    public int getCurrentEnrollment() { return currentEnrollment; }
    
    @Override
    public String toString() {
        String instructorName = instructor != null ? instructor.getFullName() : "TBA";
        return String.format("%s: %s (%d credits) - %s %d, Instructor: %s, Enrollment: %d/%d",
                courseId, title, credits, semester, year, instructorName,
                currentEnrollment, maxEnrollment);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return courseId.equals(course.courseId);
    }
    
    @Override
    public int hashCode() {
        return courseId.hashCode();
    }
}