package edu.ccrm.exception;

/**
 * Custom exception thrown when a student attempts to enroll in courses
 * that would exceed the maximum credit limit per semester.
 * Demonstrates custom exception with additional context information.
 */
public class MaxCreditLimitExceededException extends Exception {
    
    private String studentId;
    private int currentCredits;
    private int attemptedCredits;
    private int maxCredits;
    
    public MaxCreditLimitExceededException(String studentId, int currentCredits, 
                                         int attemptedCredits, int maxCredits) {
        super(String.format("Student %s credit limit exceeded: Current=%d, Attempted=%d, Max=%d", 
                          studentId, currentCredits, attemptedCredits, maxCredits));
        this.studentId = studentId;
        this.currentCredits = currentCredits;
        this.attemptedCredits = attemptedCredits;
        this.maxCredits = maxCredits;
    }
    
    public MaxCreditLimitExceededException(String message) {
        super(message);
    }
    
    public MaxCreditLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public int getCurrentCredits() {
        return currentCredits;
    }
    
    public int getAttemptedCredits() {
        return attemptedCredits;
    }
    
    public int getMaxCredits() {
        return maxCredits;
    }
    
    public int getExcessCredits() {
        return (currentCredits + attemptedCredits) - maxCredits;
    }
}