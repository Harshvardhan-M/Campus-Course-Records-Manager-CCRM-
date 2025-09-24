package edu.ccrm.util;

import edu.ccrm.domain.*;
import java.util.Comparator;

/**
 * Utility class containing various comparators for domain objects.
 * Demonstrates functional interfaces, lambda expressions, and method references.
 */
public class Comparators {
    
    // Private constructor to prevent instantiation
    private Comparators() {
        throw new AssertionError("Utility class should not be instantiated");
    }
    
    // Student Comparators
    public static final Comparator<Student> BY_NAME = 
            Comparator.comparing(Person::getLastName)
                     .thenComparing(Person::getFirstName);
    
    public static final Comparator<Student> BY_GPA = 
            Comparator.comparing(Student::getGpa).reversed(); // Highest GPA first
    
    public static final Comparator<Student> BY_CREDITS = 
            Comparator.comparing(Student::getTotalCredits).reversed(); // Most credits first
    
    public static final Comparator<Student> BY_ID = 
            Comparator.comparing(Person::getId);
    
    public static final Comparator<Student> BY_MAJOR = 
            Comparator.comparing(Student::getMajor)
                     .thenComparing(BY_NAME);
    
    // Course Comparators
    public static final Comparator<Course> BY_COURSE_ID = 
            Comparator.comparing(Course::getCourseId);
    
    public static final Comparator<Course> BY_TITLE = 
            Comparator.comparing(Course::getTitle);
    
    public static final Comparator<Course> BY_DEPARTMENT = 
            Comparator.comparing(Course::getDepartment)
                     .thenComparing(Course::getCourseId);
    
    public static final Comparator<Course> BY_CREDITS = 
            Comparator.comparing(Course::getCredits).reversed(); // Most credits first
    
    public static final Comparator<Course> BY_ENROLLMENT = 
            Comparator.comparing(Course::getCurrentEnrollment).reversed(); // Most enrolled first
    
    public static final Comparator<Course> BY_SEMESTER_YEAR = 
            Comparator.comparing(Course::getYear)
                     .thenComparing(course -> course.getSemester().getOrder())
                     .thenComparing(Course::getCourseId);
    
    // Instructor Comparators
    public static final Comparator<Instructor> INSTRUCTOR_BY_NAME = 
            Comparator.comparing(Person::getLastName)
                     .thenComparing(Person::getFirstName);
    
    public static final Comparator<Instructor> BY_DEPARTMENT = 
            Comparator.comparing(Instructor::getDepartment)
                     .thenComparing(INSTRUCTOR_BY_NAME);
    
    public static final Comparator<Instructor> BY_SALARY = 
            Comparator.comparing(Instructor::getSalary).reversed(); // Highest salary first
    
    public static final Comparator<Instructor> BY_COURSES_COUNT = 
            Comparator.comparing((Instructor i) -> i.getCoursesTeaching().size()).reversed();
    
    // Enrollment Comparators
    public static final Comparator<Enrollment> BY_ENROLLMENT_DATE = 
            Comparator.comparing(Enrollment::getEnrollmentDate).reversed(); // Most recent first
    
    public static final Comparator<Enrollment> BY_STUDENT_NAME = 
            Comparator.comparing((Enrollment e) -> e.getStudent().getLastName())
                     .thenComparing(e -> e.getStudent().getFirstName());
    
    public static final Comparator<Enrollment> BY_COURSE_ID = 
            Comparator.comparing((Enrollment e) -> e.getCourse().getCourseId());
    
    public static final Comparator<Enrollment> BY_GRADE = 
            Comparator.comparing((Enrollment e) -> e.getGrade() != null ? e.getGrade().getGradePoints() : -1.0)
                     .reversed(); // Best grades first, nulls last
    
    // Custom comparator factory methods using lambda expressions
    public static Comparator<Student> byGpaRange(double minGpa, double maxGpa) {
        return (s1, s2) -> {
            boolean s1InRange = s1.getGpa() >= minGpa && s1.getGpa() <= maxGpa;
            boolean s2InRange = s2.getGpa() >= minGpa && s2.getGpa() <= maxGpa;
            
            if (s1InRange && !s2InRange) return -1;
            if (!s1InRange && s2InRange) return 1;
            return Double.compare(s2.getGpa(), s1.getGpa()); // Higher GPA first
        };
    }
    
    public static Comparator<Course> bySemester(Semester targetSemester) {
        return (c1, c2) -> {
            boolean c1Match = c1.getSemester() == targetSemester;
            boolean c2Match = c2.getSemester() == targetSemester;
            
            if (c1Match && !c2Match) return -1;
            if (!c1Match && c2Match) return 1;
            return c1.getCourseId().compareTo(c2.getCourseId());
        };
    }
    
    // Method reference examples
    public static final Comparator<Student> BY_EMAIL = 
            Comparator.comparing(Person::getEmail);
    
    public static final Comparator<Course> BY_MAX_ENROLLMENT = 
            Comparator.comparing(Course::getMaxEnrollment).reversed();
    
    // Chaining multiple comparators example
    public static final Comparator<Student> COMPREHENSIVE_STUDENT_SORT = 
            BY_MAJOR
            .thenComparing(BY_GPA)
            .thenComparing(BY_CREDITS)
            .thenComparing(BY_NAME);
}