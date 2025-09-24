package edu.ccrm.config;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

/**
 * Application configuration using Singleton pattern.
 * Demonstrates Singleton implementation, lazy initialization, and thread safety.
 */
public class AppConfig {
    
    // Singleton instance with thread-safe lazy initialization
    private static volatile AppConfig instance;
    private static final Object lock = new Object();
    
    // Configuration properties
    private Properties properties;
    private static final String CONFIG_FILE = "config/app.properties";
    
    // Default configuration values
    private static final int DEFAULT_MAX_CREDITS_PER_SEMESTER = 18;
    private static final int DEFAULT_MIN_CREDITS_PER_SEMESTER = 12;
    private static final int DEFAULT_MAX_ENROLLMENTS_PER_STUDENT = 6;
    private static final String DEFAULT_INSTITUTION_NAME = "Campus College";
    private static final String DEFAULT_SEMESTER_SYSTEM = "TRADITIONAL";
    private static final boolean DEFAULT_GRADE_REPLACEMENT = false;
    private static final int DEFAULT_BACKUP_RETENTION_DAYS = 30;
    
    // Private constructor prevents direct instantiation
    private AppConfig() {
        loadConfiguration();
    }
    
    /**
     * Gets the singleton instance using double-checked locking pattern.
     * Demonstrates thread-safe lazy initialization.
     */
    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }
    
    /**
     * Loads configuration from properties file with fallback to defaults.
     * Demonstrates exception handling and resource management.
     */
    private void loadConfiguration() {
        properties = new Properties();
        Path configPath = Paths.get(CONFIG_FILE);
        
        // Load default properties first
        setDefaultProperties();
        
        // Try to load from file
        if (Files.exists(configPath)) {
            try (InputStream input = Files.newInputStream(configPath)) {
                properties.load(input);
                System.out.println("Configuration loaded from: " + CONFIG_FILE);
            } catch (IOException e) {
                System.err.println("Warning: Could not load configuration file. Using defaults. " + e.getMessage());
            }
        } else {
            System.out.println("Configuration file not found. Using default settings.");
            // Create default configuration file
            try {
                saveConfiguration();
            } catch (IOException e) {
                System.err.println("Could not create default configuration file: " + e.getMessage());
            }
        }
    }
    
    /**
     * Sets default property values.
     */
    private void setDefaultProperties() {
        properties.setProperty("app.institution.name", DEFAULT_INSTITUTION_NAME);
        properties.setProperty("app.semester.system", DEFAULT_SEMESTER_SYSTEM);
        properties.setProperty("student.max.credits.per.semester", String.valueOf(DEFAULT_MAX_CREDITS_PER_SEMESTER));
        properties.setProperty("student.min.credits.per.semester", String.valueOf(DEFAULT_MIN_CREDITS_PER_SEMESTER));
        properties.setProperty("student.max.enrollments", String.valueOf(DEFAULT_MAX_ENROLLMENTS_PER_STUDENT));
        properties.setProperty("grading.allow.replacement", String.valueOf(DEFAULT_GRADE_REPLACEMENT));
        properties.setProperty("backup.retention.days", String.valueOf(DEFAULT_BACKUP_RETENTION_DAYS));
        properties.setProperty("app.version", "1.0.0");
        properties.setProperty("app.debug.enabled", "false");
    }
    
    /**
     * Saves current configuration to file.
     */
    public synchronized void saveConfiguration() throws IOException {
        Path configPath = Paths.get(CONFIG_FILE);
        Files.createDirectories(configPath.getParent());
        
        try (OutputStream output = Files.newOutputStream(configPath)) {
            properties.store(output, "Campus Course & Records Manager Configuration");
            System.out.println("Configuration saved to: " + CONFIG_FILE);
        }
    }
    
    // Configuration getter methods with type safety
    
    public String getInstitutionName() {
        return properties.getProperty("app.institution.name", DEFAULT_INSTITUTION_NAME);
    }
    
    public int getMaxCreditsPerSemester() {
        return getIntProperty("student.max.credits.per.semester", DEFAULT_MAX_CREDITS_PER_SEMESTER);
    }
    
    public int getMinCreditsPerSemester() {
        return getIntProperty("student.min.credits.per.semester", DEFAULT_MIN_CREDITS_PER_SEMESTER);
    }
    
    public int getMaxEnrollmentsPerStudent() {
        return getIntProperty("student.max.enrollments", DEFAULT_MAX_ENROLLMENTS_PER_STUDENT);
    }
    
    public boolean isGradeReplacementAllowed() {
        return getBooleanProperty("grading.allow.replacement", DEFAULT_GRADE_REPLACEMENT);
    }
    
    public int getBackupRetentionDays() {
        return getIntProperty("backup.retention.days", DEFAULT_BACKUP_RETENTION_DAYS);
    }
    
    public String getAppVersion() {
        return properties.getProperty("app.version", "1.0.0");
    }
    
    public boolean isDebugEnabled() {
        return getBooleanProperty("app.debug.enabled", false);
    }
    
    public String getSemesterSystem() {
        return properties.getProperty("app.semester.system", DEFAULT_SEMESTER_SYSTEM);
    }
    
    // Configuration setter methods with validation
    
    public synchronized void setInstitutionName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            properties.setProperty("app.institution.name", name.trim());
        }
    }
    
    public synchronized void setMaxCreditsPerSemester(int maxCredits) {
        if (maxCredits > 0 && maxCredits <= 30) {
            properties.setProperty("student.max.credits.per.semester", String.valueOf(maxCredits));
        }
    }
    
    public synchronized void setMinCreditsPerSemester(int minCredits) {
        if (minCredits > 0 && minCredits <= 18) {
            properties.setProperty("student.min.credits.per.semester", String.valueOf(minCredits));
        }
    }
    
    public synchronized void setGradeReplacementAllowed(boolean allowed) {
        properties.setProperty("grading.allow.replacement", String.valueOf(allowed));
    }
    
    public synchronized void setBackupRetentionDays(int days) {
        if (days > 0) {
            properties.setProperty("backup.retention.days", String.valueOf(days));
        }
    }
    
    public synchronized void setDebugEnabled(boolean enabled) {
        properties.setProperty("app.debug.enabled", String.valueOf(enabled));
    }
    
    // Helper methods for type conversion with error handling
    
    private int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            System.err.println("Invalid integer value for property " + key + ". Using default: " + defaultValue);
            return defaultValue;
        }
    }
    
    private boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key, String.valueOf(defaultValue));
        return "true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "1".equals(value);
    }
    
    private double getDoubleProperty(String key, double defaultValue) {
        try {
            return Double.parseDouble(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            System.err.println("Invalid double value for property " + key + ". Using default: " + defaultValue);
            return defaultValue;
        }
    }
    
    // Utility methods for configuration management
    
    /**
     * Reloads configuration from file.
     */
    public synchronized void reloadConfiguration() {
        loadConfiguration();
        System.out.println("Configuration reloaded");
    }
    
    /**
     * Resets to default configuration.
     */
    public synchronized void resetToDefaults() {
        properties.clear();
        setDefaultProperties();
        try {
            saveConfiguration();
            System.out.println("Configuration reset to defaults");
        } catch (IOException e) {
            System.err.println("Error saving default configuration: " + e.getMessage());
        }
    }
    
    /**
     * Gets all configuration properties as a copy.
     */
    public Properties getAllProperties() {
        Properties copy = new Properties();
        copy.putAll(properties);
        return copy;
    }
    
    /**
     * Validates current configuration and returns validation errors.
     */
    public java.util.List<String> validateConfiguration() {
        java.util.List<String> errors = new java.util.ArrayList<>();
        
        // Validate credit limits
        int maxCredits = getMaxCreditsPerSemester();
        int minCredits = getMinCreditsPerSemester();
        
        if (minCredits > maxCredits) {
            errors.add("Minimum credits per semester cannot be greater than maximum credits");
        }
        
        if (maxCredits > 30) {
            errors.add("Maximum credits per semester should not exceed 30");
        }
        
        if (minCredits < 1) {
            errors.add("Minimum credits per semester must be at least 1");
        }
        
        // Validate retention period
        if (getBackupRetentionDays() < 1) {
            errors.add("Backup retention days must be at least 1");
        }
        
        // Validate institution name
        if (getInstitutionName().trim().isEmpty()) {
            errors.add("Institution name cannot be empty");
        }
        
        return errors;
    }
    
    /**
     * Displays current configuration in a formatted manner.
     */
    public void displayConfiguration() {
        System.out.println("\n=== Application Configuration ===");
        System.out.println("Institution: " + getInstitutionName());
        System.out.println("Version: " + getAppVersion());
        System.out.println("Semester System: " + getSemesterSystem());
        System.out.println("Max Credits/Semester: " + getMaxCreditsPerSemester());
        System.out.println("Min Credits/Semester: " + getMinCreditsPerSemester());
        System.out.println("Max Enrollments/Student: " + getMaxEnrollmentsPerStudent());
        System.out.println("Grade Replacement: " + (isGradeReplacementAllowed() ? "Enabled" : "Disabled"));
        System.out.println("Backup Retention: " + getBackupRetentionDays() + " days");
        System.out.println("Debug Mode: " + (isDebugEnabled() ? "Enabled" : "Disabled"));
        System.out.println("===================================\n");
    }
    
    // Prevent cloning and serialization to maintain singleton integrity
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cannot clone singleton instance");
    }
    
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        throw new IOException("Cannot deserialize singleton instance");
    }
    
    private void writeObject(ObjectOutputStream oos) throws IOException {
        throw new IOException("Cannot serialize singleton instance");
    }
}