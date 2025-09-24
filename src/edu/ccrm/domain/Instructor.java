package edu.ccrm.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Instructor class extending Person with instructor-specific attributes.
 * Demonstrates inheritance and method overriding.
 */
public class Instructor extends Person {
    private String department;
    private String title;
    private double salary;
    private List<Course> coursesTeaching;
    
    // Inner class for Office Hours
    public class OfficeHours {
        private String day;
        private String startTime;
        private String endTime;
        private String location;
        
        public OfficeHours(String day, String startTime, String endTime, String location) {
            this.day = day;
            this.startTime = startTime;
            this.endTime = endTime;
            this.location = location;
        }
        
        public String getInstructorName() {
            return Instructor.this.getFullName(); // Access to outer class
        }
        
        @Override
        public String toString() {
            return String.format("%s: %s-%s at %s (Instructor: %s)",
                    day, startTime, endTime, location, getInstructorName());
        }
        
        // Getters
        public String getDay() { return day; }
        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
        public String getLocation() { return location; }
    }
    
    public Instructor(String id, String firstName, String lastName, String email,
                     LocalDate birthDate, String department, String title, double salary) {
        super(id, firstName, lastName, email, birthDate);
        this.department = department;
        this.title = title;
        this.salary = salary;
        this.coursesTeaching = new ArrayList<>();
    }
    
    // Method overriding from abstract parent class
    @Override
    public String getRole() {
        return "Instructor";
    }
    
    @Override
    public String getDisplayInfo() {
        return String.format("%s - %s, %s Department, Teaching %d courses",
                getFullName(), title, department, coursesTeaching.size());
    }
    
    // Instructor-specific methods
    public void assignCourse(Course course) {
        if (!coursesTeaching.contains(course)) {
            coursesTeaching.add(course);
            course.setInstructor(this);
        }
    }
    
    public void removeCourse(Course course) {
        coursesTeaching.remove(course);
    }
    
    public OfficeHours createOfficeHours(String day, String startTime, String endTime, String location) {
        return new OfficeHours(day, startTime, endTime, location);
    }
    
    // Method overloading
    public double calculateTotalSalary() {
        return salary;
    }
    
    public double calculateTotalSalary(double bonus) {
        return salary + bonus;
    }
    
    public double calculateTotalSalary(double bonus, double benefits) {
        return salary + bonus + benefits;
    }
    
    // Getters and setters
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    
    public List<Course> getCoursesTeaching() { return new ArrayList<>(coursesTeaching); }
    
    @Override
    public String toString() {
        return super.toString() + String.format(", Department: %s, Title: %s, Courses: %d",
                department, title, coursesTeaching.size());
    }
}