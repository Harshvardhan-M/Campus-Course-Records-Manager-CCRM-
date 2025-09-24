package edu.ccrm.util;

/**
 * Enum representing academic grades with grade points.
 * Demonstrates enum with complex behavior and business logic.
 */
public enum Grade {
    S("Satisfactory", 0.0, true, "Pass/Fail - Satisfactory"),
    A("A", 4.0, true, "Excellent"),
    B("B", 3.0, true, "Good"),
    C("C", 2.0, true, "Satisfactory"),
    D("D", 1.0, true, "Below Average"),
    F("F", 0.0, false, "Fail"),
    I("Incomplete", 0.0, false, "Course not completed"),
    W("Withdrawal", 0.0, false, "Student withdrew from course"),
    AU("Audit", 0.0, false, "Audited course - no credit");
    
    private final String displayName;
    private final double gradePoints;
    private final boolean passingGrade;
    private final String description;
    
    Grade(String displayName, double gradePoints, boolean passingGrade, String description) {
        this.displayName = displayName;
        this.gradePoints = gradePoints;
        this.passingGrade = passingGrade;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public double getGradePoints() {
        return gradePoints;
    }
    
    public boolean isPassingGrade() {
        return passingGrade;
    }
    
    public String getDescription() {
        return description;
    }
    
    // Static method to get grade by points range
    public static Grade getByPoints(double points) {
        if (points >= 4.0) return A;
        else if (points >= 3.0) return B;
        else if (points >= 2.0) return C;
        else if (points >= 1.0) return D;
        else return F;
    }
    
    // Static method to parse grade from string
    public static Grade fromString(String grade) {
        if (grade == null || grade.trim().isEmpty()) {
            throw new IllegalArgumentException("Grade cannot be null or empty");
        }
        
        String upperGrade = grade.trim().toUpperCase();
        
        try {
            return Grade.valueOf(upperGrade);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid grade: " + grade);
        }
    }
    
    // Check if grade is better than another grade
    public boolean isBetterThan(Grade other) {
        if (other == null) return true;
        return this.gradePoints > other.gradePoints;
    }
    
    // Get letter representation for transcript display
    public String getLetterGrade() {
        switch (this) {
            case S: return "S";
            case I: return "I";
            case W: return "W";
            case AU: return "AU";
            default: return this.name();
        }
    }
    
    // Get color coding for UI display (if needed)
    public String getColorCode() {
        switch (this) {
            case A: return "GREEN";
            case B: return "BLUE";
            case C: return "YELLOW";
            case D: return "ORANGE";
            case F: return "RED";
            default: return "GRAY";
        }
    }
    
    @Override
    public String toString() {
        return getLetterGrade();
    }
}