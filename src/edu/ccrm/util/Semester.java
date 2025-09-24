package edu.ccrm.util;

/**
 * Enum representing academic semesters.
 * Demonstrates enum usage with methods and properties.
 */
public enum Semester {
    SPRING("Spring", 1, "January - May"),
    SUMMER("Summer", 2, "June - August"),
    FALL("Fall", 3, "September - December");
    
    private final String displayName;
    private final int order;
    private final String description;
    
    Semester(String displayName, int order, String description) {
        this.displayName = displayName;
        this.order = order;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getOrder() {
        return order;
    }
    
    public String getDescription() {
        return description;
    }
    
    // Static method to get semester by order
    public static Semester getByOrder(int order) {
        for (Semester semester : values()) {
            if (semester.order == order) {
                return semester;
            }
        }
        throw new IllegalArgumentException("Invalid semester order: " + order);
    }
    
    // Get next semester
    public Semester getNext() {
        switch (this) {
            case SPRING: return SUMMER;
            case SUMMER: return FALL;
            case FALL: return SPRING;
            default: throw new IllegalStateException("Unknown semester: " + this);
        }
    }
    
    // Get previous semester
    public Semester getPrevious() {
        switch (this) {
            case SPRING: return FALL;
            case SUMMER: return SPRING;
            case FALL: return SUMMER;
            default: throw new IllegalStateException("Unknown semester: " + this);
        }
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}