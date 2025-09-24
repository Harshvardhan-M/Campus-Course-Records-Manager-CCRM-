# Campus Course & Records Manager (CCRM)

A comprehensive Java SE console application for managing campus courses and student records. This project demonstrates advanced Java programming concepts, design patterns, and best practices.

## Project Overview

The Campus Course & Records Manager (CCRM) is a complete academic management system that handles:

- **Student Management** - Registration, updates, academic tracking
- **Course Management** - Course creation, scheduling, instructor assignments
- **Enrollment Management** - Student course enrollments with credit limits and validations
- **Transcript Generation** - Official transcripts with flexible filtering options
- **Import/Export Operations** - Bulk data management via CSV files
- **Backup & Restore** - Comprehensive data protection and recovery
- **Reports & Analytics** - Statistical analysis and performance tracking

## Quick Start

### Prerequisites

- Java 8 or higher
- Minimum 64MB RAM
- 50MB free disk space

### Running the Application

```bash
# Enable assertions for full validation
java -ea Main

# Debug mode with enhanced logging
java -ea -Dccrm.debug.enabled=true Main

# Custom configuration file
java -ea -Dccrm.config.file=config/custom.properties Main
```

### First Run Setup

1. The application automatically creates required directories:
   - `config/` - Configuration files
   - `data/` - Application data storage
   - `imports/` - CSV import files
   - `exports/` - Generated export files
   - `backups/` - System backups

2. Default configuration file is created at `config/app.properties`
3. Sample data can be imported from the `data/samples/` directory

## Project Structure

```
CCRMProject/
├── src/
│   └── edu/ccrm/
│       ├── cli/              # Command-line interface
│       │   └── CLIMenu.java
│       ├── config/           # Configuration management
│       │   └── AppConfig.java
│       ├── domain/           # Core business entities
│       │   ├── Person.java   (abstract)
│       │   ├── Student.java
│       │   ├── Instructor.java
│       │   ├── Course.java   (Builder pattern)
│       │   ├── Enrollment.java
│       │   └── Transcript.java (Builder pattern)
│       ├── exception/        # Custom exceptions
│       │   ├── DuplicateEnrollmentException.java
│       │   └── MaxCreditLimitExceededException.java
│       ├── io/               # Import/Export operations
│       │   ├── ImportExportService.java
│       │   └── BackupService.java
│       ├── service/          # Business logic layer
│       │   ├── Persistable.java (interface)
│       │   ├── Searchable.java (interface)
│       │   ├── StudentService.java
│       │   ├── CourseService.java
│       │   ├── EnrollmentService.java
│       │   └── TranscriptService.java
│       └── util/             # Utility classes and enums
│           ├── Semester.java (enum)
│           ├── Grade.java (enum)
│           ├── Validators.java
│           ├── Comparators.java
│           └── RecursiveUtils.java
├── Main.java                 # Application entry point
├── data/
│   └── samples/             # Sample CSV data files
├── config/                  # Configuration files
├── imports/                 # Import directory
├── exports/                 # Export directory
├── backups/                 # Backup directory
└── docs/
    ├── README.md
    └── USAGE.md
```

## Technical Implementation

### Design Patterns Implemented

#### 1. **Singleton Pattern**
- `AppConfig.java` - Thread-safe configuration management
- Lazy initialization with double-checked locking

#### 2. **Builder Pattern**
- `Course.java` - Flexible course creation with optional parameters
- `Transcript.java` - Complex transcript generation with filtering

#### 3. **Abstract Factory Pattern**
- `Person.java` (abstract) → `Student.java`, `Instructor.java`
- Polymorphic behavior through method overriding

### Advanced Java Features

#### **Object-Oriented Programming**
- ✅ Inheritance and polymorphism
- ✅ Method overloading and overriding
- ✅ Abstract classes and interfaces
- ✅ Encapsulation with proper access modifiers

#### **Exception Handling**
- ✅ Custom exception classes
- ✅ Multi-catch blocks
- ✅ Try-with-resources for file operations
- ✅ Comprehensive error handling strategies

#### **Java 8+ Features**
- ✅ Stream API for data processing and filtering
- ✅ Lambda expressions and functional interfaces
- ✅ Method references and collectors
- ✅ Optional for null-safe operations

#### **File I/O & NIO.2**
- ✅ Path API for file system operations
- ✅ Files utility class for efficient file handling
- ✅ Recursive directory operations
- ✅ File compression and backup operations

#### **Collections & Generics**
- ✅ Generic interfaces and classes
- ✅ Custom comparators with lambda expressions
- ✅ Stream operations for data analysis
- ✅ Type-safe collections throughout


