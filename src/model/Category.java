package model;

public class Category {
    private int categoryId;
    private int classId;
    private String name;
    private double weight;

    public Category(int categoryId, int classId, String name, double weight) {
        this.categoryId = categoryId;
        this.classId = classId;
        this.name = name;
        this.weight = weight;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getClassId() {
        return classId;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "[" + categoryId + "] " + name + " (weight=" + weight + ")";
    }
}
