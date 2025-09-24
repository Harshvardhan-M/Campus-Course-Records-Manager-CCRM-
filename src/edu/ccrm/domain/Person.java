package edu.ccrm.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Abstract base class representing a person in the campus system.
 * Demonstrates abstract class implementation and polymorphism.
 */
public abstract class Person {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    
    // Protected constructor for subclasses
    protected Person(String id, String firstName, String lastName, String email, LocalDate birthDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
    }
    
    // Abstract method to be implemented by subclasses
    public abstract String getRole();
    
    // Abstract method for polymorphic behavior
    public abstract String getDisplayInfo();
    
    // Getters and setters with proper access modifiers
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    
    // Method overloading example
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getFullName(boolean lastFirst) {
        if (lastFirst) {
            return lastName + ", " + firstName;
        }
        return getFullName();
    }
    
    // Polymorphic toString method
    @Override
    public String toString() {
        return String.format("ID: %s, Name: %s, Email: %s, Birth Date: %s, Role: %s",
                id, getFullName(), email, 
                birthDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                getRole());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return id.equals(person.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}