## Windows Installation Guide

### Step 1: Download Java JDK

1. Visit [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
2. Download JDK 11 or higher for Windows (x64)
3. Run the installer with administrator privileges


### Step 2: Install JDK

1. Follow the installation wizard
2. Note the installation path (typically `C:\Program Files\Java\jdk-11.x.x`)
3. Complete the installation


### Step 3: Set Environment Variables

1. Open **System Properties** → **Advanced** → **Environment Variables**
2. Add **JAVA_HOME** system variable:
   - Variable: `JAVA_HOME`
   - Value: `C:\Program Files\Java\jdk-11.x.x`
3. Update **PATH** system variable:
   - Add: `%JAVA_HOME%\bin`

### Step 4: Verify Installation

```cmd
java -version
javac -version
```

Expected output:
```
java version "11.0.x" 2024-xx-xx LTS
Java(TM) SE Runtime Environment (build 11.0.x+xx-LTS)
Java HotSpot(TM) 64-Bit Server VM (build 11.0.x+xx-LTS, mixed mode)
```


### Step 5: Run CCRM

```cmd
cd CCRMProject
javac -d . src/edu/ccrm/*/*.java src/edu/ccrm/*/*/*.java Main.java
java -ea Main
```

## Syllabus Mapping

### Core Java Concepts

| Concept | Implementation Location | Demonstration |
|---------|------------------------|---------------|
| **Object-Oriented Programming** | Domain package | Classes, inheritance, polymorphism |
| **Exception Handling** | Exception package + Services | Custom exceptions, try-catch-finally |
| **Collections Framework** | Service layer | Lists, Maps, Sets with generics |
| **File I/O Operations** | I/O package | NIO.2, CSV processing, backup |
| **Multithreading** | BackupService | Thread-safe Singleton pattern |
| **Generics** | Interfaces | Type-safe service contracts |
| **Lambda Expressions** | Utility classes | Stream operations, comparators |
| **Design Patterns** | Throughout | Singleton, Builder, Factory |

### Advanced Topics

| Topic | Implementation | Educational Value |
|-------|----------------|-------------------|
| **Recursion** | RecursiveUtils.java | Directory traversal algorithms |
| **Assertions** | Main.java, validation | Design by contract principles |
| **Regular Expressions** | Validators.java | Input validation patterns |
| **Serialization** | Configuration | Object persistence concepts |
| **Reflection** | Dynamic behavior | Runtime type inspection |

## Performance Considerations

### Memory Management
- Efficient object creation and reuse
- Proper resource cleanup with try-with-resources
- Stream operations for large data processing

### File Operations
- NIO.2 for improved file handling performance
- Buffered I/O for large file processing
- Compression for backup storage optimization

### Data Structures
- Appropriate collection types for different use cases
- Indexed access patterns for frequent lookups
- Stream parallel processing for large datasets

## Development Features

### Enabling Assertions
```bash
# Enable all assertions
java -ea Main

# Enable assertions for specific packages
java -ea:edu.ccrm... Main

# System property assertions
java -ea -esa Main
```

### Debug Mode
```bash
# Enable comprehensive debug logging
java -ea -Dccrm.debug.enabled=true Main
```

### Configuration Options
```properties
# config/app.properties
app.institution.name=Your College Name
student.max.credits.per.semester=18
student.min.credits.per.semester=12
app.debug.enabled=false
backup.retention.days=30
```

## Testing & Validation

### Input Validation
- Student ID format: `S1234567`
- Course ID format: `CS101`, `MATH200`
- Email validation with regex patterns
- Date validation with proper range checking

### Business Rules
- Credit limit validation (1-18 credits per semester)
- Duplicate enrollment prevention
- Grade point calculation accuracy
- Transcript generation integrity

### Error Handling
- Graceful degradation on file access errors
- User-friendly error messages
- Comprehensive logging for debugging
- Recovery mechanisms for data corruption

## Educational Value

This project serves as a comprehensive demonstration of:

1. **Software Design Principles** - SOLID principles, separation of concerns
2. **Java Best Practices** - Coding standards, documentation, error handling
3. **Real-World Application** - Practical business logic implementation
4. **Advanced Java Features** - Modern Java capabilities and idioms
5. **System Architecture** - Layered architecture with clear boundaries

## Contributing

To extend or modify the CCRM system:

1. Follow the existing package structure
2. Maintain the service layer pattern
3. Add comprehensive exception handling
4. Include unit tests for new features
5. Update documentation accordingly
Campus Course & Records Manager v1.0**  
*A comprehensive Java SE application demonstrating advanced programming concepts*
