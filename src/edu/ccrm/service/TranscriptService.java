package edu.ccrm.service;

import edu.ccrm.domain.*;
import edu.ccrm.util.Grade;
import edu.ccrm.util.Semester;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class for generating and managing transcripts.
 * Demonstrates Builder pattern usage and advanced Stream API operations.
 */
public class TranscriptService implements Persistable<Transcript>, Searchable<Transcript> {
    
    private static final String TRANSCRIPTS_FILE = "data/transcripts.csv";
    private List<Transcript> generatedTranscripts;
    private StudentService studentService;
    
    // Constructor with dependency injection
    public TranscriptService(StudentService studentService) {
        this.generatedTranscripts = new ArrayList<>();
        this.studentService = studentService;
        try {
            loadFromFile();
        } catch (IOException e) {
            System.err.println("Warning: Could not load transcripts from file: " + e.getMessage());
        }
    }
    
    // Business logic methods demonstrating Builder pattern and functional programming
    
    /**
     * Generates a complete transcript for a student.
     * Demonstrates Builder pattern and method chaining.
     */
    public Transcript generateCompleteTranscript(String studentId) {
        return generateCompleteTranscript(studentId, "");
    }
    
    public Transcript generateCompleteTranscript(String studentId, String notes) {
        Student student = studentService.findById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student with ID " + studentId + " not found");
        }
        
        Transcript transcript = new Transcript.Builder(student)
                .addNotes(notes)
                .build();
        
        // Store generated transcript
        generatedTranscripts.add(transcript);
        
        try {
            save(transcript);
        } catch (IOException e) {
            System.err.println("Warning: Could not save transcript: " + e.getMessage());
        }
        
