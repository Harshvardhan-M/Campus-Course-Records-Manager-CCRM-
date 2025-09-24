import edu.ccrm.cli.CLIMenu;

/**
 * Main entry point for the Campus Course & Records Manager (CCRM) application.
 * Demonstrates proper application startup, exception handling, and system initialization.
 */
public class Main {
    
    /**
     * Main method - application entry point.
     * Demonstrates top-level exception handling and application lifecycle management.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        
        // Enable assertions for development and testing
        // Note: Run with -ea flag to enable assertions
        assert validateSystemRequirements() : "System requirements not met";
        
        System.out.println("Campus Course & Records Manager (CCRM) - Starting...");
        
        try {
            // Initialize and run the application
            CLIMenu cliMenu = new CLIMenu();
            cliMenu.run();
            
        } catch (Exception e) {
            // Top-level exception handler
            System.err.println("FATAL ERROR: Application failed to start or encountered an unrecoverable error.");
            System.err.println("Error details: " + e.getMessage());
            
            // In production, this might log to a file or monitoring system
            e.printStackTrace();
            
            // Graceful shutdown
            System.err.println("Application will now exit.");
            System.exit(1);
            
        } finally {
            // Cleanup operations that should always execute
            performShutdownCleanup();
        }
        
        // Normal termination
        System.exit(0);
    }
    
    /**
     * Validates system requirements before starting the application.
     * Demonstrates assertion usage and system validation.
     * 
     * @return true if all requirements are met
     */
    private static boolean validateSystemRequirements() {
        // Check Java version
        String javaVersion = System.getProperty("java.version");
        System.out.println("Java version: " + javaVersion);
        
        // Ensure we have at least Java 8
        try {
            String[] versionParts = javaVersion.split("\\.");
            int majorVersion = Integer.parseInt(versionParts[0]);
            if (majorVersion < 8 && !javaVersion.startsWith("1.8")) {
                System.err.println("ERROR: Java 8 or higher is required. Current version: " + javaVersion);
                return false;
            }
        } catch (Exception e) {
            System.err.println("WARNING: Could not parse Java version: " + javaVersion);
        }
        
        // Check available memory
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        
        System.out.printf("Memory: Max=%d MB, Total=%d MB, Free=%d MB%n",
                maxMemory / 1024 / 1024,
                totalMemory / 1024 / 1024,
                freeMemory / 1024 / 1024);
        
        // Ensure we have at least 64MB available
        if (maxMemory < 64 * 1024 * 1024) {
            System.err.println("WARNING: Low memory available. Application may not perform optimally.");
        }
        
        // Check for required directories and create if needed
        createRequiredDirectories();
        
        // Validate system properties
        String os = System.getProperty("os.name");
        String userHome = System.getProperty("user.home");
        String workingDir = System.getProperty("user.dir");
        
        System.out.println("Operating System: " + os);
        System.out.println("User Home: " + userHome);
        System.out.println("Working Directory: " + workingDir);
        
        return true;
    }
    
    /**
     * Creates required directories for the application.
     * Demonstrates file system operations and error handling.
     */
    private static void createRequiredDirectories() {
        String[] requiredDirs = {
            "config",
            "data", 
            "imports",
            "exports",
            "backups"
        };
        
        for (String dirName : requiredDirs) {
            java.io.File dir = new java.io.File(dirName);
            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    System.out.println("Created directory: " + dirName);
                } else {
                    System.err.println("WARNING: Could not create directory: " + dirName);
                }
            }
        }
    }
    
    /**
     * Performs cleanup operations during application shutdown.
     * Demonstrates finally block usage and cleanup patterns.
     */
    private static void performShutdownCleanup() {
        try {
            // Log application shutdown
            System.out.println("Performing shutdown cleanup...");
            
            // Force garbage collection (optional in production)
            if (Boolean.getBoolean("ccrm.cleanup.gc")) {
                System.gc();
                System.out.println("Garbage collection requested.");
            }
            
            // Display memory usage statistics
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            System.out.printf("Final memory usage: %d MB used, %d MB free%n",
                    usedMemory / 1024 / 1024,
                    freeMemory / 1024 / 1024);
            
            System.out.println("Cleanup completed successfully.");
            
        } catch (Exception e) {
            // Even cleanup can fail, but we don't want to crash during shutdown
            System.err.println("Warning: Error during cleanup: " + e.getMessage());
        }
    }
    
    /**
     * Alternative main method for testing with specific configurations.
     * Demonstrates method overloading and configuration options.
     */
    public static void main(String[] args, boolean debugMode, String configFile) {
        if (debugMode) {
            System.setProperty("ccrm.debug.enabled", "true");
            System.setProperty("ccrm.cleanup.gc", "true");
            System.out.println("Debug mode enabled.");
        }
        
        if (configFile != null && !configFile.isEmpty()) {
            System.setProperty("ccrm.config.file", configFile);
            System.out.println("Using config file: " + configFile);
        }
        
        // Call main method
        main(args);
    }
    
    /**
     * Prints application information and usage instructions.
     * Demonstrates static utility methods and documentation.
     */
    public static void printUsageInformation() {
        System.out.println("Campus Course & Records Manager (CCRM) v1.0");
        System.out.println("Usage: java Main [options]");
        System.out.println("");
        System.out.println("Options:");
        System.out.println("  -Dccrm.debug.enabled=true    Enable debug mode");
        System.out.println("  -Dccrm.config.file=<file>    Use custom config file");
        System.out.println("  -Dccrm.cleanup.gc=true       Force garbage collection on exit");
        System.out.println("  -ea                          Enable assertions");
        System.out.println("");
        System.out.println("Examples:");
        System.out.println("  java -ea Main");
        System.out.println("  java -ea -Dccrm.debug.enabled=true Main");
        System.out.println("");
        System.out.println("For more information, see README.md");
    }
}