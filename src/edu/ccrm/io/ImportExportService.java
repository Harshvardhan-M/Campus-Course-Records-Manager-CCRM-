package edu.ccrm.io;

import edu.ccrm.domain.*;
import edu.ccrm.service.*;
import edu.ccrm.util.Grade;
import edu.ccrm.util.Semester;
import edu.ccrm.util.Validators;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Service for importing and exporting data in various formats.
 * Demonstrates NIO.2, exception handling, and file processing.
 */
public class ImportExportService {
    
    private StudentService studentService;
    private CourseService courseService;
    private EnrollmentService enrollmentService;
    
    // Statistics tracking
    private int successCount = 0;
    private int errorCount = 0;
    private List<String> errorMessages = new ArrayList<>();
    
    public ImportExportService(StudentService studentService, CourseService courseService, 
                              EnrollmentService enrollmentService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
    }
    
    // Import methods demonstrating NIO.2 and comprehensive error handling
    
    /**
     * Imports students from CSV file using NIO.2.
     * Demonstrates Path API, exception handling, and data validation.
     */
    public ImportResult importStudentsFromCSV(String filename) {
        resetCounters();
        Path filePath = Paths.get("imports", filename);
        
        if (!Files.exists(filePath)) {
            return new ImportResult(false, "File not found: " + filename, 0, 0, Collections.emptyList());
        }
        
        try {
            List<String> lines = Files.readAllLines(filePath);
            if (lines.isEmpty()) {
                return new ImportResult(false, "File is empty", 0, 0, Collections.emptyList());
            }
            
            // Skip header line
            for (int i = 1; i < lines.size(); i++) {
                try {
                    processStudentLine(lines.get(i), i + 1);
                } catch (Exception e) {
                    handleError("Line " + (i + 1), e.getMessage());
                }
            }
            
            return new ImportResult(true, 
                    String.format("Import completed: %d successful, %d errors", successCount, errorCount),
                    successCount, errorCount, new ArrayList<>(errorMessages));
            
        } catch (IOException e) {
            return new ImportResult(false, "IO Error: " + e.getMessage(), 0, 0, Collections.emptyList());
        }
    }
    
    /**
     * Imports courses from CSV with instructor assignment.
     */
    public ImportResult importCoursesFromCSV(String filename) {
        resetCounters();
        Path filePath = Paths.get("imports", filename);
        
        try {
            if (!Files.exists(filePath)) {
                throw new FileNotFoundException("File not found: " + filename);
            }
            
            List<String> lines = Files.readAllLines(filePath);
            
            // Process each line
            for (int i = 1; i < lines.size(); i++) {
                try {
                    processCourseLine(lines.get(i), i + 1);
                } catch (Exception e) {
                    handleError("Line " + (i + 1), e.getMessage());
                }
            }
            
            return new ImportResult(true,
                    String.format("Courses imported: %d successful, %d errors", successCount, errorCount),
                    successCount, errorCount, new ArrayList<>(errorMessages));
            
        } catch (Exception e) {
            return new ImportResult(false, "Import failed: " + e.getMessage(), 0, 0, errorMessages);
        }
    }
    
    /**
     * Imports enrollments with grade assignments.
     */
    public ImportResult importEnrollmentsFromCSV(String filename) {
        resetCounters();
        Path filePath = Paths.get("imports", filename);
        
        try {
            List<String> lines = Files.readAllLines(filePath);
            
            for (int i = 1; i < lines.size(); i++) {
                try {
                    processEnrollmentLine(lines.get(i), i + 1);
                } catch (Exception e) {
                    handleError("Line " + (i + 1), e.getMessage());
                }
            }
            
            return new ImportResult(true,
                    String.format("Enrollments imported: %d successful, %d errors", successCount, errorCount),
                    successCount, errorCount, new ArrayList<>(errorMessages));
            
        } catch (Exception e) {
            return new ImportResult(false, "Import failed: " + e.getMessage(), 0, 0, errorMessages);
        }
    }
    
    // Export methods using NIO.2
    
    /**
     * Exports all students to CSV format.
     */
    public ExportResult exportStudentsToCSV(String filename) {
        Path exportPath = Paths.get("exports", filename);
        
        try {
            // Ensure exports directory exists
            Files.createDirectories(exportPath.getParent());
            
            List<Student> students = studentService.getAllStudents();
            
            try (BufferedWriter writer = Files.newBufferedWriter(exportPath)) {
                // Write header
                writer.write("ID,FirstName,LastName,Email,BirthDate,Major,TotalCredits,GPA");
                writer.newLine();
                
                // Write student data
                for (Student student : students) {
                    writer.write(String.format("%s,%s,%s,%s,%s,%s,%d,%.2f",
                            student.getId(),
                            student.getFirstName(),
                            student.getLastName(),
                            student.getEmail(),
                            student.getBirthDate(),
                            student.getMajor(),
                            student.getTotalCredits(),
                            student.getGpa()));
                    writer.newLine();
                }
            }
            
            return new ExportResult(true, 
                    String.format("Exported %d students to %s", students.size(), filename),
                    students.size());
            
        } catch (IOException e) {
            return new ExportResult(false, "Export failed: " + e.getMessage(), 0);
        }
    }
    