        return transcript;
    }
    
    /**
     * Generates a semester-specific transcript.
     * Demonstrates Builder pattern with filtering.
     */
    public Transcript generateSemesterTranscript(String studentId, Semester semester, int year) {
        Student student = studentService.findById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student with ID " + studentId + " not found");
        }
        
        Transcript transcript = new Transcript.Builder(student)
                .filterBySemester(semester, year)
                .addNotes(String.format("Transcript for %s %d semester", semester, year))
                .build();
        
        generatedTranscripts.add(transcript);
        
        try {
            save(transcript);
        } catch (IOException e) {
            System.err.println("Warning: Could not save transcript: " + e.getMessage());
        }
        
        return transcript;
    }
    
    /**
     * Generates a transcript showing only courses with minimum grade.
     * Demonstrates Builder pattern with grade filtering.
     */
    public Transcript generateFilteredTranscript(String studentId, Grade minGrade) {
        Student student = studentService.findById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student with ID " + studentId + " not found");
        }
        
        Transcript transcript = new Transcript.Builder(student)
                .filterByGrade(minGrade)
                .addNotes(String.format("Showing courses with grade %s or better", minGrade))
                .build();
        
        generatedTranscripts.add(transcript);
        
        try {
            save(transcript);
        } catch (IOException e) {
            System.err.println("Warning: Could not save transcript: " + e.getMessage());
        }
        
        return transcript;
    }
    
    /**
     * Generates a department-specific transcript.
     */
    public Transcript generateDepartmentTranscript(String studentId, String department) {
        Student student = studentService.findById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student with ID " + studentId + " not found");
        }
        
        Transcript transcript = new Transcript.Builder(student)
                .filterByDepartment(department)
                .addNotes(String.format("Showing courses from %s department", department))
                .build();
        
        generatedTranscripts.add(transcript);
        
        try {
            save(transcript);
        } catch (IOException e) {
            System.err.println("Warning: Could not save transcript: " + e.getMessage());
        }
        
        return transcript;
    }
    
    /**
     * Generates a formatted transcript report with detailed formatting.
     */
    public String generateFormattedTranscript(String studentId, String reportType) {
        Student student = studentService.findById(studentId);
        if (student == null) {
            return "Student not found";
        }
        
        Transcript transcript;
        
        switch (reportType.toUpperCase()) {
            case "COMPLETE":
                transcript = generateCompleteTranscript(studentId, "Complete Academic Record");
                break;
            case "CURRENT":
                // Get current semester/year (simplified for demo)
                int currentYear = LocalDateTime.now().getYear();
                Semester currentSemester = getCurrentSemester();
                transcript = generateSemesterTranscript(studentId, currentSemester, currentYear);
                break;
            case "HONORS":
                transcript = generateFilteredTranscript(studentId, Grade.B);
                break;
            default:
                transcript = generateCompleteTranscript(studentId);
                break;
        }
        
        return transcript.generateTranscriptReport();
    }
    
    /**
     * Exports transcript to file with custom formatting.
     */
    public boolean exportTranscriptToFile(String studentId, String filename, String format) {
        try {
            Student student = studentService.findById(studentId);
            if (student == null) {
                throw new IllegalArgumentException("Student not found");
            }
            
            Transcript transcript = generateCompleteTranscript(studentId);
            
            switch (format.toUpperCase()) {
                case "TXT":
                    exportToText(transcript, filename);
                    break;
                case "HTML":
                    exportToHTML(transcript, filename);
                    break;
                case "CSV":
                    exportToCSV(transcript, filename);
                    break;
                default:
                    exportToText(transcript, filename);
                    break;
            }
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error exporting transcript: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets transcript statistics for analysis.
     */
    public Map<String, Object> getTranscriptStatistics(String studentId) {
        Student student = studentService.findById(studentId);
        if (student == null) {
            return Collections.emptyMap();
        }
        
        List<Enrollment> enrollments = student.getEnrollments();
        Map<String, Object> stats = new HashMap<>();
        
        // Basic statistics
        stats.put("totalCourses", enrollments.size());
        stats.put("completedCourses", enrollments.stream()
                .filter(Enrollment::isCompleted)
                .count());
        
        // Grade distribution
        Map<Grade, Long> gradeDistribution = enrollments.stream()
                .filter(e -> e.getGrade() != null)
                .collect(Collectors.groupingBy(Enrollment::getGrade, Collectors.counting()));
        stats.put("gradeDistribution", gradeDistribution);
        
        // Department breakdown
        Map<String, Long> departmentCounts = enrollments.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCourse().getDepartment(),
                        Collectors.counting()));
        stats.put("departmentBreakdown", departmentCounts);
        
        // Semester progression
        Map<String, Long> semesterCounts = enrollments.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCourse().getSemester() + " " + e.getCourse().getYear(),
                        Collectors.counting()));
        stats.put("semesterProgression", semesterCounts);
        
        return stats;
    }
    
    // Helper methods for different export formats
    
    private void exportToText(Transcript transcript, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter("exports/" + filename + ".txt"))) {
            writer.println(transcript.generateTranscriptReport());
        }
    }
    
    private void exportToHTML(Transcript transcript, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter("exports/" + filename + ".html"))) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html><head><title>Official Transcript</title>");
            writer.println("<style>body{font-family:Arial,sans-serif;margin:20px;}</style>");
            writer.println("</head><body>");
            writer.println("<h1>OFFICIAL TRANSCRIPT</h1>");
            
            Student student = transcript.getStudent();
            writer.printf("<h2>%s (ID: %s)</h2>%n", student.getFullName(), student.getId());
            writer.printf("<p><strong>Major:</strong> %s</p>%n", student.getMajor());
            writer.printf("<p><strong>Generated:</strong> %s</p>%n", 
                    transcript.getGeneratedDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            writer.println("<table border='1' style='border-collapse:collapse;width:100%;'>");
            writer.println("<tr><th>Course ID</th><th>Title</th><th>Credits</th><th>Grade</th></tr>");
            
            transcript.getEnrollments().forEach(enrollment -> {
                Course course = enrollment.getCourse();
                Grade grade = enrollment.getGrade();
                writer.printf("<tr><td>%s</td><td>%s</td><td>%d</td><td>%s</td></tr>%n",
                        course.getCourseId(), course.getTitle(), course.getCredits(),
                        grade != null ? grade.toString() : "IP");
            });
            
            writer.println("</table>");
            writer.printf("<p><strong>Cumulative GPA:</strong> %.2f</p>%n", transcript.getCumulativeGPA());
            writer.printf("<p><strong>Credits Earned:</strong> %d</p>%n", transcript.getTotalCreditsEarned());
            writer.println("</body></html>");
        }
    }
    
    private void exportToCSV(Transcript transcript, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter("exports/" + filename + ".csv"))) {
            writer.println("CourseID,Title,Credits,Grade,Semester,Year");
            
            transcript.getEnrollments().forEach(enrollment -> {
                Course course = enrollment.getCourse();
                Grade grade = enrollment.getGrade();
                writer.printf("%s,%s,%d,%s,%s,%d%n",
                        course.getCourseId(),
                        course.getTitle().replace(",", ";"),
                        course.getCredits(),
                        grade != null ? grade.toString() : "",
                        course.getSemester(),
                        course.getYear());
            });
        }
    }
    
    private Semester getCurrentSemester() {
        int month = LocalDateTime.now().getMonthValue();
        if (month >= 1 && month <= 5) return Semester.SPRING;
        if (month >= 6 && month <= 8) return Semester.SUMMER;
        return Semester.FALL;
    }
    
    // Persistable interface implementation
    
    @Override
    public void save(Transcript transcript) throws IOException {
        saveAll(generatedTranscripts);
    }
    
    @Override
    public void saveAll(List<Transcript> transcripts) throws IOException {
        // For demo purposes, we'll save transcript metadata to CSV
        try (PrintWriter writer = new PrintWriter(new FileWriter(TRANSCRIPTS_FILE))) {
            writer.println("StudentID,StudentName,GeneratedDate,EnrollmentCount,CumulativeGPA,TotalCredits,Notes");
            
            for (Transcript transcript : transcripts) {
                Student student = transcript.getStudent();
                writer.printf("%s,%s,%s,%d,%.2f,%d,%s%n",
                        student.getId(),
                        student.getFullName().replace(",", ";"),
                        transcript.getGeneratedDate(),
                        transcript.getEnrollments().size(),
                        transcript.getCumulativeGPA(),
                        transcript.getTotalCreditsEarned(),
                        transcript.getNotes().replace(",", ";"));
            }
        }
    }
    
    @Override
    public Transcript load(String id) throws IOException {
        // Implementation would reconstruct transcript from student ID
        return null;
    }
    
    @Override
    public List<Transcript> loadAll() throws IOException {
        return new ArrayList<>(generatedTranscripts);
    }
    
    @Override
    public boolean delete(String id) throws IOException {
        // Implementation for deleting transcript records
        return false;
    }
    
    @Override
    public boolean exists(String id) throws IOException {
        return false;
    }
    
    @Override
    public int count() throws IOException {
        return generatedTranscripts.size();
    }
    
    @Override
    public void clear() throws IOException {
        generatedTranscripts.clear();
        saveAll(generatedTranscripts);
    }
    
    // Searchable interface implementation
    
    @Override
    public List<Transcript> search(Predicate<Transcript> predicate) {
        return generatedTranscripts.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
    
    @Override
    public Transcript findFirst(Predicate<Transcript> predicate) {
        return generatedTranscripts.stream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<Transcript> searchByText(String searchText) {
        String lowerSearchText = searchText.toLowerCase();
        return generatedTranscripts.stream()
                .filter(t -> t.getStudent().getFullName().toLowerCase().contains(lowerSearchText) ||
                           t.getNotes().toLowerCase().contains(lowerSearchText))
                .collect(Collectors.toList());
    }
    
    @Override
    public long count(Predicate<Transcript> predicate) {
        return generatedTranscripts.stream()
                .filter(predicate)
                .count();
    }
    
    // Simple file loading (metadata only)
    private void loadFromFile() throws IOException {
        File file = new File(TRANSCRIPTS_FILE);
        if (!file.exists()) {
            return;
        }
        
        // For demo, we'll just track that transcripts were generated
        // In a real system, we'd reconstruct transcripts or store references
        System.out.println("Transcript metadata loaded from file");
    }
}