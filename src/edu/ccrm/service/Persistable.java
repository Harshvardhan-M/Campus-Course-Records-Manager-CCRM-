package edu.ccrm.service;

import java.io.IOException;
import java.util.List;

/**
 * Interface for objects that can be persisted to and from storage.
 * Demonstrates interface definition with generic types.
 * 
 * @param <T> the type of object to persist
 */
public interface Persistable<T> {
    
    /**
     * Saves an object to persistent storage.
     * 
     * @param object the object to save
     * @throws IOException if an I/O error occurs
     */
    void save(T object) throws IOException;
    
    /**
     * Saves a list of objects to persistent storage.
     * 
     * @param objects the list of objects to save
     * @throws IOException if an I/O error occurs
     */
    void saveAll(List<T> objects) throws IOException;
    
    /**
     * Loads an object by its identifier.
     * 
     * @param id the identifier of the object to load
     * @return the loaded object, or null if not found
     * @throws IOException if an I/O error occurs
     */
    T load(String id) throws IOException;
    
    /**
     * Loads all objects from persistent storage.
     * 
     * @return list of all objects
     * @throws IOException if an I/O error occurs
     */
    List<T> loadAll() throws IOException;
    
    /**
     * Deletes an object from persistent storage.
     * 
     * @param id the identifier of the object to delete
     * @return true if deleted successfully, false if not found
     * @throws IOException if an I/O error occurs
     */
    boolean delete(String id) throws IOException;
    
    /**
     * Checks if an object exists in persistent storage.
     * 
     * @param id the identifier to check
     * @return true if the object exists, false otherwise
     * @throws IOException if an I/O error occurs
     */
    boolean exists(String id) throws IOException;
    
    /**
     * Gets the total count of objects in storage.
     * 
     * @return the total count
     * @throws IOException if an I/O error occurs
     */
    int count() throws IOException;
    
    /**
     * Clears all objects from persistent storage.
     * Use with caution!
     * 
     * @throws IOException if an I/O error occurs
     */
    void clear() throws IOException;
}