    /**
     * Exports courses with enrollment statistics.
     */
    public ExportResult exportCoursesToCSV(String filename) {
        Path exportPath = Paths.get("exports", filename);
        
        try {
            Files.createDirectories(exportPath.getParent());
            List<Course> courses = courseService.getAllCourses();
            
            try (BufferedWriter writer = Files.newBufferedWriter(exportPath)) {
                writer.write("CourseID,Title,Description,Credits,Department,Prerequisites,Semester,Year,MaxEnrollment,CurrentEnrollment");
                writer.newLine();
                
                for (Course course : courses) {
                    writer.write(String.format("%s,%s,%s,%d,%s,%s,%s,%d,%d,%d",
                            course.getCourseId(),
                            escapeCSV(course.getTitle()),
                            escapeCSV(course.getDescription()),
                            course.getCredits(),
                            course.getDepartment(),
                            escapeCSV(course.getPrerequisites()),
                            course.getSemester(),
                            course.getYear(),
                            course.getMaxEnrollment(),
                            course.getCurrentEnrollment()));
                    writer.newLine();
                }
            }
            
            return new ExportResult(true,
                    String.format("Exported %d courses to %s", courses.size(), filename),
                    courses.size());
            
        } catch (IOException e) {
            return new ExportResult(false, "Export failed: " + e.getMessage(), 0);
        }
    }
    
    /**
     * Exports enrollment data with grades.
     */
    public ExportResult exportEnrollmentsToCSV(String filename) {
        Path exportPath = Paths.get("exports", filename);
        
        try {
            Files.createDirectories(exportPath.getParent());
            List<Enrollment> enrollments = enrollmentService.loadAll();
            
            try (BufferedWriter writer = Files.newBufferedWriter(exportPath)) {
                writer.write("EnrollmentID,StudentID,StudentName,CourseID,CourseName,EnrollmentDate,Grade,Status");
                writer.newLine();
                
                for (Enrollment enrollment : enrollments) {
                    writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                            enrollment.getEnrollmentId(),
                            enrollment.getStudent().getId(),
                            escapeCSV(enrollment.getStudent().getFullName()),
                            enrollment.getCourse().getCourseId(),
                            escapeCSV(enrollment.getCourse().getTitle()),
                            enrollment.getEnrollmentDate(),
                            enrollment.getGrade() != null ? enrollment.getGrade().toString() : "",
                            enrollment.getStatus()));
                    writer.newLine();
                }
            }
            
