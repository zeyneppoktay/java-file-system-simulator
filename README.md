# Java File System Simulator

A console-based file system simulation developed in Java. The project models a hierarchical file system and provides operations for navigating, creating, deleting, searching, and listing files and directories.

## Features

- Navigate through directories
- Create new directories and files
- Delete files and directories
- Recursive search by file or directory name
- Recursive search by file extension
- O(1) lookup in the current directory using a HashMap
- USER and SYSTEM access levels
- Automatic directory metadata updates
- Load the initial file system structure from a text file
- Display file and directory information including size, modification date, and access level

## Project Structure

```text
java-file-system-simulator/
├── src/
│   ├── Main.java
│   ├── FileSystemNode.java
│   ├── DirectoryNode.java
│   ├── FileNode.java
│   ├── FileSystemLoader.java
│   └── FileSystemManager.java
├── filesystem.txt
├── .gitignore
└── README.md
```

## Main Components

### FileSystemNode
Abstract base class for file system elements. It stores common information such as name, size, last modified date, access level, and parent directory.

### DirectoryNode
Represents directories in the file system. Child nodes are stored using both a list and a HashMap, allowing efficient direct lookup within the current directory.

### FileNode
Represents individual files together with their file extension and metadata.

### FileSystemLoader
Reads the file system hierarchy from `filesystem.txt` and constructs the directory structure.

### FileSystemManager
Handles file system operations such as directory navigation, file and directory creation, deletion, recursive searching, and content listing.

### Main
Provides the console-based user interface and allows users to interact with the simulated file system.

## Data Structures & Concepts

This project demonstrates several core programming and data structure concepts:

- Object-Oriented Programming
- Inheritance and abstraction
- Tree-based hierarchical structures
- Recursion
- ArrayList
- HashMap
- Stack
- File I/O

## How to Run

### Requirements

- Java Development Kit (JDK)

### Compile

From the project root directory:

```bash
javac src/*.java
```

### Run

```bash
java -cp src Main
```

Keep `filesystem.txt` in the project root directory because the application loads the initial file system structure from this file.

## Example Operations

The console application allows the user to:

```text
1 - Change current directory
2 - Navigate to a subdirectory
3 - Add a new directory
4 - Add a new file
5 - Delete a file or directory
6 - Search recursively by name
7 - Search recursively by extension
8 - Search in the current directory using O(1) lookup
9 - List current directory contents
```

## Purpose  School Project

This project was developed as an academic Java project to practice object-oriented programming, recursive algorithms, hierarchical data structures, file processing, and efficient lookup techniques.

## Author

**Zeynep Oktay**
