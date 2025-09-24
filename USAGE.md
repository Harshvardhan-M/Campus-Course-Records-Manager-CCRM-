# Campus Course & Records Manager - User Guide

## 📋 Table of Contents
1. [Getting Started](#getting-started)
2. [System Navigation](#system-navigation)
3. [Student Management](#student-management)
4. [Course Management](#course-management)
5. [Enrollment Operations](#enrollment-operations)
6. [Transcript Generation](#transcript-generation)
7. [Data Import/Export](#data-importexport)
8. [Backup & Restore](#backup--restore)
9. [Configuration](#configuration)
10. [Troubleshooting](#troubleshooting)

## 🚀 Getting Started

### First Time Setup

1. **Start the application:**
   ```bash
   java -ea Main
   ```

2. **Initial screen will show:**
   ```
   ====================================================
       WELCOME TO CAMPUS COURSE & RECORDS MANAGER
       Version 1.0.0
       Campus College
   ====================================================
   Starting system initialization...
   Checking configuration... ✓
   Initializing services... ✓
   System ready!
   ```

3. **The system automatically creates required directories:**
   - `data/` - Application data
   - `imports/` - CSV import files
   - `exports/` - Generated exports
   - `backups/` - System backups
   - `config/` - Configuration files

## 🧭 System Navigation

### Main Menu Structure
```
==================================================
    CAMPUS COURSE & RECORDS MANAGER
    Campus College
    User: Administrator
==================================================
1. Student Management
2. Course Management
3. Enrollment Management
4. Transcript Generation
5. Import/Export Data
6. Backup & Restore
7. Reports & Statistics
8. Configuration
9. Help
0. Exit
==================================================
```

### Navigation Tips
- Enter the number corresponding to your choice
- Use `0` to return to the previous menu
- Press Enter when prompted to continue
- Type `y` or `yes` for confirmations

## 👨‍🎓 Student Management

### Adding a New Student

1. Select `1. Student Management` → `1. Add New Student`
2. Enter the following information:

```
--- Add New Student ---
Student ID (format S1234567): S2024001
First Name: John
Last Name: Doe
Email: john.doe@college.edu
Birth Date (YYYY-MM-DD): 2000-05-15
Major: Computer Science
```

**Result:** `✓ Student added successfully!`

### Student ID Format Rules
- Must start with 'S'
- Followed by 7 digits
- Example: `S2024001`, `S2023456`

### Viewing All Students

Select `1. Student Management` → `2. View All Students`

**Sample Output:**
```
--- All Students ---
ID           Name                 Email                          Major           Credits GPA  
----------------------------------------------------------------------------------------------------
S2024001     John Doe            john.doe@college.edu           Computer Science    12     3.75
S2024002     Jane Smith          jane.smith@college.edu         Mathematics         15     3.90
S2024003     Bob Johnson         bob.johnson@college.edu        Physics             9      3.50

Total students: 3
```

### Searching Students

1. Select `1. Student Management` → `3. Search Students`
2. Enter search term (name, email, major, or ID)

**Example:**
```
--- Search Students ---
Enter search term (name, email, major, or ID): Computer Science

Search results (1 found):
• ID: S2024001, Name: John Doe, Email: john.doe@college.edu, Birth Date: 2000-05-15, Role: Student, Major: Computer Science, Credits: 12, GPA: 3.75
```

## 📚 Course Management

### Creating a New Course

1. Select `2. Course Management` → `1. Create New Course`
2. Fill in course details:

```
--- Create New Course ---
Course ID (format CS101): CS101
Course Title: Introduction to Programming
Credits (1-6): 3
Department: Computer Science
Description: Basic programming concepts using Java
Prerequisites (optional): None
Available semesters: [SPRING, SUMMER, FALL]
Semester: FALL
Year: 2024
Max Enrollment: 25
```

**Result:** `✓ Course created successfully!`

### Course ID Format Rules
- 2-4 letter department code + 3-digit number
- Examples: `CS101`, `MATH200`, `PHYS150`

### Viewing All Courses

Select `2. Course Management` → `2. View All Courses`

**Sample Output:**
```
--- All Courses ---
Course ID  Title                          Credits  Department       Semester   Year     Enroll
----------------------------------------------------------------------------------------------------
CS101      Introduction to Programming    3        Computer Science FALL       2024     5/25
MATH200    Calculus I                    4        Mathematics      FALL       2024     12/30
PHYS150    General Physics               4        Physics          SPRING     2025     0/20

Total courses: 3
```

## 📝 Enrollment Operations

### Enrolling a Student in Course

1. Select `3. Enrollment Management` → `1. Enroll Student in Course`
2. Provide student and course information:

```
--- Enroll Student in Course ---
Student ID: S2024001
Course ID: CS101

Enrollment Preview:
Student: John Doe (S2024001)
Course: Introduction to Programming (CS101)
Credits: 3
Current semester credits: 9
After enrollment: 12
Course availability: 5/25
Proceed with enrollment? (y/n): y
```

**Success Result:** `✓ Student enrolled successfully! Enrollment ID: ENR1640995200123`

### Credit Limit Validation

The system enforces credit limits per semester (default: 18 credits maximum):

**Example of Credit Limit Exceeded:**
```
✗ Credit limit exceeded: Student S2024001 credit limit exceeded: Current=15, Attempted=6, Max=18
  Current credits: 15
  Attempted credits: 6
  Maximum allowed: 18
  Excess: 3
```

### Duplicate Enrollment Prevention

**Example:**
```
✗ Duplicate enrollment: Student S2024001 is already enrolled in course CS101
```

### Assigning Grades

1. Select `3. Enrollment Management` → `2. Assign Grades`
2. Enter enrollment details:

```
--- Assign Grades ---
Enrollment ID: ENR1640995200123

Enrollment Details:
Student: John Doe
Course: Introduction to Programming
Current Status: ENROLLED
Current Grade: Not assigned

Available Grades: [S, A, B, C, D, F, I, W, AU]
Enter grade: A
```

**Result:** 
```
✓ Grade assigned successfully!
Student's updated GPA: 3.85
```

### Grade System

| Grade | Description | Grade Points | Passing |
|-------|-------------|--------------|---------|
| A | Excellent | 4.0 | ✅ |
| B | Good | 3.0 | ✅ |
| C | Satisfactory | 2.0 | ✅ |
| D | Below Average | 1.0 | ✅ |
| F | Fail | 0.0 | ❌ |
| S | Satisfactory (Pass/Fail) | 0.0 | ✅ |
| I | Incomplete | 0.0 | ❌ |
| W | Withdrawal | 0.0 | ❌ |
| AU | Audit | 0.0 | ❌ |

## 🎓 Transcript Generation

### Complete Transcript

1. Select `4. Transcript Generation` → `1. Generate Complete Transcript`
2. Enter student ID:

```
--- Generate Complete Transcript ---
Student ID: S2024001
Additional notes (optional): Official transcript for transfer application
```

**Generated Transcript:**
```
================================================================================
OFFICIAL TRANSCRIPT
==================
Student: John Doe (ID: S2024001)
Major: Computer Science
Generated: 2024-01-15T10:30:00

=== FALL 2024 ===
CS101      Introduction to Programming        3 A
MATH200    Calculus I                        4 B
ENG101     English Composition               3 A

=== SPRING 2025 ===
CS102      Data Structures                   3 A
MATH201    Calculus II                       4 B

SUMMARY
=======
Credits Attempted: 17
Credits Earned: 17
Cumulative GPA: 3.76
Notes: Official transcript for transfer application
================================================================================
```

### Export Options

After generating a transcript, you can save it to file:

```
Save transcript to file? (y/n): y
✓ Transcript saved to exports/S2024001_complete_transcript_20240115.txt
```

**Available Export Formats:**
- **TXT** - Plain text format
- **HTML** - Web-friendly format with tables
- **CSV** - Spreadsheet compatible

### Filtered Transcripts

#### Honor Roll Transcript (B+ or better)
```
--- Generate Honor Roll Transcript ---
Student ID: S2024001

Shows only courses with grade B or better
```

#### Department-Specific Transcript
```
--- Generate Department Transcript ---
Student ID: S2024001
Department: Computer Science

Shows only Computer Science courses
```

#### Semester-Specific Transcript
```
--- Generate Semester Transcript ---
Student ID: S2024001
Semester: FALL
Year: 2024

Shows only Fall 2024 courses
```

## 📊 Data Import/Export

### CSV Import Format

#### Students CSV Format
Create `imports/students.csv`:
```csv
ID,FirstName,LastName,Email,BirthDate,Major
S2024001,John,Doe,john.doe@college.edu,2000-05-15,Computer Science
S2024002,Jane,Smith,jane.smith@college.edu,1999-08-22,Mathematics
S2024003,Bob,Johnson,bob.johnson@college.edu,2001-02-10,Physics
```

#### Courses CSV Format
Create `imports/courses.csv`:
```csv
CourseID,Title,Description,Credits,Department,Prerequisites,Semester,Year,MaxEnrollment
CS101,Introduction to Programming,Basic programming concepts,3,Computer Science,None,FALL,2024,25
MATH200,Calculus I,Differential calculus,4,Mathematics,None,FALL,2024,30
PHYS150,General Physics,Classical mechanics,4,Physics,MATH200,SPRING,2025,20
```

#### Enrollments CSV Format
Create `imports/enrollments.csv`:
```csv
StudentID,CourseID,Grade
S2024001,CS101,A
S2024001,MATH200,B
S2024002,MATH200,A
```

### Import Process

1. Select `5. Import/Export Data` → `1. Import Students from CSV`
2. Place your CSV file in the `imports/` directory
3. Enter filename: `students.csv`

**Import Results:**
```
Import completed: 3 successful, 0 errors
Students imported successfully!
```

### Bulk Import

Select `5. Import/Export Data` → `7. Bulk Import All Data`

This imports all files in the imports directory:
- `students.csv`
- `courses.csv` 
- `enrollments.csv`

**Sample Results:**
```
Bulk Import Results:
✓ students: 15 imported successfully
✓ courses: 8 imported successfully  
✓ enrollments: 25 imported successfully
```

### Export Operations

#### Export Students
Select `5. Import/Export Data` → `4. Export Students to CSV`

**Generated file:** `exports/students_export.csv`
```csv
ID,FirstName,LastName,Email,BirthDate,Major,TotalCredits,GPA
S2024001,John,Doe,john.doe@college.edu,2000-05-15,Computer Science,12,3.75
S2024002,Jane,Smith,jane.smith@college.edu,1999-08-22,Mathematics,15,3.90
```

## 💾 Backup & Restore

### Creating Full Backup

1. Select `6. Backup & Restore` → `1. Create Full Backup`

```
--- Create Full Backup ---
Creating full backup of all data...
✓ Full backup created: full_backup_20240115_103000.zip (Original: 2.3 MB, Compressed: 456 KB)
Compression ratio: 19.8%
```

**Backup Contents:**
- All student data
- All course information
- Enrollment records
- Configuration files
- Metadata and timestamps

### Backup File Location
Backups are stored in `backups/` directory with naming convention:
- `full_backup_YYYYMMDD_HHMMSS.zip`
- `incremental_backup_YYYYMMDD_HHMMSS.zip`

### Listing Available Backups

Select `6. Backup & Restore` → `3. List Available Backups`

```
--- Available Backups ---
1. full_backup_20240115_103000.zip - 456 KB - 2024-01-15 10:30:00 (FULL)
2. incremental_backup_20240114_150000.zip - 125 KB - 2024-01-14 15:00:00 (INCREMENTAL)
3. full_backup_20240110_080000.zip - 398 KB - 2024-01-10 08:00:00 (FULL)

Total: 3 backups, 979 KB used
```

### Restoring from Backup

1. Select `6. Backup & Restore` → `4. Restore from Backup`
2. Enter backup filename: `full_backup_20240115_103000.zip`

**Restore Process:**
```
--- Restore from Backup ---
Extracting backup file...
Creating safety backup of current data...
Restoring data files...
✓ Successfully restored 245 files from full_backup_20240115_103000.zip
```

⚠️ **Warning:** Restore operations replace all current data. A safety backup is automatically created.

## ⚙️ Configuration

### Viewing Configuration

Select `8. Configuration` → `1. View Current Configuration`

```
=== Application Configuration ===
Institution: Campus College
Version: 1.0.0
Semester System: TRADITIONAL
Max Credits/Semester: 18
Min Credits/Semester: 12
Max Enrollments/Student: 6
Grade Replacement: Disabled
Backup Retention: 30 days
Debug Mode: Disabled
===================================
```

### Configuration File Location
Settings are stored in `config/app.properties`:

```properties
app.institution.name=Campus College
app.semester.system=TRADITIONAL
student.max.credits.per.semester=18
student.min.credits.per.semester=12
student.max.enrollments=6
grading.allow.replacement=false
backup.retention.days=30
app.version=1.0.0
app.debug.enabled=false
```

### Modifying Configuration

**Through Menu:**
1. Select `8. Configuration` → `2. Modify Configuration`
2. Follow prompts to change specific settings

**Direct File Edit:**
1. Edit `config/app.properties`
2. Select `8. Configuration` → `5. Reload Configuration`

### Configuration Validation

Select `8. Configuration` → `4. Validate Configuration`

**Sample Validation Results:**
```
Configuration Validation Results:
✓ All settings are valid
✓ Credit limits are properly configured
✓ Backup retention period is valid
✓ Institution name is set
```

**Validation Errors Example:**
```
Configuration Validation Errors:
✗ Minimum credits per semester cannot be greater than maximum credits
✗ Backup retention days must be at least 1
✗ Institution name cannot be empty
```

## 📊 Reports & Statistics

### Student Statistics

Select `7. Reports & Statistics` → `1. Student Statistics`

```
--- Student Statistics ---
Total Students: 156
Average GPA: 3.42
Total Credits Earned: 2,340

Major Distribution:
  Computer Science: 45
  Mathematics: 32
  Physics: 28
  Engineering: 35
  Business: 16
```

### System Statistics

Select `7. Reports & Statistics` → `7. System Statistics`

```
--- System Statistics ---
System Information:
  Java Version: 11.0.19
  Operating System: Windows 10
  Available Memory: 512 MB
  Used Memory: 128 MB

Database Statistics:
  Total Students: 156
  Total Courses: 89
  Total Enrollments: 1,245
  Active Enrollments: 892

File System:
  Data Directory Size: 2.3 MB
  Backup Directory Size: 15.6 MB
  Export Directory Size: 847 KB
```

## 🔧 Troubleshooting

### Common Issues

#### Issue: "Student ID format invalid"
**Solution:** Use format `S` followed by 7 digits (e.g., `S2024001`)

#### Issue: "Course ID format invalid"  
**Solution:** Use 2-4 letters + 3 digits (e.g., `CS101`, `MATH200`)

#### Issue: "Credit limit exceeded"
**Check:** Current semester credit total and system configuration
- Default limit: 18 credits per semester
- Modify in configuration if needed

#### Issue: "File not found during import"
**Solution:** 
1. Ensure file is in `imports/` directory
2. Check filename spelling
3. Verify file is not open in another program

#### Issue: "Invalid date format"
**Solution:** Use ISO format `YYYY-MM-DD` (e.g., `2000-05-15`)

### Debug Mode

Enable debug mode for detailed error information:
```bash
java -ea -Dccrm.debug.enabled=true Main
```

Debug mode provides:
- Detailed error messages
- Stack traces for exceptions
- Memory usage information
- File operation details

### Data Recovery

**If data appears corrupted:**
1. Stop the application
2. Select `6. Backup & Restore` → `4. Restore from Backup`
3. Choose a recent backup file
4. Restart the application

**If no backups exist:**
1. Check `data/` directory for CSV files
2. Use `5. Import/Export Data` to reimport from CSV
3. Create immediate backup after recovery

### Memory Issues

**If application runs slowly:**
1. Check system requirements (minimum 64MB RAM)
2. Close other applications
3. Run with increased memory: `java -Xmx256m -ea Main`

### File Permission Issues

**If backup/restore fails:**
1. Run command prompt as administrator
2. Check that directories are not read-only
3. Ensure antivirus is not blocking file operations

## 📞 Support

### Help Menu
Select `9. Help` from the main menu for:
- Feature overview
- Key shortcuts
- Directory information
- Contact information

### System Information
- Configuration file: `config/app.properties`
- Data directory: `data/`
- Log files: Debug output in console
- Version information: Available in configuration

### Best Practices

1. **Regular Backups:** Create full backups weekly
2. **Data Validation:** Always review import results
3. **Configuration:** Validate configuration after changes  
4. **Testing:** Use debug mode when troubleshooting
5. **Documentation:** Keep track of customizations

---

**Campus Course & Records Manager v1.0**  
*Complete user guide for comprehensive academic management*