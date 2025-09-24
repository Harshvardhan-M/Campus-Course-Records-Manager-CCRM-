package edu.ccrm.io;

import edu.ccrm.util.RecursiveUtils;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.*;

/**
 * Service for backup operations with compression and scheduling.
 * Demonstrates NIO.2, recursion, and file compression.
 */
public class BackupService {
    
    private static final String BACKUP_DIR = "backups";
    private static final String DATA_DIR = "data";
    private Path backupPath;
    private Path dataPath;
    
    public BackupService() {
        this.backupPath = Paths.get(BACKUP_DIR);
        this.dataPath = Paths.get(DATA_DIR);
        initializeDirectories();
    }
    
    /**
     * Creates a full backup of all data files.
     * Demonstrates NIO.2 file operations and compression.
     */
    public BackupResult createFullBackup() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupName = "full_backup_" + timestamp;
        
        try {
            // Create backup directory structure
            Path currentBackupPath = backupPath.resolve(backupName);
            Files.createDirectories(currentBackupPath);
            
            // Copy data files using NIO.2
            if (Files.exists(dataPath)) {
                copyDirectoryRecursively(dataPath, currentBackupPath.resolve("data"));
            }
            
            // Create metadata file
            createBackupMetadata(currentBackupPath, "FULL", timestamp);
            
            // Compress backup
            Path zipFile = compressBackup(currentBackupPath, backupName + ".zip");
            
            // Calculate backup size using recursion
            long backupSize = RecursiveUtils.calculateDirectorySize(currentBackupPath.toFile());
            long zipSize = Files.size(zipFile);
            
            // Clean up uncompressed backup
            deleteDirectoryRecursively(currentBackupPath);
            
            return new BackupResult(true, 
                    String.format("Full backup created: %s (Original: %s, Compressed: %s)",
                            zipFile.getFileName(),
                            RecursiveUtils.formatFileSize(backupSize),
                            RecursiveUtils.formatFileSize(zipSize)),
                    zipFile, backupSize, zipSize);
            
        } catch (Exception e) {
            return new BackupResult(false, "Backup failed: " + e.getMessage(), null, 0, 0);
        }
    }
    
    /**
     * Creates an incremental backup (files modified since last backup).
     */
    public BackupResult createIncrementalBackup() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupName = "incremental_backup_" + timestamp;
        
        try {
            Path lastBackupTime = getLastBackupTimestamp();
            long lastBackupMillis = lastBackupTime != null ? 
                    Files.getLastModifiedTime(lastBackupTime).toMillis() : 0;
            
            Path currentBackupPath = backupPath.resolve(backupName);
            Files.createDirectories(currentBackupPath);
            
            // Copy only modified files
            int filesCopied = copyModifiedFiles(dataPath, currentBackupPath.resolve("data"), lastBackupMillis);
            
            if (filesCopied == 0) {
                // Clean up empty backup
                Files.deleteIfExists(currentBackupPath);
                return new BackupResult(true, "No changes detected - incremental backup skipped", null, 0, 0);
            }
            
            createBackupMetadata(currentBackupPath, "INCREMENTAL", timestamp);
            Path zipFile = compressBackup(currentBackupPath, backupName + ".zip");
            
            long backupSize = RecursiveUtils.calculateDirectorySize(currentBackupPath.toFile());
            long zipSize = Files.size(zipFile);
            
            deleteDirectoryRecursively(currentBackupPath);
            
            return new BackupResult(true,
                    String.format("Incremental backup created: %s (%d files, %s compressed)",
                            zipFile.getFileName(), filesCopied, RecursiveUtils.formatFileSize(zipSize)),
                    zipFile, backupSize, zipSize);
            
        } catch (Exception e) {
            return new BackupResult(false, "Incremental backup failed: " + e.getMessage(), null, 0, 0);
        }
    }
    
    /**
     * Restores data from a backup file.
     */
    public RestoreResult restoreFromBackup(String backupFileName) {
        Path backupFile = backupPath.resolve(backupFileName);
        
        if (!Files.exists(backupFile)) {
            return new RestoreResult(false, "Backup file not found: " + backupFileName, 0);
        }
        
        try {
            // Create temporary restore directory
            String tempDirName = "restore_temp_" + System.currentTimeMillis();
            Path tempRestorePath = backupPath.resolve(tempDirName);
            Files.createDirectories(tempRestorePath);
            
            // Extract backup
            extractBackup(backupFile, tempRestorePath);
            
            // Validate backup structure
            Path backupDataPath = tempRestorePath.resolve("data");
            if (!Files.exists(backupDataPath)) {
                return new RestoreResult(false, "Invalid backup structure", 0);
            }
            
            // Backup current data (safety)
            if (Files.exists(dataPath)) {
                String safetyBackupName = "pre_restore_backup_" + 
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                createSafetyBackup(dataPath, backupPath.resolve(safetyBackupName));
            }
            
            // Restore data
            if (Files.exists(dataPath)) {
                deleteDirectoryRecursively(dataPath);
            }
            copyDirectoryRecursively(backupDataPath, dataPath);
            
            // Count restored files
            int restoredFiles = RecursiveUtils.countFiles(dataPath.toFile());
            
            // Clean up
            deleteDirectoryRecursively(tempRestorePath);
            
            return new RestoreResult(true,
                    String.format("Successfully restored %d files from %s", restoredFiles, backupFileName),
                    restoredFiles);
            
        } catch (Exception e) {
            return new RestoreResult(false, "Restore failed: " + e.getMessage(), 0);
        }
    }
    
    /**
     * Lists all available backups with details.
     */
    public List<BackupInfo> listBackups() {
        List<BackupInfo> backups = new ArrayList<>();
        
        try {
            if (!Files.exists(backupPath)) {
                return backups;
            }
            
            Files.list(backupPath)
                    .filter(path -> path.toString().endsWith(".zip"))
                    .forEach(backupFile -> {
                        try {
                            BackupInfo info = getBackupInfo(backupFile);
                            if (info != null) {
                                backups.add(info);
                            }
                        } catch (Exception e) {
                            System.err.println("Error reading backup info: " + e.getMessage());
                        }
                    });
            
            // Sort by creation date (newest first)
            backups.sort((b1, b2) -> b2.getCreationDate().compareTo(b1.getCreationDate()));
            
        } catch (Exception e) {
            System.err.println("Error listing backups: " + e.getMessage());
        }
        
        return backups;
    }
    
    /**
     * Cleans up old backups based on retention policy.
     */
    public CleanupResult cleanupOldBackups(int maxBackups, int daysToKeep) {
        List<BackupInfo> backups = listBackups();
        List<Path> deletedBackups = new ArrayList<>();
        long spaceFreed = 0;
        
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
            
            for (int i = 0; i < backups.size(); i++) {
                BackupInfo backup = backups.get(i);
                boolean shouldDelete = false;
                
                // Keep recent backups within retention period
                if (backup.getCreationDate().isBefore(cutoffDate) && i >= maxBackups) {
                    shouldDelete = true;
                }
                
                if (shouldDelete) {
                    Path backupFile = backupPath.resolve(backup.getFileName());
                    if (Files.exists(backupFile)) {
                        spaceFreed += Files.size(backupFile);
                        Files.delete(backupFile);
                        deletedBackups.add(backupFile);
                    }
                }
            }
            
            return new CleanupResult(true,
                    String.format("Cleaned up %d old backups, freed %s",
                            deletedBackups.size(), RecursiveUtils.formatFileSize(spaceFreed)),
                    deletedBackups.size(), spaceFreed);
            
        } catch (Exception e) {
            return new CleanupResult(false, "Cleanup failed: " + e.getMessage(), 0, 0);
        }
    }
    
    // Private helper methods
    
    private void initializeDirectories() {
        try {
            Files.createDirectories(backupPath);
            Files.createDirectories(dataPath);
        } catch (IOException e) {
            System.err.println("Error initializing backup directories: " + e.getMessage());
        }
    }
    
    private void copyDirectoryRecursively(Path source, Path destination) throws IOException {
        Files.walk(source)
                .forEach(sourcePath -> {
                    try {
                        Path destPath = destination.resolve(source.relativize(sourcePath));
                        if (Files.isDirectory(sourcePath)) {
                            Files.createDirectories(destPath);
                        } else {
                            Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Copy failed", e);
                    }
                });
    }
    
    private int copyModifiedFiles(Path source, Path destination, long lastBackupTime) throws IOException {
        if (!Files.exists(source)) {
            return 0;
        }
        
        final int[] fileCount = {0};
        
        Files.walk(source)
                .filter(Files::isRegularFile)
                .filter(path -> {
                    try {
                        return Files.getLastModifiedTime(path).toMillis() > lastBackupTime;
                    } catch (IOException e) {
                        return false;
                    }
                })
                .forEach(sourcePath -> {
                    try {
                        Path destPath = destination.resolve(source.relativize(sourcePath));
                        Files.createDirectories(destPath.getParent());
                        Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                        fileCount[0]++;
                    } catch (IOException e) {
                        throw new RuntimeException("Modified file copy failed", e);
                    }
                });
        
        return fileCount[0];
    }
    
    private void createBackupMetadata(Path backupDir, String type, String timestamp) throws IOException {
        Path metadataFile = backupDir.resolve("backup_metadata.txt");
        
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(metadataFile))) {
            writer.println("Backup Type: " + type);
            writer.println("Created: " + timestamp);
            writer.println("System: " + System.getProperty("os.name"));
            writer.println("Java Version: " + System.getProperty("java.version"));
            writer.println("Data Directory Size: " + 
                    RecursiveUtils.formatFileSize(RecursiveUtils.calculateDirectorySize(dataPath.toFile())));
        }
    }
    
    private Path compressBackup(Path sourceDir, String zipFileName) throws IOException {
        Path zipFile = backupPath.resolve(zipFileName);
        
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            Files.walk(sourceDir)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            String entryName = sourceDir.relativize(file).toString();
                            zos.putNextEntry(new ZipEntry(entryName));
                            Files.copy(file, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException("Compression failed", e);
                        }
                    });
        }
        
        return zipFile;
    }
    
    private void extractBackup(Path zipFile, Path destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path entryPath = destDir.resolve(entry.getName());
                Files.createDirectories(entryPath.getParent());
                
                if (!entry.isDirectory()) {
                    Files.copy(zis, entryPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }
    
    private void createSafetyBackup(Path source, Path dest) throws IOException {
        Files.createDirectories(dest);
        copyDirectoryRecursively(source, dest);
    }
    
    private void deleteDirectoryRecursively(Path dir) throws IOException {
        if (Files.exists(dir)) {
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            System.err.println("Failed to delete: " + path);
                        }
                    });
        }
    }
    
    private Path getLastBackupTimestamp() throws IOException {
        if (!Files.exists(backupPath)) {
            return null;
        }
        
        return Files.list(backupPath)
                .filter(path -> path.toString().endsWith(".zip"))
                .max(Comparator.comparing(path -> {
                    try {
                        return Files.getLastModifiedTime(path);
                    } catch (IOException e) {
                        return FileTime.fromMillis(0);
                    }
                }))
                .orElse(null);
    }
    
    private BackupInfo getBackupInfo(Path backupFile) {
        try {
            String fileName = backupFile.getFileName().toString();
            long size = Files.size(backupFile);
            LocalDateTime created = LocalDateTime.ofInstant(
                    Files.getLastModifiedTime(backupFile).toInstant(),
                    java.time.ZoneId.systemDefault());
            
            return new BackupInfo(fileName, size, created, "FULL");
            
        } catch (Exception e) {
            return null;
        }
    }
    
    // Result classes
    
    public static class BackupResult {
        private final boolean success;
        private final String message;
        private final Path backupFile;
        private final long originalSize;
        private final long compressedSize;
        
        public BackupResult(boolean success, String message, Path backupFile, long originalSize, long compressedSize) {
            this.success = success;
            this.message = message;
            this.backupFile = backupFile;
            this.originalSize = originalSize;
            this.compressedSize = compressedSize;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Path getBackupFile() { return backupFile; }
        public long getOriginalSize() { return originalSize; }
        public long getCompressedSize() { return compressedSize; }
        public double getCompressionRatio() { 
            return originalSize > 0 ? (double) compressedSize / originalSize : 0; 
        }
    }
    
    public static class RestoreResult {
        private final boolean success;
        private final String message;
        private final int filesRestored;
        
        public RestoreResult(boolean success, String message, int filesRestored) {
            this.success = success;
            this.message = message;
            this.filesRestored = filesRestored;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getFilesRestored() { return filesRestored; }
    }
    
    public static class BackupInfo {
        private final String fileName;
        private final long size;
        private final LocalDateTime creationDate;
        private final String type;
        
        public BackupInfo(String fileName, long size, LocalDateTime creationDate, String type) {
            this.fileName = fileName;
            this.size = size;
            this.creationDate = creationDate;
            this.type = type;
        }
        
        // Getters
        public String getFileName() { return fileName; }
        public long getSize() { return size; }
        public LocalDateTime getCreationDate() { return creationDate; }
        public String getType() { return type; }
        public String getFormattedSize() { return RecursiveUtils.formatFileSize(size); }
    }
    
    public static class CleanupResult {
        private final boolean success;
        private final String message;
        private final int backupsDeleted;
        private final long spaceFreed;
        
        public CleanupResult(boolean success, String message, int backupsDeleted, long spaceFreed) {
            this.success = success;
            this.message = message;
            this.backupsDeleted = backupsDeleted;
            this.spaceFreed = spaceFreed;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getBackupsDeleted() { return backupsDeleted; }
        public long getSpaceFreed() { return spaceFreed; }
    }
}