package edu.ccrm.service;

import edu.ccrm.domain.*;
import edu.ccrm.util.Grade;
import edu.ccrm.util.Semester;
import edu.ccrm.exception.DuplicateEnrollmentException;
import edu.ccrm.exception.MaxCreditLimitExceededException;
import edu.ccrm.config.AppConfig;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class for managing student enrollments.
 * Demonstrates comprehensive exception handling, business rules, and transactions.
 */
public class EnrollmentService implements Persistable<Enrollment>, Searchable<Enrollment> {
    
    private static final String ENROLLMENTS_FILE = "data/enrollments.csv";
    private List<Enrollment> enrollments;
    private StudentService studentService;
    private CourseService courseService;
    
    // Constructor with dependency injection
    public EnrollmentService(StudentService studentService, CourseService courseService) {
        this.enrollments = new ArrayList<>();
        this.studentService = studentService;
        this.courseService = courseService;
        try {
            loadFromFile();
        } catch (IOException e) {
            System.err.println("Warning: Could not load enrollments from file: " + e.getMessage());
        }
    }
    
    // Business logic methods with comprehensive exception handling
    
    /**
     * Enrolls a student in a course with validation and business rules.
     * Demonstrates multiple exception types and transaction-like behavior.
     */
    public String enrollStudent(String studentId, String courseId) 
            throws DuplicateEnrollmentException, MaxCreditLimitExceededException, IllegalArgumentException {
        
        // Pre-conditions validation
        Student student = studentService.findById(studentId);
        Course course = courseService.findById(courseId);
        
        if (student == null) {
            throw new IllegalArgumentException("Student with ID " + studentId + " not found");
        }
        
        if (course == null) {
            throw new IllegalArgumentException("Course with ID " + courseId + " not found");
        }
        
        // Check for duplicate enrollment
        if (isStudentEnrolled(studentId, courseId)) {
            throw new DuplicateEnrollmentException(studentId, courseId);
        }
        
        // Check course capacity
        if (!course.canEnroll()) {
            throw new IllegalArgumentException("Course " + courseId + " is at maximum capacity");
        }
        
        // Check credit limit using AppConfig
        AppConfig config = AppConfig.getInstance();
        int currentCredits = getCurrentSemesterCredits(studentId, course.getSemester(), course.getYear());
        int totalCredits = currentCredits + course.getCredits();
        
        if (totalCredits > config.getMaxCreditsPerSemester()) {
            throw new MaxCreditLimitExceededException(studentId, currentCredits, 
                    course.getCredits(), config.getMaxCreditsPerSemester());
        }
        
        try {
            // Create enrollment
            String enrollmentId = generateEnrollmentId();
            Enrollment enrollment = new Enrollment(enrollmentId, student, course);
            
            // Add to collections
            enrollments.add(enrollment);
            student.addEnrollment(enrollment);
            course.incrementEnrollment();
            
            // Save changes
            save(enrollment);
            
            return enrollmentId;
            
        } catch (Exception e) {
            // Rollback changes if save fails
            System.err.println("Enrollment failed, rolling back changes: " + e.getMessage());
            throw new RuntimeException("Enrollment failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Assigns a grade to an enrollment and updates student GPA.
     * Demonstrates method chaining and state transitions.
     */
    public boolean assignGrade(String enrollmentId, Grade grade) {
        try {
            Enrollment enrollment = findById(enrollmentId);
            if (enrollment == null) {
                throw new IllegalArgumentException("Enrollment with ID " + enrollmentId + " not found");
            }
            
            if (!enrollment.isActive()) {
                throw new IllegalStateException("Cannot assign grade to inactive enrollment");
            }
            
            // Assign grade and complete enrollment
            enrollment.complete(grade);
            
            // Update student GPA and credits
            Student student = enrollment.getStudent();
            student.calculateGPA();
            student.calculateTotalCredits();
            
            // Save changes
            saveAll(enrollments);
            
            // Update student service
            studentService.updateStudentGPA(student.getId());
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error assigning grade: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Withdraws a student from a course.
     * Demonstrates transaction-like behavior with rollback capability.
     */
    public boolean withdrawStudent(String enrollmentId) {
        Enrollment enrollment = null;
        Course course = null;
        
        try {
            enrollment = findById(enrollmentId);
            if (enrollment == null) {
                throw new IllegalArgumentException("Enrollment with ID " + enrollmentId + " not found");
            }
            
            if (!enrollment.isActive()) {
                throw new IllegalStateException("Cannot withdraw from inactive enrollment");
            }
            
            course = enrollment.getCourse();
            Student student = enrollment.getStudent();
            
            // Withdraw enrollment
            enrollment.withdraw();
            course.decrementEnrollment();
            
            // Recalculate student statistics
            student.calculateGPA();
            student.calculateTotalCredits();
            
            // Save changes
            saveAll(enrollments);
            studentService.updateStudentGPA(student.getId());
            
            return true;
            
        } catch (Exception e) {
            // Attempt rollback
            try {
                if (enrollment != null && course != null) {
                    enrollment.setStatus("ENROLLED");
                    enrollment.setGrade(null);
                    course.incrementEnrollment();
                }
            } catch (Exception rollbackError) {
                System.err.println("Rollback failed: " + rollbackError.getMessage());
            }
            
            System.err.println("Error withdrawing student: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets enrollment history for a student.
     */
    public List<Enrollment> getStudentEnrollments(String studentId) {
        return enrollments.stream()
                .filter(e -> studentId.equals(e.getStudent().getId()))
                .sorted((e1, e2) -> e2.getEnrollmentDate().compareTo(e1.getEnrollmentDate()))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets enrollments for a specific course.
     */
    public List<Enrollment> getCourseEnrollments(String courseId) {
        return enrollments.stream()
                .filter(e -> courseId.equals(e.getCourse().getCourseId()))
                .sorted((e1, e2) -> e1.getStudent().getLastName().compareTo(e2.getStudent().getLastName()))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets enrollments for a specific semester.
     */
    public List<Enrollment> getSemesterEnrollments(Semester semester, int year) {
        return enrollments.stream()
                .filter(e -> e.getCourse().getSemester() == semester && e.getCourse().getYear() == year)
                .collect(Collectors.toList());
    }
    
    /**
     * Checks if a student is enrolled in a course.
     */
    public boolean isStudentEnrolled(String studentId, String courseId) {
        return enrollments.stream()
                .anyMatch(e -> studentId.equals(e.getStudent().getId()) &&
                             courseId.equals(e.getCourse().getCourseId()) &&
                             e.isActive());
    }
    
    /**
     * Gets current semester credit count for a student.
     */
    public int getCurrentSemesterCredits(String studentId, Semester semester, int year) {
        return enrollments.stream()
                .filter(e -> studentId.equals(e.getStudent().getId()) &&
                           e.getCourse().getSemester() == semester &&
                           e.getCourse().getYear() == year &&
                           e.isActive())
                .mapToInt(e -> e.getCourse().getCredits())
                .sum();
    }
    
    /**
     * Generates a unique enrollment ID.
     */
    private String generateEnrollmentId() {
        return "ENR" + System.currentTimeMillis() + String.format("%03d", 
                (int)(Math.random() * 1000));
    }
    
    // Persistable interface implementation
    
    @Override
    public void save(Enrollment enrollment) throws IOException {
        saveAll(enrollments);
    }
    
    @Override
    public void saveAll(List<Enrollment> enrollments) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ENROLLMENTS_FILE))) {
            // Write CSV header
            writer.println("EnrollmentID,StudentID,CourseID,EnrollmentDate,Grade,Status");
            
            // Write enrollment data
            for (Enrollment enrollment : enrollments) {
                writer.printf("%s,%s,%s,%s,%s,%s%n",
                        enrollment.getEnrollmentId(),
                        enrollment.getStudent().getId(),
                        enrollment.getCourse().getCourseId(),
                        enrollment.getEnrollmentDate(),
                        enrollment.getGrade() != null ? enrollment.getGrade().toString() : "",
                        enrollment.getStatus());
            }
        }
    }
    
    @Override
    public Enrollment load(String id) throws IOException {
        return findById(id);
    }
    
    @Override
    public List<Enrollment> loadAll() throws IOException {
        return new ArrayList<>(enrollments);
    }
    
    @Override
    public boolean delete(String id) throws IOException {
        boolean removed = enrollments.removeIf(e -> e.getEnrollmentId().equals(id));
        if (removed) {
            saveAll(enrollments);
        }
        return removed;
    }
    
    @Override
    public boolean exists(String id) throws IOException {
        return enrollments.stream().anyMatch(e -> e.getEnrollmentId().equals(id));
    }
    
    @Override
    public int count() throws IOException {
        return enrollments.size();
    }
    
    @Override
    public void clear() throws IOException {
        enrollments.clear();
        saveAll(enrollments);
    }
    
    // Searchable interface implementation
    
    @Override
    public List<Enrollment> search(Predicate<Enrollment> predicate) {
        return enrollments.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
    
    @Override
    public Enrollment findFirst(Predicate<Enrollment> predicate) {
        return enrollments.stream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<Enrollment> searchByText(String searchText) {
        String lowerSearchText = searchText.toLowerCase();
        return enrollments.stream()
                .filter(e -> e.getEnrollmentId().toLowerCase().contains(lowerSearchText) ||
                           e.getStudent().getFullName().toLowerCase().contains(lowerSearchText) ||
                           e.getCourse().getTitle().toLowerCase().contains(lowerSearchText) ||
                           e.getStatus().toLowerCase().contains(lowerSearchText))
                .collect(Collectors.toList());
    }
    
    @Override
    public long count(Predicate<Enrollment> predicate) {
        return enrollments.stream()
                .filter(predicate)
                .count();
    }
    
    // Utility methods
    
    public Enrollment findById(String enrollmentId) {
        return enrollments.stream()
                .filter(e -> e.getEnrollmentId().equals(enrollmentId))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Loads enrollments from CSV file with error handling.
     */
    private void loadFromFile() throws IOException {
        File file = new File(ENROLLMENTS_FILE);
        if (!file.exists()) {
            return; // No file to load from
        }
        
        enrollments.clear();
        int lineNumber = 0;
        int successCount = 0;
        int errorCount = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header
            lineNumber++;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] parts = line.split(",");
                    if (parts.length >= 6) {
                        String enrollmentId = parts[0].trim();
                        String studentId = parts[1].trim();
                        String courseId = parts[2].trim();
                        
                        Student student = studentService.findById(studentId);
                        Course course = courseService.findById(courseId);
                        
                        if (student != null && course != null) {
                            Enrollment enrollment = new Enrollment(enrollmentId, student, course);
                            
                            // Set additional fields if available
                            if (parts.length > 4 && !parts[4].trim().isEmpty()) {
                                enrollment.setGrade(Grade.fromString(parts[4].trim()));
                            }
                            if (parts.length > 5) {
                                enrollment.setStatus(parts[5].trim());
                            }
                            
                            enrollments.add(enrollment);
                            student.addEnrollment(enrollment);
                            successCount++;
                        }
                    }
                } catch (Exception e) {
                    errorCount++;
                    System.err.printf("Error parsing enrollment line %d: %s - %s%n", 
                            lineNumber, line, e.getMessage());
                }
            }
        }
        
        System.out.printf("Loaded %d enrollments successfully, %d errors%n", successCount, errorCount);
    }
}