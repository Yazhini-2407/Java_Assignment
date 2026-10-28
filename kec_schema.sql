
CREATE DATABASE IF NOT EXISTS kec_academic;
USE kec_academic;

CREATE TABLE students (
    roll_no VARCHAR(15) PRIMARY KEY,
    student_name VARCHAR(100) NOT NULL,
    department VARCHAR(20) NOT NULL,
    current_semester INT NOT NULL
);

CREATE TABLE courses (
    course_code VARCHAR(15) PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    credits INT NOT NULL,
    course_type ENUM('THEORY', 'PRACTICAL', 'EMBEDDED') NOT NULL
);

CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    roll_no VARCHAR(15) NOT NULL,
    course_code VARCHAR(15) NOT NULL,
    semester INT NOT NULL,
    classes_held INT DEFAULT 0,
    classes_attended INT DEFAULT 0,
    medical_approved BOOLEAN DEFAULT FALSE,
    UNIQUE (roll_no, course_code, semester),
    FOREIGN KEY (roll_no) REFERENCES students(roll_no),
    FOREIGN KEY (course_code) REFERENCES courses(course_code),
    CHECK (classes_held >= 0),
    CHECK (classes_attended >= 0),
    CHECK (classes_attended <= classes_held)
);

CREATE TABLE assessment_marks (
    enrollment_id INT PRIMARY KEY,
    cia1 DECIMAL(5,2),
    cia2 DECIMAL(5,2),
    assignment_marks DECIMAL(5,2),
    ese_marks DECIMAL(5,2),
    ese_absent BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (enrollment_id)
        REFERENCES enrollments(enrollment_id),
    CHECK (cia1 IS NULL OR cia1 BETWEEN 0 AND 100),
    CHECK (cia2 IS NULL OR cia2 BETWEEN 0 AND 100),
    CHECK (assignment_marks IS NULL OR assignment_marks BETWEEN 0 AND 100),
    CHECK (ese_marks IS NULL OR ese_marks BETWEEN 0 AND 100)
);
