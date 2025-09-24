package edu.ccrm.domain;

import edu.ccrm.util.Grade;
import edu.ccrm.util.Semester;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Transcript class implementing Builder pattern for flexible transcript generation.
 * Demonstrates advanced Stream API usage and functional programming concepts.
 */
public class Transcript {
    private Student student;
    private LocalDateTime generatedDate;
    private List<Enrollment> enrollments;
    private double cumulativeGPA;
    private int totalCreditsAttempted;
    private int totalCreditsEarned;
    private String notes;
    
    // Private constructor for Builder pattern
    private Transcript(Builder builder) {
        this.student = builder.student;
        this.generatedDate = LocalDateTime.now();
        this.enrollments = new ArrayList<>(builder.enrollments);
        this.notes = builder.notes;
        calculateStatistics();
    }
    
    /**
     * Builder class for Transcript construction.
     * Implements Builder pattern with method chaining.
     */
    public static class Builder {
        private Student student;
        private List<Enrollment> enrollments = new ArrayList<>();
        private String notes = "";
        
        public Builder(Student student) {
            this.student = student;
            this.enrollments.addAll(student.getEnrollments());
        }
        
        public Builder filterBySemester(Semester semester, int year) {
            this.enrollments = enrollments.stream()
                    .filter(e -> e.getCourse().getSemester() == semester 
                            && e.getCourse().getYear() == year)
                    .collect(Collectors.toList());
            return this;
        }
        
        public Builder filterByGrade(Grade minGrade) {
            this.enrollments = enrollments.stream()
                    .filter(e -> e.getGrade() != null 
                            && e.getGrade().getGradePoints() >= minGrade.getGradePoints())
                    .collect(Collectors.toList());
            return this;
        }
        
        public Builder filterByDepartment(String department) {
            this.enrollments = enrollments.stream()
                    .filter(e -> department.equals(e.getCourse().getDepartment()))
                    .collect(Collectors.toList());
            return this;
        }
        
        public Builder addNotes(String notes) {
            this.notes = notes;
            return this;
        }
        
        public Transcript build() {
            return new Transcript(this);
        }
    }
    
    // Statistics calculation using streams and lambda expressions
    private void calculateStatistics() {
        // Calculate total credits attempted
        totalCreditsAttempted = enrollments.stream()
                .filter(e -> e.getGrade() != null)
                .mapToInt(e -> e.getCourse().getCredits())
                .sum();
        
        // Calculate total credits earned (passing grades only)
        totalCreditsEarned = enrollments.stream()
                .filter(e -> e.getGrade() != null && e.getGrade().isPassingGrade())
                .mapToInt(e -> e.getCourse().getCredits())
                .sum();
        
        // Calculate GPA using weighted average
        if (totalCreditsAttempted > 0) {
            double totalGradePoints = enrollments.stream()
                    .filter(e -> e.getGrade() != null)
                    .mapToDouble(e -> e.getGrade().getGradePoints() * e.getCourse().getCredits())
                    .sum();
            cumulativeGPA = totalGradePoints / totalCreditsAttempted;
        } else {
            cumulativeGPA = 0.0;
        }
    }
    
    // Generate formatted transcript string using functional programming
    public String generateTranscriptReport() {
        StringBuilder report = new StringBuilder();
        
        // Header
        report.append("OFFICIAL TRANSCRIPT\n");
        report.append("==================\n");
        report.append(String.format("Student: %s (ID: %s)\n", 
                student.getFullName(), student.getId()));
        report.append(String.format("Major: %s\n", student.getMajor()));
        report.append(String.format("Generated: %s\n\n", generatedDate));
        
        // Group enrollments by semester and year
        Map<String, List<Enrollment>> semesterGroups = enrollments.stream()
                .collect(Collectors.groupingBy(e -> 
                        e.getCourse().getSemester() + " " + e.getCourse().getYear()));
        
        // Display each semester
        semesterGroups.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    report.append(String.format("=== %s ===\n", entry.getKey()));
                    
                    entry.getValue().stream()
                            .sorted((e1, e2) -> e1.getCourse().getCourseId()
                                    .compareTo(e2.getCourse().getCourseId()))
                            .forEach(enrollment -> {
                                Course course = enrollment.getCourse();
                                Grade grade = enrollment.getGrade();
                                report.append(String.format("%-10s %-30s %2d %s\n",
                                        course.getCourseId(),
                                        course.getTitle(),
                                        course.getCredits(),
                                        grade != null ? grade.toString() : "IP"));
                            });
                    report.append("\n");
                });
        
        // Summary statistics
        report.append("SUMMARY\n");
        report.append("=======\n");
        report.append(String.format("Credits Attempted: %d\n", totalCreditsAttempted));
        report.append(String.format("Credits Earned: %d\n", totalCreditsEarned));
        report.append(String.format("Cumulative GPA: %.2f\n", cumulativeGPA));
        
        if (!notes.isEmpty()) {
            report.append(String.format("Notes: %s\n", notes));
        }
        
        return report.toString();
    }
    
    // Getters
    public Student getStudent() { return student; }
    public LocalDateTime getGeneratedDate() { return generatedDate; }
    public List<Enrollment> getEnrollments() { return new ArrayList<>(enrollments); }
    public double getCumulativeGPA() { return cumulativeGPA; }
    public int getTotalCreditsAttempted() { return totalCreditsAttempted; }
    public int getTotalCreditsEarned() { return totalCreditsEarned; }
    public String getNotes() { return notes; }
    
    @Override
    public String toString() {
        return String.format("Transcript for %s - GPA: %.2f, Credits: %d/%d",
                student.getFullName(), cumulativeGPA, totalCreditsEarned, totalCreditsAttempted);
    }
}