package edu.ccrm.service;

import java.util.List;
import java.util.function.Predicate;

/**
 * Interface for objects that support search operations.
 * Demonstrates functional interface usage and generic types.
 * 
 * @param <T> the type of object to search
 */
public interface Searchable<T> {
    
    /**
     * Searches for objects matching a predicate.
     * 
     * @param predicate the search criteria
     * @return list of matching objects
     */
    List<T> search(Predicate<T> predicate);
    
    /**
     * Finds the first object matching a predicate.
     * 
     * @param predicate the search criteria
     * @return the first matching object, or null if none found
     */
    T findFirst(Predicate<T> predicate);
    
    /**
     * Searches by text in relevant fields.
     * 
     * @param searchText the text to search for
     * @return list of matching objects
     */
    List<T> searchByText(String searchText);
    
    /**
     * Counts objects matching a predicate.
     * 
     * @param predicate the search criteria
     * @return count of matching objects
     */
    long count(Predicate<T> predicate);
}