package edu.ccrm.service;

import edu.ccrm.domain.Course;
import edu.ccrm.domain.Instructor;
import edu.ccrm.util.Semester;
import edu.ccrm.util.Validators;
import edu.ccrm.util.Comparators;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class for managing courses.
 * Implements business logic, validation, and persistence for Course entities.
 * Demonstrates service layer pattern and Stream API usage.
 */
public class CourseService implements Persistable<Course>, Searchable<Course> {
    
    private static final String COURSES_FILE = "data/courses.csv";
    private List<Course> courses;
    
    // Constructor with initialization
    public CourseService() {
        this.courses = new ArrayList<>();
        try {
            loadFromFile();
        } catch (IOException e) {
            System.err.println("Warning: Could not load courses from file: " + e.getMessage());
        }
    }
    
    // Business logic methods
    
    /**
     * Creates a new course using the Builder pattern.
     * Demonstrates Builder pattern usage and validation.
     */
    public boolean createCourse(String courseId, String title, int credits, String department,
                               String description, String prerequisites, Instructor instructor,
                               Semester semester, int year, int maxEnrollment) {
        try {
            // Validate input parameters
            validateCourseInput(courseId, title, credits, department, semester, year, maxEnrollment);
            
            // Check for duplicate course ID
            if (exists(courseId)) {
                throw new IllegalArgumentException("Course with ID " + courseId + " already exists");
            }
            
            // Create course using Builder pattern
            Course course = new Course.Builder(courseId, title, credits)
                    .department(department)
                    .description(description)
                    .prerequisites(prerequisites)
                    .instructor(instructor)
                    .semester(semester)
                    .year(year)
                    .maxEnrollment(maxEnrollment)
                    .build();
            
            courses.add(course);
            
            // Sort courses to maintain order
            courses.sort(Comparators.BY_COURSE_ID);
            
            // Save to file
            save(course);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error creating course: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Updates course information.
     */
    public boolean updateCourse(String courseId, String title, String description,
                               String prerequisites, int maxEnrollment) {
        try {
            Course course = findById(courseId);
            if (course == null) {
                throw new IllegalArgumentException("Course with ID " + courseId + " not found");
            }
            
            // Note: In a real implementation, Course would need setters or a Builder for updates
            // For this demo, we'll create a new course with updated information
            Course updatedCourse = new Course.Builder(courseId, title, course.getCredits())
                    .department(course.getDepartment())
                    .description(description)
                    .prerequisites(prerequisites)
                    .instructor(course.getInstructor())
                    .semester(course.getSemester())
                    .year(course.getYear())
                    .maxEnrollment(maxEnrollment)
                    .build();
            
            // Replace the old course
            int index = courses.indexOf(course);
            courses.set(index, updatedCourse);
            
            // Save changes
            saveAll(courses);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error updating course: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Assigns an instructor to a course.
     */
    public boolean assignInstructor(String courseId, Instructor instructor) {
        try {
            Course course = findById(courseId);
            if (course == null) {
                throw new IllegalArgumentException("Course with ID " + courseId + " not found");
            }
            
            course.setInstructor(instructor);
            instructor.assignCourse(course);
            
            saveAll(courses);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error assigning instructor: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets courses by semester and year.
     */
    public List<Course> getCoursesBySemester(Semester semester, int year) {
        return courses.stream()
                .filter(c -> c.getSemester() == semester && c.getYear() == year)
                .sorted(Comparators.BY_COURSE_ID)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets courses by department.
     */
    public List<Course> getCoursesByDepartment(String department) {
        return courses.stream()
                .filter(c -> department.equalsIgnoreCase(c.getDepartment()))
                .sorted(Comparators.BY_COURSE_ID)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets available courses (with enrollment capacity).
     */
    public List<Course> getAvailableCourses() {
        return courses.stream()
                .filter(Course::canEnroll)
                .sorted(Comparators.BY_ENROLLMENT)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets courses taught by a specific instructor.
     */
    public List<Course> getCoursesByInstructor(String instructorId) {
        return courses.stream()
                .filter(c -> c.getInstructor() != null && 
                           instructorId.equals(c.getInstructor().getId()))
                .sorted(Comparators.BY_COURSE_ID)
                .collect(Collectors.toList());
    }
    
    /**
     * Validates course input parameters.
     */
    private void validateCourseInput(String courseId, String title, int credits,
                                   String department, Semester semester, int year,
                                   int maxEnrollment) {
        if (!Validators.isValidCourseId(courseId)) {
            throw new IllegalArgumentException("Invalid course ID format. Expected format: CS101");
        }
        if (!Validators.isNotEmpty(title)) {
            throw new IllegalArgumentException("Course title cannot be empty");
        }
        if (!Validators.isValidCredits(credits)) {
            throw new IllegalArgumentException("Credits must be between 1 and 6");
        }
        if (!Validators.isNotEmpty(department)) {
            throw new IllegalArgumentException("Department cannot be empty");
        }
        if (!Validators.isValidSemesterYear(semester, year)) {
            throw new IllegalArgumentException("Invalid semester/year combination");
        }
        if (maxEnrollment <= 0 || maxEnrollment > 500) {
            throw new IllegalArgumentException("Max enrollment must be between 1 and 500");
        }
    }
    
    // Persistable interface implementation
    
    @Override
    public void save(Course course) throws IOException {
        saveAll(courses);
    }
    
    @Override
    public void saveAll(List<Course> courses) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(COURSES_FILE))) {
            // Write CSV header
            writer.println("CourseID,Title,Description,Credits,Department,Prerequisites,InstructorID,Semester,Year,MaxEnrollment,CurrentEnrollment");
            
            // Write course data
            for (Course course : courses) {
                String instructorId = course.getInstructor() != null ? course.getInstructor().getId() : "";
                writer.printf("%s,%s,%s,%d,%s,%s,%s,%s,%d,%d,%d%n",
                        course.getCourseId(),
                        escapeCommas(course.getTitle()),
                        escapeCommas(course.getDescription()),
                        course.getCredits(),
                        course.getDepartment(),
                        escapeCommas(course.getPrerequisites()),
                        instructorId,
                        course.getSemester(),
                        course.getYear(),
                        course.getMaxEnrollment(),
                        course.getCurrentEnrollment());
            }
        }
    }
    
    @Override
    public Course load(String id) throws IOException {
        return findById(id);
    }
    
    @Override
    public List<Course> loadAll() throws IOException {
        return new ArrayList<>(courses);
    }
    
    @Override
    public boolean delete(String id) throws IOException {
        boolean removed = courses.removeIf(c -> c.getCourseId().equals(id));
        if (removed) {
            saveAll(courses);
        }
        return removed;
    }
    
    @Override
    public boolean exists(String id) throws IOException {
        return courses.stream().anyMatch(c -> c.getCourseId().equals(id));
    }
    
    @Override
    public int count() throws IOException {
        return courses.size();
    }
    
    @Override
    public void clear() throws IOException {
        courses.clear();
        saveAll(courses);
    }
    
    // Searchable interface implementation
    
    @Override
    public List<Course> search(Predicate<Course> predicate) {
        return courses.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
    
    @Override
    public Course findFirst(Predicate<Course> predicate) {
        return courses.stream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<Course> searchByText(String searchText) {
        String lowerSearchText = searchText.toLowerCase();
        return courses.stream()
                .filter(c -> c.getCourseId().toLowerCase().contains(lowerSearchText) ||
                           c.getTitle().toLowerCase().contains(lowerSearchText) ||
                           c.getDescription().toLowerCase().contains(lowerSearchText) ||
                           c.getDepartment().toLowerCase().contains(lowerSearchText) ||
                           c.getPrerequisites().toLowerCase().contains(lowerSearchText))
                .collect(Collectors.toList());
    }
    
    @Override
    public long count(Predicate<Course> predicate) {
        return courses.stream()
                .filter(predicate)
                .count();
    }
    
    // Utility methods
    
    public Course findById(String courseId) {
        return courses.stream()
                .filter(c -> c.getCourseId().equals(courseId))
                .findFirst()
                .orElse(null);
    }
    
    public List<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }
    
    /**
     * Loads courses from CSV file.
     */
    private void loadFromFile() throws IOException {
        File file = new File(COURSES_FILE);
        if (!file.exists()) {
            return; // No file to load from
        }
        
        courses.clear();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length >= 9) {
                        Course course = new Course.Builder(
                                parts[0].trim(),                    // Course ID
                                unescapeCommas(parts[1].trim()),    // Title
                                Integer.parseInt(parts[3].trim())   // Credits
                        )
                        .description(unescapeCommas(parts[2].trim()))
                        .department(parts[4].trim())
                        .prerequisites(unescapeCommas(parts[5].trim()))
                        .semester(Semester.valueOf(parts[7].trim()))
                        .year(Integer.parseInt(parts[8].trim()))
                        .maxEnrollment(parts.length > 9 ? Integer.parseInt(parts[9].trim()) : 30)
                        .build();
                        
                        courses.add(course);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing course line: " + line + " - " + e.getMessage());
                }
            }
        }
        
        // Sort courses after loading
        courses.sort(Comparators.BY_COURSE_ID);
    }
    
    /**
     * Helper method to escape commas in CSV fields.
     */
    private String escapeCommas(String text) {
        if (text == null) return "";
        return text.replace(",", ";");
    }
    
    /**
     * Helper method to unescape commas from CSV fields.
     */
    private String unescapeCommas(String text) {
        if (text == null) return "";
        return text.replace(";", ",");
    }
}