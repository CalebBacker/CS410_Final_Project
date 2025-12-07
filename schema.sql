-- schema.sql
-- Database schema for Grade Management App

-- Drop tables in reverse FK order if you need to reset
DROP TABLE IF EXISTS Grade;
DROP TABLE IF EXISTS Enrollment;
DROP TABLE IF EXISTS Assignment;
DROP TABLE IF EXISTS Category;
DROP TABLE IF EXISTS Student;
DROP TABLE IF EXISTS Class;

-- CLASS: one row per course section
CREATE TABLE Class (
                       class_id       INT AUTO_INCREMENT PRIMARY KEY,
                       course_number  VARCHAR(20) NOT NULL,   -- e.g. 'CS410'
                       term           VARCHAR(10) NOT NULL,   -- e.g. 'Sp25'
                       section        INT NOT NULL,           -- e.g. 1
                       description    VARCHAR(255),

-- Prevent duplicate sections for same term & course
                       UNIQUE(course_number, term, section)
);

-- CATEGORY: per-class grading categories
-- Example: Homework 40, Exam 40, Project 20
CREATE TABLE Category (
                          category_id INT AUTO_INCREMENT PRIMARY KEY,
                          class_id    INT NOT NULL,
                          name        VARCHAR(50) NOT NULL,
                          weight      DECIMAL(5,2) NOT NULL,
-- weight is the "raw" weight; you'll rescale in code if needed

                          FOREIGN KEY (class_id) REFERENCES Class(class_id)
);

CREATE INDEX idx_category_classid ON Category(class_id);

-- ASSIGNMENT: individual graded items
-- Each belongs to a category and a class
CREATE TABLE Assignment (
                            assignment_id INT AUTO_INCREMENT PRIMARY KEY,
                            class_id      INT NOT NULL,
                            category_id   INT NOT NULL,
                            name          VARCHAR(100) NOT NULL,   -- unique within a class
                            description   TEXT,
                            points        DECIMAL(7,2) NOT NULL,   -- max points, e.g. 20.0

-- Ensure no two assignments in the same class share a name
                            UNIQUE(class_id, name),

                            FOREIGN KEY (category_id) REFERENCES Category(category_id),
                            FOREIGN KEY (class_id)    REFERENCES Class(class_id)
);

CREATE INDEX idx_assignment_classid ON Assignment(class_id);
CREATE INDEX idx_assignment_categoryid ON Assignment(category_id);

-- STUDENT: global list of students
-- username is PK like
CREATE TABLE Student (
                         username   VARCHAR(50) PRIMARY KEY,
                         student_id VARCHAR(20) UNIQUE NOT NULL,
                         first_name VARCHAR(50),
                         last_name  VARCHAR(50)
);

-- ENROLLMENT: which students are in which class
CREATE TABLE Enrollment (
                            class_id INT NOT NULL,
                            username VARCHAR(50) NOT NULL,

                            PRIMARY KEY (class_id, username),

                            FOREIGN KEY (class_id) REFERENCES Class(class_id),
                            FOREIGN KEY (username) REFERENCES Student(username)
);

CREATE INDEX idx_enrollment_username ON Enrollment(username);

-- GRADE: grade for a single assignment for a single student
CREATE TABLE Grade (
                       assignment_id INT NOT NULL,
                       username      VARCHAR(50) NOT NULL,
                       grade_points  DECIMAL(7,2),

                       PRIMARY KEY (assignment_id, username),

                       FOREIGN KEY (assignment_id) REFERENCES Assignment(assignment_id),
                       FOREIGN KEY (username)      REFERENCES Student(username)
);

CREATE INDEX idx_grade_username ON Grade(username);
