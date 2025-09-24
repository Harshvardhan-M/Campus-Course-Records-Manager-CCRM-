package edu.ccrm.service;

import edu.ccrm.domain.Student;
import edu.ccrm.domain.Enrollment;
import edu.ccrm.domain.Course;
import edu.ccrm.util.Validators;
import edu.ccrm.util.Comparators;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class for managing students.
 * Implements business logic, validation, and persistence for Student entities.
 * Demonstrates service layer pattern and comprehensive exception handling.
 */
public class StudentService implements Persistable<Student>, Searchable<Student> {
    
    private static final String STUDENTS_FILE = "data/students.csv";
    private List<Student> students;
    
    // Constructor with initialization
    public StudentService() {
        this.students = new ArrayList<>();
        try {
            loadFromFile();
        } catch (IOException e) {
            System.err.println("Warning: Could not load students from file: " + e.getMessage());
        }
    }
    
    // Business logic methods
    
    /**
     * Adds a new student with validation.
     * Demonstrates comprehensive validation and exception handling.
     */
    public boolean addStudent(String id, String firstName, String lastName, 
                             String email, LocalDate birthDate, String major) {
        try {
            // Validate input parameters
            validateStudentInput(id, firstName, lastName, email, birthDate, major);
            
            // Check for duplicate ID
            if (exists(id)) {
                throw new IllegalArgumentException("Student with ID " + id + " already exists");
            }
            
            // Create and add student
            Student student = new Student(id, firstName, lastName, email, birthDate, major);
            students.add(student);
            
            // Sort students to maintain order
            students.sort(Comparators.BY_ID);
            
            // Save to file
            save(student);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error adding student: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Updates an existing student's information.
     */
    public boolean updateStudent(String id, String firstName, String lastName, 
                                String email, String major) {
        try {
            Student student = findById(id);
            if (student == null) {
                throw new IllegalArgumentException("Student with ID " + id + " not found");
            }
            
            // Validate new information
            if (!Validators.isNotEmpty(firstName)) {
                throw new IllegalArgumentException("First name cannot be empty");
            }
            if (!Validators.isNotEmpty(lastName)) {
                throw new IllegalArgumentException("Last name cannot be empty");
            }
            if (!Validators.isValidEmail(email)) {
                throw new IllegalArgumentException("Invalid email format");
            }
            if (!Validators.isNotEmpty(major)) {
                throw new IllegalArgumentException("Major cannot be empty");
            }
            
            // Update student information
            student.setFirstName(firstName);
            student.setLastName(lastName);
            student.setEmail(email);
            student.setMajor(major);
            
            // Save changes
            saveAll(students);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error updating student: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Calculates and updates GPA for a student based on their enrollments.
     */
    public void updateStudentGPA(String studentId) {
        try {
            Student student = findById(studentId);
            if (student != null) {
                student.calculateGPA();
                student.calculateTotalCredits();
                saveAll(students);
            }
        } catch (IOException e) {
            System.err.println("Error updating student GPA: " + e.getMessage());
        }
    }
    
    /**
     * Gets students by major with optional sorting.
     */
    public List<Student> getStudentsByMajor(String major, boolean sortByGPA) {
        List<Student> result = students.stream()
                .filter(s -> major.equalsIgnoreCase(s.getMajor()))
                .collect(Collectors.toList());
        
        if (sortByGPA) {
            result.sort(Comparators.BY_GPA);
        } else {
            result.sort(Comparators.BY_NAME);
        }
        
        return result;
    }
    
    /**
     * Gets top students by GPA.
     */
    public List<Student> getTopStudents(int count) {
        return students.stream()
                .sorted(Comparators.BY_GPA)
                .limit(count)
                .collect(Collectors.toList());
    }
    
    /**
     * Validates student input parameters.
     */
    private void validateStudentInput(String id, String firstName, String lastName,
                                    String email, LocalDate birthDate, String major) {
        if (!Validators.isValidStudentId(id)) {
            throw new IllegalArgumentException("Invalid student ID format. Expected format: S1234567");
        }
        if (!Validators.isNotEmpty(firstName)) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (!Validators.isNotEmpty(lastName)) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (!Validators.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (!Validators.isValidBirthDate(birthDate)) {
            throw new IllegalArgumentException("Invalid birth date");
        }
        if (!Validators.isNotEmpty(major)) {
            throw new IllegalArgumentException("Major cannot be empty");
        }
    }
    
    // Persistable interface implementation
    
    @Override
    public void save(Student student) throws IOException {
        // Implementation would save single student to file
        saveAll(students);
    }
    
    @Override
    public void saveAll(List<Student> students) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(STUDENTS_FILE))) {
            // Write CSV header
            writer.println("ID,FirstName,LastName,Email,BirthDate,Major,TotalCredits,GPA");
            
            // Write student data
            for (Student student : students) {
                writer.printf("%s,%s,%s,%s,%s,%s,%d,%.2f%n",
                        student.getId(),
                        student.getFirstName(),
                        student.getLastName(),
                        student.getEmail(),
                        student.getBirthDate(),
                        student.getMajor(),
                        student.getTotalCredits(),
                        student.getGpa());
            }
        }
    }
    
    @Override
    public Student load(String id) throws IOException {
        return findById(id);
    }
    
    @Override
    public List<Student> loadAll() throws IOException {
        return new ArrayList<>(students);
    }
    
    @Override
    public boolean delete(String id) throws IOException {
        boolean removed = students.removeIf(s -> s.getId().equals(id));
        if (removed) {
            saveAll(students);
        }
        return removed;
    }
    
    @Override
    public boolean exists(String id) throws IOException {
        return students.stream().anyMatch(s -> s.getId().equals(id));
    }
    
    @Override
    public int count() throws IOException {
        return students.size();
    }
    
    @Override
    public void clear() throws IOException {
        students.clear();
        saveAll(students);
    }
    
    // Searchable interface implementation
    
    @Override
    public List<Student> search(Predicate<Student> predicate) {
        return students.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
    
    @Override
    public Student findFirst(Predicate<Student> predicate) {
        return students.stream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<Student> searchByText(String searchText) {
        String lowerSearchText = searchText.toLowerCase();
        return students.stream()
                .filter(s -> s.getFirstName().toLowerCase().contains(lowerSearchText) ||
                           s.getLastName().toLowerCase().contains(lowerSearchText) ||
                           s.getEmail().toLowerCase().contains(lowerSearchText) ||
                           s.getMajor().toLowerCase().contains(lowerSearchText) ||
                           s.getId().toLowerCase().contains(lowerSearchText))
                .collect(Collectors.toList());
    }
    
    @Override
    public long count(Predicate<Student> predicate) {
        return students.stream()
                .filter(predicate)
                .count();
    }
    
    // Utility methods
    
    public Student findById(String id) {
        return students.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }
    
    /**
     * Loads students from CSV file using NIO.2.
     */
    private void loadFromFile() throws IOException {
        File file = new File(STUDENTS_FILE);
        if (!file.exists()) {
            return; // No file to load from
        }
        
        students.clear();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length >= 6) {
                        Student student = new Student(
                                parts[0].trim(),                    // ID
                                parts[1].trim(),                    // First Name
                                parts[2].trim(),                    // Last Name
                                parts[3].trim(),                    // Email
                                LocalDate.parse(parts[4].trim()),   // Birth Date
                                parts[5].trim()                     // Major
                        );
                        
                        // Set additional fields if available
                        if (parts.length > 6) {
                            student.setTotalCredits(Integer.parseInt(parts[6].trim()));
                        }
                        if (parts.length > 7) {
                            student.setGpa(Double.parseDouble(parts[7].trim()));
                        }
                        
                        students.add(student);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing student line: " + line + " - " + e.getMessage());
                }
            }
        }
        
        // Sort students after loading
        students.sort(Comparators.BY_ID);
    }
    
    /**
     * Gets statistical information about students.
     */
    public Student.StudentStatistics getStatistics() {
        if (students.isEmpty()) {
            return new Student.StudentStatistics(0, 0.0, 0);
        }
        
        int totalStudents = students.size();
        double averageGPA = students.stream()
                .mapToDouble(Student::getGpa)
                .average()
                .orElse(0.0);
        int totalCredits = students.stream()
                .mapToInt(Student::getTotalCredits)
                .sum();
        
        return new Student.StudentStatistics(totalStudents, averageGPA, totalCredits);
    }
}