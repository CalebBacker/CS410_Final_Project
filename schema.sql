CREATE TABLE Class (
    class_id       INT AUTO_INCREMENT PRIMARY KEY,
    course_number  VARCHAR(20) NOT NULL,
    term           VARCHAR(10) NOT NULL,
    section        INT NOT NULL,
    description    VARCHAR(255),

    UNIQUE(course_number, term, section)
);

CREATE TABLE Category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    class_id    INT NOT NULL,
    name        VARCHAR(50) NOT NULL,
    weight      DECIMAL(5,2) NOT NULL,

    FOREIGN KEY (class_id) REFERENCES Class(class_id)
);

CREATE INDEX idx_category_classid ON Category(class_id);

CREATE TABLE Assignment (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    class_id      INT NOT NULL,
    category_id   INT NOT NULL,
    name          VARCHAR(100) NOT NULL,
    description   TEXT,
    points        DECIMAL(7,2) NOT NULL,

    UNIQUE(class_id, name),
    FOREIGN KEY (category_id) REFERENCES Category(category_id),
    FOREIGN KEY (class_id)    REFERENCES Class(class_id)
);

CREATE TABLE Student (
    username   VARCHAR(50) PRIMARY KEY,
    student_id VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(50),
    last_name  VARCHAR(50)
);

CREATE TABLE Enrollment (
    class_id INT NOT NULL,
    username VARCHAR(50) NOT NULL,

    PRIMARY KEY (class_id, username),
    FOREIGN KEY (class_id) REFERENCES Class(class_id),
    FOREIGN KEY (username) REFERENCES Student(username)
);

CREATE TABLE Grade (
    assignment_id INT NOT NULL,
    username      VARCHAR(50) NOT NULL,
    grade_points  DECIMAL(7,2),

    PRIMARY KEY (assignment_id, username),
    FOREIGN KEY (assignment_id) REFERENCES Assignment(assignment_id),
    FOREIGN KEY (username)      REFERENCES Student(username)
);
