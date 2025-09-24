package edu.ccrm.exception;

/**
 * Custom exception thrown when attempting to enroll a student in a course
 * they are already enrolled in.
 * Demonstrates custom exception creation and inheritance.
 */
public class DuplicateEnrollmentException extends Exception {
    
    private String studentId;
    private String courseId;
    
    public DuplicateEnrollmentException(String studentId, String courseId) {
        super(String.format("Student %s is already enrolled in course %s", studentId, courseId));
        this.studentId = studentId;
        this.courseId = courseId;
    }
    
    public DuplicateEnrollmentException(String studentId, String courseId, String message) {
        super(message);
        this.studentId = studentId;
        this.courseId = courseId;
    }
    
    public DuplicateEnrollmentException(String studentId, String courseId, String message, Throwable cause) {
        super(message, cause);
        this.studentId = studentId;
        this.courseId = courseId;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public String getCourseId() {
        return courseId;
    }
}