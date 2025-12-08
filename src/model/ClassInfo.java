// model/ClassInfo.java
package model;

public class ClassInfo {
    private int classId;
    private String courseNumber;
    private String term;
    private int section;
    private String description;

    public ClassInfo(int classId, String courseNumber, String term,
                     int section, String description) {
        this.classId = classId;
        this.courseNumber = courseNumber;
        this.term = term;
        this.section = section;
        this.description = description;
    }

    public int getClassId() { return classId; }
    public String getCourseNumber() { return courseNumber; }
    public String getTerm() { return term; }
    public int getSection() { return section; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return courseNumber + " " + term + " sec " + section +
                " (" + description + ") [id=" + classId + "]";
    }
}
