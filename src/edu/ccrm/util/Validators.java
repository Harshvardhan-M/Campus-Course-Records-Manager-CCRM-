package edu.ccrm.util;

import java.util.regex.Pattern;
import java.time.LocalDate;

/**
 * Utility class for validation methods.
 * Demonstrates static methods, regex patterns, and validation logic.
 */
public class Validators {
    
    // Email pattern for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Student ID pattern (e.g., S2024001)
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile(
        "^S\\d{7}$"
    );
    
    // Course ID pattern (e.g., CS101, MATH200)
    private static final Pattern COURSE_ID_PATTERN = Pattern.compile(
        "^[A-Z]{2,4}\\d{3}$"
    );
    
    // Private constructor to prevent instantiation
    private Validators() {
        throw new AssertionError("Utility class should not be instantiated");
    }
    
    /**
     * Validates email address format.
     * @param email the email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validates student ID format.
     * @param studentId the student ID to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidStudentId(String studentId) {
        return studentId != null && STUDENT_ID_PATTERN.matcher(studentId).matches();
    }
    
    /**
     * Validates course ID format.
     * @param courseId the course ID to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidCourseId(String courseId) {
        return courseId != null && COURSE_ID_PATTERN.matcher(courseId).matches();
    }
    
    /**
     * Validates credit hours (must be between 1 and 6).
     * @param credits the credits to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidCredits(int credits) {
        return credits >= 1 && credits <= 6;
    }
    
    /**
     * Validates GPA (must be between 0.0 and 4.0).
     * @param gpa the GPA to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidGPA(double gpa) {
        return gpa >= 0.0 && gpa <= 4.0;
    }
    
    /**
     * Validates birth date (must be in the past and reasonable for student/instructor).
     * @param birthDate the birth date to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidBirthDate(LocalDate birthDate) {
        if (birthDate == null) return false;
        
        LocalDate today = LocalDate.now();
        LocalDate minDate = today.minusYears(100); // Maximum age 100
        LocalDate maxDate = today.minusYears(16);  // Minimum age 16
        
        return birthDate.isAfter(minDate) && birthDate.isBefore(maxDate);
    }
    
    /**
     * Validates that a string is not null or empty.
     * @param value the string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Validates semester and year combination.
     * @param semester the semester
     * @param year the year
     * @return true if valid, false otherwise
     */
    public static boolean isValidSemesterYear(Semester semester, int year) {
        int currentYear = LocalDate.now().getYear();
        return semester != null && year >= (currentYear - 10) && year <= (currentYear + 5);
    }
    
    /**
     * Validates enrollment capacity.
     * @param current current enrollment
     * @param maximum maximum enrollment
     * @return true if valid, false otherwise
     */
    public static boolean isValidEnrollment(int current, int maximum) {
        return current >= 0 && maximum > 0 && current <= maximum;
    }
    
    /**
     * Validates instructor salary.
     * @param salary the salary to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidSalary(double salary) {
        return salary >= 30000.0 && salary <= 200000.0; // Reasonable salary range
    }
}