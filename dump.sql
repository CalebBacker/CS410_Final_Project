-- ========================================
-- dump.sql
-- Example data for Grade Management App
-- Assumes schema.sql has already been run.
-- ========================================

-- ----- Classes -----
INSERT INTO Class (course_number, term, section, description)
VALUES
    ('CS410', 'Fa25', 1, 'Databases Undergraduate'),
    ('CS510', 'Fa25', 1, 'Databases Graduate');

-- Let’s assume CS410, Fa25, section 1 gets id = 1
-- CS510, Fa25, section 1 gets id = 2

-- ----- Categories -----
INSERT INTO Category (class_id, name, weight)
VALUES
    (1, 'Homework', 40.0),
    (1, 'Exam',    40.0),
    (1, 'Project', 20.0),
    (2, 'Homework', 30.0),
    (2, 'Exam',    70.0);

-- For reference (not stored):
-- CS410: cat_ids might be 1=HW, 2=Exam, 3=Project

-- ----- Assignments -----
INSERT INTO Assignment (class_id, category_id, name, description, points)
VALUES
    (1, 1, 'HW1', 'Intro SQL queries', 20.0),
    (1, 1, 'HW2', 'Joins and aggregation', 25.0),
    (1, 2, 'Midterm', 'Midterm exam', 100.0),
    (1, 3, 'Project', 'Final DB project', 100.0),
    (2, 4, 'HW1', 'Graduate homework 1', 25.0),
    (2, 5, 'Final', 'Comprehensive final', 150.0);

-- ----- Students -----
INSERT INTO Student (username, student_id, first_name, last_name)
VALUES
    ('cbacker', '1001', 'Caleb', 'Backer'),
    ('jdoe', '1002', 'John', 'Doe'),
    ('asmith', '1003', 'Alice', 'Smith');

-- ----- Enrollment -----
INSERT INTO Enrollment (class_id, username)
VALUES
    (1, 'cbacker'),
    (1, 'jdoe'),
    (1, 'asmith'),
    (2, 'cbacker');   -- Caleb is also enrolled in CS510

-- ----- Grades for CS410 -----
-- HW1
INSERT INTO Grade (assignment_id, username, grade_points)
VALUES
    (1, 'cbacker', 18.0),
    (1, 'jdoe',    20.0),
    (1, 'asmith',  16.0);

-- HW2
INSERT INTO Grade (assignment_id, username, grade_points)
VALUES
    (2, 'cbacker', 22.0),
    (2, 'jdoe',    25.0);
-- Note: asmith missing HW2 grade on purpose

-- Midterm
INSERT INTO Grade (assignment_id, username, grade_points)
VALUES
    (3, 'cbacker', 88.0),
    (3, 'jdoe',    75.0),
    (3, 'asmith',  92.0);

-- Project
INSERT INTO Grade (assignment_id, username, grade_points)
VALUES
    (4, 'cbacker', 95.0),
    (4, 'jdoe',    90.0);
-- asmith missing Project grade
