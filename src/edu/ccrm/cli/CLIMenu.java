package edu.ccrm.cli;

import edu.ccrm.domain.*;
import edu.ccrm.service.*;
import edu.ccrm.io.*;
import edu.ccrm.config.AppConfig;
import edu.ccrm.util.*;
import edu.ccrm.exception.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Command-line interface for the Campus Course & Records Manager.
 * Demonstrates comprehensive CLI design, switch statements, and user interaction.
 */
public class CLIMenu {
    
    private Scanner scanner;
    private StudentService studentService;
    private CourseService courseService;
    private EnrollmentService enrollmentService;
    private TranscriptService transcriptService;
    private ImportExportService importExportService;
    private BackupService backupService;
    private AppConfig config;
    
    // Menu state management
    private boolean running = true;
    private String currentUser = "Administrator";
    
    // Constructor with dependency injection
    public CLIMenu() {
        this.scanner = new Scanner(System.in);
        this.config = AppConfig.getInstance();
        
        // Initialize services with proper dependencies
        this.studentService = new StudentService();
        this.courseService = new CourseService();
        this.enrollmentService = new EnrollmentService(studentService, courseService);
        this.transcriptService = new TranscriptService(studentService);
        this.importExportService = new ImportExportService(studentService, courseService, enrollmentService);
        this.backupService = new BackupService();
    }
    
    /**
     * Main menu loop demonstrating switch statements and user interaction.
     */
    public void run() {
        displayWelcomeMessage();
        
        while (running) {
            try {
                displayMainMenu();
                int choice = getIntInput("Enter your choice: ");
                
                switch (choice) {
                    case 1:
                        handleStudentManagement();
                        break;
                    case 2:
                        handleCourseManagement();
                        break;
                    case 3:
                        handleEnrollmentManagement();
                        break;
                    case 4:
                        handleTranscriptGeneration();
                        break;
                    case 5:
                        handleImportExport();
                        break;
                    case 6:
                        handleBackupRestore();
                        break;
                    case 7:
                        handleReports();
                        break;
                    case 8:
                        handleConfiguration();
                        break;
                    case 9:
                        handleHelp();
                        break;
                    case 0:
                        handleExit();
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
                
                if (running) {
                    promptContinue();
                }
                
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                if (config.isDebugEnabled()) {
                    e.printStackTrace();
                }
                promptContinue();
            }
        }
        
        cleanup();
    }
    
    // Main menu display
    private void displayMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    CAMPUS COURSE & RECORDS MANAGER");
        System.out.println("    " + config.getInstitutionName());
        System.out.println("    User: " + currentUser);
        System.out.println("=".repeat(50));
        System.out.println("1. Student Management");
        System.out.println("2. Course Management");
        System.out.println("3. Enrollment Management");
        System.out.println("4. Transcript Generation");
        System.out.println("5. Import/Export Data");
        System.out.println("6. Backup & Restore");
        System.out.println("7. Reports & Statistics");
        System.out.println("8. Configuration");
        System.out.println("9. Help");
        System.out.println("0. Exit");
        System.out.println("=".repeat(50));
    }
    
