// model/Student.java
// Represents a student

package model;

public class Student {
    private String username;
    private String studentId;
    private String firstName;
    private String lastName;

    public Student(String username, String studentId, String firstName, String lastName) {
        this.username = username;
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername()  { return username; }
    public String getStudentId() { return studentId; }
    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }

    @Override
    public String toString() {
        return username + " (" + studentId + ") " + lastName + ", " + firstName;
    }
}
