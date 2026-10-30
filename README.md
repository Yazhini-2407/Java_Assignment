# KEC R2024 Academic Evaluation System

## Project Overview
The **KEC R2024 Academic Evaluation System** is a database-driven Java application developed using Java JDBC and MySQL. It is designed to manage student academic records, course enrollment, attendance, assessment marks, and examination results according to Kongu Engineering College Regulation 2024.

The system provides a menu-driven interface that helps manage academic information efficiently.

## Objectives
- To maintain student and course details.
- To manage student enrollment in courses.
- To track and update student attendance.
- To manage internal assessment marks.
- To check attendance eligibility for examinations.
- To generate student mark sheets and academic reports.

## Technologies Used
- **Programming Language:** Java
- **Database:** MySQL
- **Database Connectivity:** JDBC
- **IDE:** Visual Studio Code
- **JDK:** Java Development Kit

## Features

1. **Register Student** – Add new student details.
2. **Add Course** – Add course information.
3. **View Students** – Display registered student records.
4. **Enroll Student in Course** – Enroll students in selected courses.
5. **Update Attendance** – Record and update attendance details.
6. **Approve Medical Condonation** – Manage medical attendance condonation.
7. **Assessment Marks** – Enter and update assessment marks.
8. **Student Mark Sheet** – Generate student academic performance reports.
9. **Attendance Defaulter Report** – Identify students with low attendance.
10. **Department Result Summary** – Display department-wise academic results.

## Attendance Eligibility Rules
- **75% and above:** Eligible for examination.
- **65% to below 75%:** Eligible for consideration with approved medical condonation.
- **Below 65%:** Not eligible for examination.

## Project Structure

```text
Java_Assignment/
│
├── AcademicDAO.java
├── AcademicDAO.class
├── DBConnection.java
├── DBConnection.class
├── MainApp.java
├── MainApp.class
└── README.md
```

## Database Configuration

1. Install MySQL Server.
2. Create a database for the academic evaluation system.
3. Create the required tables for students, courses, enrollments, attendance, and assessment marks.
4. Update the database username and password in `DBConnection.java`.
5. Add the MySQL Connector/J library to the project.

## How to Run the Project

**Step 1:** Clone the repository.

```bash
git clone https://github.com/Yazhini-2407/Java_Assignment.git
```

**Step 2:** Open the project folder in Visual Studio Code.

**Step 3:** Configure the MySQL database connection.

**Step 4:** Compile the Java files with the MySQL Connector/J JAR in the classpath.

**Step 5:** Run the main application.

```bash
java MainApp
```

## Expected Output

```text
======================================
 KEC R2024 ACADEMIC EVALUATION SYSTEM
======================================

1. Register Student
2. Add Course
3. View Students
4. Enroll Student in Course
5. Update Attendance
6. Approve Medical Condonation
7. Enter / Update Assessment Marks
8. Generate Student Mark Sheet
9. Attendance Defaulter Report
10. Department Result Summary
0. Exit

Enter your choice:
```

## Learning Outcomes
- Understanding Java programming concepts.
- Implementing JDBC connectivity.
- Performing database operations using MySQL.
- Working with SQL queries and relational databases.
- Developing a menu-driven Java application.
- Managing academic records efficiently.

## Author
**Yazhini S**

B.Tech – Information Technology

Kongu Engineering College

## GitHub Repository
https://github.com/Yazhini-2407/Java_Assignment
