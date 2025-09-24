package edu.ccrm.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Utility class demonstrating recursion for directory operations.
 * Used primarily for backup and file management operations.
 */
public class RecursiveUtils {
    
    // Private constructor to prevent instantiation
    private RecursiveUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }
    
    /**
     * Recursively calculates the total size of a directory and its subdirectories.
     * Demonstrates recursion for file system traversal.
     * 
     * @param directory the directory to calculate size for
     * @return total size in bytes
     */
    public static long calculateDirectorySize(File directory) {
        if (directory == null || !directory.exists()) {
            return 0;
        }
        
        // Base case: if it's a file, return its size
        if (directory.isFile()) {
            return directory.length();
        }
        
        // Recursive case: if it's a directory, sum up all contents
        long totalSize = 0;
        File[] files = directory.listFiles();
        
        if (files != null) {
            for (File file : files) {
                totalSize += calculateDirectorySize(file); // Recursive call
            }
        }
        
        return totalSize;
    }
    
    /**
     * Recursively counts the total number of files in a directory tree.
     * 
     * @param directory the directory to count files in
     * @return total number of files
     */
    public static int countFiles(File directory) {
        if (directory == null || !directory.exists()) {
            return 0;
        }
        
        // Base case: if it's a file, count it
        if (directory.isFile()) {
            return 1;
        }
        
        // Recursive case: if it's a directory, count all contents
        int fileCount = 0;
        File[] files = directory.listFiles();
        
        if (files != null) {
            for (File file : files) {
                fileCount += countFiles(file); // Recursive call
            }
        }
        
        return fileCount;
    }
    
    /**
     * Recursively finds all files with a specific extension.
     * 
     * @param directory the directory to search in
     * @param extension the file extension to search for (without dot)
     * @return list of files with the specified extension
     */
    public static List<File> findFilesByExtension(File directory, String extension) {
        List<File> result = new ArrayList<>();
        findFilesByExtensionRecursive(directory, extension, result);
        return result;
    }
    
    /**
     * Helper method for recursive file search.
     * Demonstrates tail recursion and helper method pattern.
     */
    private static void findFilesByExtensionRecursive(File directory, String extension, List<File> result) {
        if (directory == null || !directory.exists()) {
            return;
        }
        
        // Base case: if it's a file, check extension
        if (directory.isFile()) {
            String fileName = directory.getName();
            if (fileName.toLowerCase().endsWith("." + extension.toLowerCase())) {
                result.add(directory);
            }
            return;
        }
        
        // Recursive case: if it's a directory, search all contents
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                findFilesByExtensionRecursive(file, extension, result); // Recursive call
            }
        }
    }
    
    /**
     * Recursively creates a directory tree structure representation.
     * Demonstrates string building with recursion.
     * 
     * @param directory the directory to represent
     * @param indent current indentation level
     * @return string representation of directory tree
     */
    public static String generateDirectoryTree(File directory, String indent) {
        if (directory == null || !directory.exists()) {
            return "";
        }
        
        StringBuilder tree = new StringBuilder();
        tree.append(indent).append(directory.getName()).append("\n");
        
        // If it's a directory, process its contents
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    boolean isLast = (i == files.length - 1);
                    String nextIndent = indent + (isLast ? "    " : "│   ");
                    String currentPrefix = isLast ? "└── " : "├── ";
                    
                    tree.append(indent).append(currentPrefix);
                    tree.append(generateDirectoryTree(files[i], nextIndent));
                }
            }
        }
        
        return tree.toString();
    }
    
    /**
     * Recursively calculates the maximum depth of a directory tree.
     * 
     * @param directory the directory to measure depth for
     * @return maximum depth of the directory tree
     */
    public static int calculateMaxDepth(File directory) {
        if (directory == null || !directory.exists() || directory.isFile()) {
            return 0;
        }
        
        int maxDepth = 0;
        File[] files = directory.listFiles();
        
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    int depth = calculateMaxDepth(file); // Recursive call
                    maxDepth = Math.max(maxDepth, depth);
                }
            }
        }
        
        return maxDepth + 1;
    }
    
    /**
     * Recursively deletes a directory and all its contents.
     * Use with caution - this permanently deletes files!
     * 
     * @param directory the directory to delete
     * @return true if successful, false otherwise
     */
    public static boolean deleteDirectoryRecursively(File directory) {
        if (directory == null || !directory.exists()) {
            return false;
        }
        
        // If it's a directory, first delete all contents
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!deleteDirectoryRecursively(file)) { // Recursive call
                        return false; // If any deletion fails, return false
                    }
                }
            }
        }
        
        // Finally, delete the directory/file itself
        return directory.delete();
    }
    
    /**
     * Utility method to format file size in human-readable format.
     * Not recursive, but useful for displaying results from recursive calculations.
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int unit = 1024;
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
</biltAction>