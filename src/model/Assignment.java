package model;

public class Assignment {
    private int assignmentId;
    private int classId;
    private int categoryId;
    private String name;
    private String description;
    private double points;

    public Assignment(int assignmentId, int classId, int categoryId,
                      String name, String description, double points) {
        this.assignmentId = assignmentId;
        this.classId = classId;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.points = points;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public int getClassId() {
        return classId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return "[" + assignmentId + "] " + name + " (" + points + " pts) - " + description;
    }
}