    // Student management submenu
    private void handleStudentManagement() {
        System.out.println("\n=== STUDENT MANAGEMENT ===");
        System.out.println("1. Add New Student");
        System.out.println("2. View All Students");
        System.out.println("3. Search Students");
        System.out.println("4. Update Student");
        System.out.println("5. View Student Details");
        System.out.println("6. Delete Student");
        System.out.println("0. Back to Main Menu");
        
        int choice = getIntInput("Enter choice: ");
        
        switch (choice) {
            case 1:
                addNewStudent();
                break;
            case 2:
                viewAllStudents();
                break;
            case 3:
                searchStudents();
                break;
            case 4:
                updateStudent();
                break;
            case 5:
                viewStudentDetails();
                break;
            case 6:
                deleteStudent();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    // Course management submenu
    private void handleCourseManagement() {
        System.out.println("\n=== COURSE MANAGEMENT ===");
        System.out.println("1. Create New Course");
        System.out.println("2. View All Courses");
        System.out.println("3. Search Courses");
        System.out.println("4. Update Course");
        System.out.println("5. View Course Details");
        System.out.println("6. Assign Instructor");
        System.out.println("7. View Courses by Semester");
        System.out.println("0. Back to Main Menu");
        
        int choice = getIntInput("Enter choice: ");
        
        switch (choice) {
            case 1:
                createNewCourse();
                break;
            case 2:
                viewAllCourses();
                break;
            case 3:
                searchCourses();
                break;
            case 4:
                updateCourse();
                break;
            case 5:
                viewCourseDetails();
                break;
            case 6:
                assignInstructor();
                break;
            case 7:
                viewCoursesBySemester();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    // Enrollment management with exception handling
    private void handleEnrollmentManagement() {
        System.out.println("\n=== ENROLLMENT MANAGEMENT ===");
        System.out.println("1. Enroll Student in Course");
        System.out.println("2. Assign Grades");
        System.out.println("3. Withdraw Student");
        System.out.println("4. View Student Enrollments");
        System.out.println("5. View Course Enrollments");
        System.out.println("6. View Enrollment Statistics");
        System.out.println("0. Back to Main Menu");
        
        int choice = getIntInput("Enter choice: ");
        
        switch (choice) {
            case 1:
                enrollStudentInCourse();
                break;
            case 2:
                assignGrades();
                break;
            case 3:
                withdrawStudent();
                break;
            case 4:
                viewStudentEnrollments();
                break;
            case 5:
                viewCourseEnrollments();
                break;
            case 6:
                viewEnrollmentStatistics();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    // Transcript generation with Builder pattern usage
    private void handleTranscriptGeneration() {
        System.out.println("\n=== TRANSCRIPT GENERATION ===");
        System.out.println("1. Generate Complete Transcript");
        System.out.println("2. Generate Semester Transcript");
        System.out.println("3. Generate Department Transcript");
        System.out.println("4. Generate Honor Roll Transcript");
        System.out.println("5. Export Transcript to File");
        System.out.println("6. View Transcript Statistics");
        System.out.println("0. Back to Main Menu");
        
        int choice = getIntInput("Enter choice: ");
        
        switch (choice) {
            case 1:
                generateCompleteTranscript();
                break;
            case 2:
                generateSemesterTranscript();
                break;
            case 3:
                generateDepartmentTranscript();
                break;
            case 4:
                generateHonorRollTranscript();
                break;
            case 5:
                exportTranscriptToFile();
                break;
            case 6:
                viewTranscriptStatistics();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    // Import/Export operations
    private void handleImportExport() {
        System.out.println("\n=== IMPORT/EXPORT DATA ===");
        System.out.println("1. Import Students from CSV");
        System.out.println("2. Import Courses from CSV");
        System.out.println("3. Import Enrollments from CSV");
        System.out.println("4. Export Students to CSV");
        System.out.println("5. Export Courses to CSV");
        System.out.println("6. Export Enrollments to CSV");
        System.out.println("7. Bulk Import All Data");
        System.out.println("8. Bulk Export All Data");
        System.out.println("0. Back to Main Menu");
        
        int choice = getIntInput("Enter choice: ");
        
        switch (choice) {
            case 1:
                importStudentsFromCSV();
                break;
            case 2:
                importCoursesFromCSV();
                break;
            case 3:
                importEnrollmentsFromCSV();
                break;
            case 4:
                exportStudentsToCSV();
                break;
            case 5:
                exportCoursesToCSV();
                break;
            case 6:
                exportEnrollmentsToCSV();
                break;
            case 7:
                bulkImportAllData();
                break;
            case 8:
                bulkExportAllData();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    // Backup and restore operations
    private void handleBackupRestore() {
        System.out.println("\n=== BACKUP & RESTORE ===");
        System.out.println("1. Create Full Backup");
        System.out.println("2. Create Incremental Backup");
        System.out.println("3. List Available Backups");
        System.out.println("4. Restore from Backup");
        System.out.println("5. Clean Up Old Backups");
        System.out.println("0. Back to Main Menu");
        
        int choice = getIntInput("Enter choice: ");
        
        switch (choice) {
            case 1:
                createFullBackup();
                break;
            case 2:
                createIncrementalBackup();
                break;
            case 3:
                listAvailableBackups();
                break;
            case 4:
                restoreFromBackup();
                break;
            case 5:
                cleanupOldBackups();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    // Reports and statistics
    private void handleReports() {
        System.out.println("\n=== REPORTS & STATISTICS ===");
        System.out.println("1. Student Statistics");
        System.out.println("2. Course Statistics");
        System.out.println("3. Enrollment Statistics");
        System.out.println("4. Grade Distribution Report");
        System.out.println("5. Department Analysis");
        System.out.println("6. Top Performing Students");
        System.out.println("7. System Statistics");
        System.out.println("0. Back to Main Menu");
        
        int choice = getIntInput("Enter choice: ");
        
        switch (choice) {
            case 1:
                showStudentStatistics();
                break;
            case 2:
                showCourseStatistics();
                break;
            case 3:
                showEnrollmentStatistics();
                break;
            case 4:
                showGradeDistribution();
                break;
            case 5:
                showDepartmentAnalysis();
                break;
            case 6:
                showTopPerformingStudents();
                break;
            case 7:
                showSystemStatistics();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    // Student management implementations
    
    private void addNewStudent() {
        System.out.println("\n--- Add New Student ---");
        
        try {
            String id = getStringInput("Student ID (format S1234567): ");
            String firstName = getStringInput("First Name: ");
            String lastName = getStringInput("Last Name: ");
            String email = getStringInput("Email: ");
            String birthDateStr = getStringInput("Birth Date (YYYY-MM-DD): ");
            String major = getStringInput("Major: ");
            
            LocalDate birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            
            if (studentService.addStudent(id, firstName, lastName, email, birthDate, major)) {
                System.out.println("✓ Student added successfully!");
            } else {
                System.out.println("✗ Failed to add student. Please check the input data.");
            }
            
        } catch (DateTimeParseException e) {
            System.out.println("✗ Invalid date format. Please use YYYY-MM-DD format.");
        } catch (Exception e) {
            System.out.println("✗ Error adding student: " + e.getMessage());
        }
    }
    
    private void viewAllStudents() {
        System.out.println("\n--- All Students ---");
        List<Student> students = studentService.getAllStudents();
        
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        
        System.out.printf("%-12s %-20s %-30s %-15s %-8s %-6s%n",
                "ID", "Name", "Email", "Major", "Credits", "GPA");
        System.out.println("-".repeat(100));
        
        for (Student student : students) {
            System.out.printf("%-12s %-20s %-30s %-15s %-8d %-6.2f%n",
                    student.getId(),
                    student.getFullName(),
                    student.getEmail(),
                    student.getMajor(),
                    student.getTotalCredits(),
                    student.getGpa());
        }
        
        System.out.println("\nTotal students: " + students.size());
    }
    
    private void searchStudents() {
        System.out.println("\n--- Search Students ---");
        String searchTerm = getStringInput("Enter search term (name, email, major, or ID): ");
        
        List<Student> results = studentService.searchByText(searchTerm);
        
        if (results.isEmpty()) {
            System.out.println("No students found matching: " + searchTerm);
            return;
        }
        
        System.out.println("Search results (" + results.size() + " found):");
        for (Student student : results) {
            System.out.println("• " + student.toString());
        }
    }
    
    // Course management implementations
    
    private void createNewCourse() {
        System.out.println("\n--- Create New Course ---");
        
        try {
            String courseId = getStringInput("Course ID (format CS101): ");
            String title = getStringInput("Course Title: ");
            int credits = getIntInput("Credits (1-6): ");
            String department = getStringInput("Department: ");
            String description = getStringInput("Description: ");
            String prerequisites = getStringInput("Prerequisites (optional): ");
            
            System.out.println("Available semesters: " + Arrays.toString(Semester.values()));
            String semesterStr = getStringInput("Semester: ").toUpperCase();
            Semester semester = Semester.valueOf(semesterStr);
            
            int year = getIntInput("Year: ");
            int maxEnrollment = getIntInput("Max Enrollment: ");
            
            if (courseService.createCourse(courseId, title, credits, department,
                    description, prerequisites, null, semester, year, maxEnrollment)) {
                System.out.println("✓ Course created successfully!");
            } else {
                System.out.println("✗ Failed to create course. Please check the input data.");
            }
            
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Invalid semester. Available options: " + Arrays.toString(Semester.values()));
        } catch (Exception e) {
            System.out.println("✗ Error creating course: " + e.getMessage());
        }
    }
    
    private void viewAllCourses() {
        System.out.println("\n--- All Courses ---");
        List<Course> courses = courseService.getAllCourses();
        
        if (courses.isEmpty()) {
            System.out.println("No courses found.");
            return;
        }
        
        System.out.printf("%-10s %-30s %-8s %-15s %-10s %-8s %-6s%n",
                "Course ID", "Title", "Credits", "Department", "Semester", "Year", "Enroll");
        System.out.println("-".repeat(100));
        
        for (Course course : courses) {
            System.out.printf("%-10s %-30s %-8d %-15s %-10s %-8d %-6s%n",
                    course.getCourseId(),
                    course.getTitle().length() > 30 ? course.getTitle().substring(0, 27) + "..." : course.getTitle(),
                    course.getCredits(),
                    course.getDepartment(),
                    course.getSemester(),
                    course.getYear(),
                    course.getCurrentEnrollment() + "/" + course.getMaxEnrollment());
        }
        
        System.out.println("\nTotal courses: " + courses.size());
    }
    
    // Enrollment management with comprehensive exception handling
    
    private void enrollStudentInCourse() {
        System.out.println("\n--- Enroll Student in Course ---");
        
        try {
            String studentId = getStringInput("Student ID: ");
            String courseId = getStringInput("Course ID: ");
            
            // Pre-enrollment validation display
            Student student = studentService.findById(studentId);
            Course course = courseService.findById(courseId);
            
            if (student == null) {
                System.out.println("✗ Student not found: " + studentId);
                return;
            }
            
            if (course == null) {
                System.out.println("✗ Course not found: " + courseId);
                return;
            }
            
            // Display enrollment preview
            System.out.println("\nEnrollment Preview:");
            System.out.println("Student: " + student.getFullName() + " (" + student.getId() + ")");
            System.out.println("Course: " + course.getTitle() + " (" + course.getCourseId() + ")");
            System.out.println("Credits: " + course.getCredits());
            
            int currentCredits = enrollmentService.getCurrentSemesterCredits(
                    studentId, course.getSemester(), course.getYear());
            System.out.println("Current semester credits: " + currentCredits);
            System.out.println("After enrollment: " + (currentCredits + course.getCredits()));
            System.out.println("Course availability: " + course.getCurrentEnrollment() + "/" + course.getMaxEnrollment());
            
            if (!getConfirmation("Proceed with enrollment?")) {
                System.out.println("Enrollment cancelled.");
                return;
            }
            
            String enrollmentId = enrollmentService.enrollStudent(studentId, courseId);
            System.out.println("✓ Student enrolled successfully! Enrollment ID: " + enrollmentId);
            
        } catch (DuplicateEnrollmentException e) {
            System.out.println("✗ Duplicate enrollment: " + e.getMessage());
        } catch (MaxCreditLimitExceededException e) {
            System.out.println("✗ Credit limit exceeded: " + e.getMessage());
            System.out.println("  Current credits: " + e.getCurrentCredits());
            System.out.println("  Attempted credits: " + e.getAttemptedCredits());
            System.out.println("  Maximum allowed: " + e.getMaxCredits());
            System.out.println("  Excess: " + e.getExcessCredits());
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Enrollment error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ Unexpected error during enrollment: " + e.getMessage());
            if (config.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }
    
    private void assignGrades() {
        System.out.println("\n--- Assign Grades ---");
        
        try {
            String enrollmentId = getStringInput("Enrollment ID: ");
            
            Enrollment enrollment = enrollmentService.findById(enrollmentId);
            if (enrollment == null) {
                System.out.println("✗ Enrollment not found: " + enrollmentId);
                return;
            }
            
            System.out.println("Enrollment Details:");
            System.out.println("Student: " + enrollment.getStudent().getFullName());
            System.out.println("Course: " + enrollment.getCourse().getTitle());
            System.out.println("Current Status: " + enrollment.getStatus());
            System.out.println("Current Grade: " + (enrollment.getGrade() != null ? enrollment.getGrade() : "Not assigned"));
            
            System.out.println("\nAvailable Grades: " + Arrays.toString(Grade.values()));
            String gradeStr = getStringInput("Enter grade: ").toUpperCase();
            
            Grade grade = Grade.fromString(gradeStr);
            
            if (enrollmentService.assignGrade(enrollmentId, grade)) {
                System.out.println("✓ Grade assigned successfully!");
                
                // Display updated student GPA
                Student student = enrollment.getStudent();
                studentService.updateStudentGPA(student.getId());
                System.out.println("Student's updated GPA: " + String.format("%.2f", student.getGpa()));
            } else {
                System.out.println("✗ Failed to assign grade.");
            }
            
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Invalid grade: " + e.getMessage());
            System.out.println("Available grades: " + Arrays.toString(Grade.values()));
        } catch (Exception e) {
            System.out.println("✗ Error assigning grade: " + e.getMessage());
        }
    }
    
    // Transcript generation implementations
    
    private void generateCompleteTranscript() {
        System.out.println("\n--- Generate Complete Transcript ---");
        
        String studentId = getStringInput("Student ID: ");
        String notes = getStringInput("Additional notes (optional): ");
        
        try {
            Transcript transcript = transcriptService.generateCompleteTranscript(studentId, notes);
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println(transcript.generateTranscriptReport());
            System.out.println("=".repeat(80));
            
            if (getConfirmation("Save transcript to file?")) {
                String filename = studentId + "_complete_transcript_" + 
                        java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                transcriptService.exportTranscriptToFile(studentId, filename, "TXT");
                System.out.println("✓ Transcript saved to exports/" + filename + ".txt");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Error generating transcript: " + e.getMessage());
        }
    }
    
    // Backup operations
    
    private void createFullBackup() {
        System.out.println("\n--- Create Full Backup ---");
        System.out.println("Creating full backup of all data...");
        
        BackupService.BackupResult result = backupService.createFullBackup();
        
        if (result.isSuccess()) {
            System.out.println("✓ " + result.getMessage());
            System.out.println("Compression ratio: " + String.format("%.1f%%", 
                    result.getCompressionRatio() * 100));
        } else {
            System.out.println("✗ " + result.getMessage());
        }
    }
    
    // Report generation
    
    private void showStudentStatistics() {
        System.out.println("\n--- Student Statistics ---");
        
        try {
            Student.StudentStatistics stats = studentService.getStatistics();
            List<Student> students = studentService.getAllStudents();
            
            System.out.println("Total Students: " + stats.getTotalStudents());
            System.out.println("Average GPA: " + String.format("%.2f", stats.getAverageGPA()));
            System.out.println("Total Credits Earned: " + stats.getTotalCreditsEarned());
            
            if (!students.isEmpty()) {
                // Major distribution using streams
                Map<String, Long> majorCount = students.stream()
                        .collect(java.util.stream.Collectors.groupingBy(
                                Student::getMajor, 
                                java.util.stream.Collectors.counting()));
                
                System.out.println("\nMajor Distribution:");
                majorCount.entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .forEach(entry -> System.out.println("  " + entry.getKey() + ": " + entry.getValue()));
            }
            
        } catch (Exception e) {
            System.out.println("Error generating statistics: " + e.getMessage());
        }
    }
    
    // Configuration management
    
    private void handleConfiguration() {
        System.out.println("\n=== CONFIGURATION ===");
        System.out.println("1. View Current Configuration");
        System.out.println("2. Modify Configuration");
        System.out.println("3. Reset to Defaults");
        System.out.println("4. Validate Configuration");
        System.out.println("5. Reload Configuration");
        System.out.println("0. Back to Main Menu");
        
        int choice = getIntInput("Enter choice: ");
        
        switch (choice) {
            case 1:
                config.displayConfiguration();
                break;
            case 2:
                modifyConfiguration();
                break;
            case 3:
                if (getConfirmation("Reset all configuration to defaults?")) {
                    config.resetToDefaults();
                    System.out.println("✓ Configuration reset to defaults.");
                }
                break;
            case 4:
                validateConfiguration();
                break;
            case 5:
                config.reloadConfiguration();
                System.out.println("✓ Configuration reloaded.");
                break;
        }
    }
    
    // Utility methods
    
    private void displayWelcomeMessage() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("    WELCOME TO CAMPUS COURSE & RECORDS MANAGER");
        System.out.println("    Version " + config.getAppVersion());
        System.out.println("    " + config.getInstitutionName());
        System.out.println("=".repeat(60));
        System.out.println("Starting system initialization...");
        
        // System startup checks
        System.out.print("Checking configuration... ");
        List<String> configErrors = config.validateConfiguration();
        if (configErrors.isEmpty()) {
            System.out.println("✓");
        } else {
            System.out.println("⚠ " + configErrors.size() + " issues found");
        }
        
        System.out.print("Initializing services... ");
        System.out.println("✓");
        
        System.out.println("System ready!\n");
    }
    
    private void handleHelp() {
        System.out.println("\n=== HELP & INFORMATION ===");
        System.out.println("This is the Campus Course & Records Manager (CCRM) system.");
        System.out.println("Use the numbered menu options to navigate through the system.");
        System.out.println();
        System.out.println("Key Features:");
        System.out.println("• Student Management - Add, update, search students");
        System.out.println("• Course Management - Create and manage courses");
        System.out.println("• Enrollment Management - Handle student enrollments and grades");
        System.out.println("• Transcript Generation - Create official transcripts");
        System.out.println("• Import/Export - Bulk data operations via CSV files");
        System.out.println("• Backup/Restore - Data protection and recovery");
        System.out.println("• Reports - Statistical analysis and summaries");
        System.out.println();
        System.out.println("For technical support, please contact your system administrator.");
        System.out.println();
        System.out.println("Configuration file: config/app.properties");
        System.out.println("Data directory: data/");
        System.out.println("Exports directory: exports/");
        System.out.println("Backups directory: backups/");
    }
    
    private void handleExit() {
        System.out.println("\nShutting down CCRM system...");
        
        // Save any pending changes
        try {
            config.saveConfiguration();
            System.out.println("Configuration saved.");
        } catch (Exception e) {
            System.out.println("Warning: Could not save configuration: " + e.getMessage());
        }
        
        // Cleanup
        System.out.println("Performing cleanup...");
        
        // Ask for backup before exit
        if (getConfirmation("Create backup before exit?")) {
            BackupService.BackupResult result = backupService.createFullBackup();
            if (result.isSuccess()) {
                System.out.println("✓ Backup created successfully.");
            } else {
                System.out.println("⚠ Backup failed: " + result.getMessage());
            }
        }
        
        System.out.println("Thank you for using CCRM!");
        System.out.println("Goodbye!");
        
        running = false;
    }
    
    // Input utility methods
    
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    private boolean getConfirmation(String prompt) {
        String response = getStringInput(prompt + " (y/n): ").toLowerCase();
        return response.equals("y") || response.equals("yes");
    }
    
    private void promptContinue() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private void cleanup() {
        if (scanner != null) {
            scanner.close();
        }
    }
    
    // Placeholder implementations for remaining methods
    private void updateStudent() { System.out.println("Update student feature - To be implemented"); }
    private void viewStudentDetails() { System.out.println("View student details feature - To be implemented"); }
    private void deleteStudent() { System.out.println("Delete student feature - To be implemented"); }
    private void searchCourses() { System.out.println("Search courses feature - To be implemented"); }
    private void updateCourse() { System.out.println("Update course feature - To be implemented"); }
    private void viewCourseDetails() { System.out.println("View course details feature - To be implemented"); }
    private void assignInstructor() { System.out.println("Assign instructor feature - To be implemented"); }
    private void viewCoursesBySemester() { System.out.println("View courses by semester feature - To be implemented"); }
    private void withdrawStudent() { System.out.println("Withdraw student feature - To be implemented"); }
    private void viewStudentEnrollments() { System.out.println("View student enrollments feature - To be implemented"); }
    private void viewCourseEnrollments() { System.out.println("View course enrollments feature - To be implemented"); }
    private void viewEnrollmentStatistics() { System.out.println("View enrollment statistics feature - To be implemented"); }
    private void generateSemesterTranscript() { System.out.println("Generate semester transcript feature - To be implemented"); }
    private void generateDepartmentTranscript() { System.out.println("Generate department transcript feature - To be implemented"); }
    private void generateHonorRollTranscript() { System.out.println("Generate honor roll transcript feature - To be implemented"); }
    private void exportTranscriptToFile() { System.out.println("Export transcript to file feature - To be implemented"); }
    private void viewTranscriptStatistics() { System.out.println("View transcript statistics feature - To be implemented"); }
    private void importStudentsFromCSV() { System.out.println("Import students from CSV feature - To be implemented"); }
    private void importCoursesFromCSV() { System.out.println("Import courses from CSV feature - To be implemented"); }
    private void importEnrollmentsFromCSV() { System.out.println("Import enrollments from CSV feature - To be implemented"); }
    private void exportStudentsToCSV() { System.out.println("Export students to CSV feature - To be implemented"); }
    private void exportCoursesToCSV() { System.out.println("Export courses to CSV feature - To be implemented"); }
    private void exportEnrollmentsToCSV() { System.out.println("Export enrollments to CSV feature - To be implemented"); }
    private void bulkImportAllData() { System.out.println("Bulk import all data feature - To be implemented"); }
    private void bulkExportAllData() { System.out.println("Bulk export all data feature - To be implemented"); }
    private void createIncrementalBackup() { System.out.println("Create incremental backup feature - To be implemented"); }
    private void listAvailableBackups() { System.out.println("List available backups feature - To be implemented"); }
    private void restoreFromBackup() { System.out.println("Restore from backup feature - To be implemented"); }
    private void cleanupOldBackups() { System.out.println("Cleanup old backups feature - To be implemented"); }
    private void showCourseStatistics() { System.out.println("Show course statistics feature - To be implemented"); }
    private void showEnrollmentStatistics() { System.out.println("Show enrollment statistics feature - To be implemented"); }
    private void showGradeDistribution() { System.out.println("Show grade distribution feature - To be implemented"); }
    private void showDepartmentAnalysis() { System.out.println("Show department analysis feature - To be implemented"); }
    private void showTopPerformingStudents() { System.out.println("Show top performing students feature - To be implemented"); }
    private void showSystemStatistics() { System.out.println("Show system statistics feature - To be implemented"); }
    private void modifyConfiguration() { System.out.println("Modify configuration feature - To be implemented"); }
    private void validateConfiguration() { System.out.println("Validate configuration feature - To be implemented"); }
}