            return new ExportResult(true,
                    String.format("Exported %d enrollments to %s", enrollments.size(), filename),
                    enrollments.size());
            
        } catch (IOException e) {
            return new ExportResult(false, "Export failed: " + e.getMessage(), 0);
        }
    }
    
    // Bulk operations
    
    /**
     * Performs bulk import of all data files in a directory.
     */
    public Map<String, ImportResult> bulkImport(String directoryPath) {
        Map<String, ImportResult> results = new HashMap<>();
        Path importDir = Paths.get(directoryPath);
        
        try {
            if (!Files.exists(importDir)) {
                Files.createDirectories(importDir);
                results.put("ERROR", new ImportResult(false, "Import directory created: " + directoryPath, 0, 0, Collections.emptyList()));
                return results;
            }
            
            // Import students first
            Path studentsFile = importDir.resolve("students.csv");
            if (Files.exists(studentsFile)) {
                results.put("students", importStudentsFromCSV("students.csv"));
            }
            
            // Then courses
            Path coursesFile = importDir.resolve("courses.csv");
            if (Files.exists(coursesFile)) {
                results.put("courses", importCoursesFromCSV("courses.csv"));
            }
            
            // Finally enrollments
            Path enrollmentsFile = importDir.resolve("enrollments.csv");
            if (Files.exists(enrollmentsFile)) {
                results.put("enrollments", importEnrollmentsFromCSV("enrollments.csv"));
            }
            
        } catch (IOException e) {
            results.put("ERROR", new ImportResult(false, "Bulk import failed: " + e.getMessage(), 0, 0, Collections.emptyList()));
        }
        
        return results;
    }
    
    /**
     * Exports all data to separate CSV files.
     */
    public Map<String, ExportResult> bulkExport(String directoryPath) {
        Map<String, ExportResult> results = new HashMap<>();
        
        try {
            Path exportDir = Paths.get(directoryPath);
            Files.createDirectories(exportDir);
            
            results.put("students", exportStudentsToCSV("students_export.csv"));
            results.put("courses", exportCoursesToCSV("courses_export.csv"));
            results.put("enrollments", exportEnrollmentsToCSV("enrollments_export.csv"));
            
        } catch (IOException e) {
            results.put("ERROR", new ExportResult(false, "Bulk export failed: " + e.getMessage(), 0));
        }
        
        return results;
    }
    
    // Private helper methods
    
    private void processStudentLine(String line, int lineNumber) throws Exception {
        String[] parts = line.split(",");
        if (parts.length < 6) {
            throw new IllegalArgumentException("Insufficient data fields");
        }
        
        String id = parts[0].trim();
        String firstName = parts[1].trim();
        String lastName = parts[2].trim();
        String email = parts[3].trim();
        String birthDateStr = parts[4].trim();
        String major = parts[5].trim();
        
        // Validate and parse birth date
        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid birth date format: " + birthDateStr);
        }
        
        // Add student
        if (studentService.addStudent(id, firstName, lastName, email, birthDate, major)) {
            successCount++;
        } else {
            throw new Exception("Failed to add student");
        }
    }
    
    private void processCourseLine(String line, int lineNumber) throws Exception {
        String[] parts = line.split(",");
        if (parts.length < 8) {
            throw new IllegalArgumentException("Insufficient data fields");
        }
        
        String courseId = parts[0].trim();
        String title = parts[1].trim();
        String description = parts[2].trim();
        int credits = Integer.parseInt(parts[3].trim());
        String department = parts[4].trim();
        String prerequisites = parts[5].trim();
        Semester semester = Semester.valueOf(parts[6].trim().toUpperCase());
        int year = Integer.parseInt(parts[7].trim());
        int maxEnrollment = parts.length > 8 ? Integer.parseInt(parts[8].trim()) : 30;
        
        if (courseService.createCourse(courseId, title, credits, department, description,
                prerequisites, null, semester, year, maxEnrollment)) {
            successCount++;
        } else {
            throw new Exception("Failed to create course");
        }
    }
    
    private void processEnrollmentLine(String line, int lineNumber) throws Exception {
        String[] parts = line.split(",");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Insufficient data fields");
        }
        
        String studentId = parts[0].trim();
        String courseId = parts[1].trim();
        String gradeStr = parts.length > 2 ? parts[2].trim() : "";
        
        try {
            String enrollmentId = enrollmentService.enrollStudent(studentId, courseId);
            
            // Assign grade if provided
            if (!gradeStr.isEmpty()) {
                Grade grade = Grade.fromString(gradeStr);
                enrollmentService.assignGrade(enrollmentId, grade);
            }
            
            successCount++;
            
        } catch (Exception e) {
            throw new Exception("Enrollment failed: " + e.getMessage());
        }
    }
    
    private void resetCounters() {
        successCount = 0;
        errorCount = 0;
        errorMessages.clear();
    }
    
    private void handleError(String context, String message) {
        errorCount++;
        errorMessages.add(context + ": " + message);
    }
    
    private String escapeCSV(String value) {
        if (value == null) return "";
        return value.replace(",", ";").replace("\n", " ").replace("\r", " ");
    }
    
    // Result classes for operation outcomes
    
    public static class ImportResult {
        private final boolean success;
        private final String message;
        private final int successCount;
        private final int errorCount;
        private final List<String> errors;
        
        public ImportResult(boolean success, String message, int successCount, int errorCount, List<String> errors) {
            this.success = success;
            this.message = message;
            this.successCount = successCount;
            this.errorCount = errorCount;
            this.errors = errors;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getSuccessCount() { return successCount; }
        public int getErrorCount() { return errorCount; }
        public List<String> getErrors() { return errors; }
        
        @Override
        public String toString() {
            return String.format("ImportResult{success=%s, message='%s', success=%d, errors=%d}",
                    success, message, successCount, errorCount);
        }
    }
    
    public static class ExportResult {
        private final boolean success;
        private final String message;
        private final int recordCount;
        
        public ExportResult(boolean success, String message, int recordCount) {
            this.success = success;
            this.message = message;
            this.recordCount = recordCount;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getRecordCount() { return recordCount; }
        
        @Override
        public String toString() {
            return String.format("ExportResult{success=%s, message='%s', records=%d}",
                    success, message, recordCount);
        }
    }
}