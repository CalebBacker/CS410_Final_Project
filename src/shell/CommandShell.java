package shell;

import db.*;
import model.ClassInfo;
import model.Student;
import model.Category;
import model.Assignment;

import java.sql.*;
import java.util.List;
import java.util.Scanner;

public class CommandShell {

    private final ClassDAO classDAO = new ClassDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final AssignmentDAO assignmentDAO = new AssignmentDAO();
    private final GradeDAO gradeDAO = new GradeDAO();

    private Integer activeClassId = null;
    private ClassInfo activeClass = null;

    public void run() {
        System.out.println("Grade Manager Shell. Type 'help' for commands, 'quit' to exit.");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) break;
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                handleCommand(line);
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                e.printStackTrace(System.out);
            }
        }

        System.out.println("Goodbye.");
    }

    private void handleCommand(String line) throws Exception {
        String[] parts = splitCommand(line);
        if (parts.length == 0) return;

        String cmd = parts[0];

        switch (cmd) {
            case "help" -> printHelp();
            case "new-class" -> cmdNewClass(parts);
            case "list-classes" -> cmdListClasses();
            case "select-class" -> cmdSelectClass(parts);
            case "show-class" -> cmdShowClass();
            case "add-student" -> cmdAddStudent(parts);
            case "show-students" -> cmdShowStudents(parts);
            case "show-categories" -> cmdShowCategories();
            case "add-category" -> cmdAddCategory(parts);
            case "show-assignments" -> cmdShowAssignments();
            case "show-assignment" -> cmdShowAssignments(); // alias for spec
            case "add-assignment" -> cmdAddAssignment(parts);
            case "grade" -> cmdGrade(parts);
            case "student-grades" -> cmdStudentGrades(parts);
            case "gradebook" -> cmdGradebook();
            default -> System.out.println("Unknown command: " + cmd);
        }
    }

    // Very simple splitter
    private String[] splitCommand(String line) {
        return line.split("\\s+");
    }

    private void printHelp() {
        System.out.println("""
            Commands:
              new-class course term section Description
              list-classes
              select-class course [term] [section]
              show-class
              add-student username studentid Last First
              add-student username
              show-students [substring]

              show-categories
              add-category Name weight

              show-assignments
              add-assignment name CategoryName Description points

              grade assignmentName username points
              student-grades username
              gradebook

              quit
            """);
    }

    // -------------------- Class Management --------------------

    private void cmdNewClass(String[] parts) throws SQLException {
        if (parts.length < 5) {
            System.out.println("Usage: new-class CS410 Sp25 1 Description");
            return;
        }
        String course = parts[1];
        String term = parts[2];
        int section = Integer.parseInt(parts[3]);
        StringBuilder sb = new StringBuilder();
        for (int i = 4; i < parts.length; i++) {
            if (i > 4) sb.append(' ');
            sb.append(parts[i]);
        }
        String description = sb.toString();

        ClassInfo ci = classDAO.createClass(course, term, section, description);
        System.out.println("Created class: " + ci);
    }

    private void cmdListClasses() throws SQLException {
        List<ClassInfo> classes = classDAO.listClassesWithStudentCount();
        for (ClassInfo ci : classes) {
            System.out.println(ci);
        }
    }

    private void cmdSelectClass(String[] parts) throws SQLException {
        if (parts.length < 2) {
            System.out.println("Usage: select-class course [term] [section]");
            return;
        }

        String course = parts[1];

        ClassInfo selected;
        if (parts.length == 2) {
            selected = classDAO.selectMostRecentByCourse(course);
        } else if (parts.length == 3) {
            String term = parts[2];
            selected = classDAO.selectByCourseAndTerm(course, term);
        } else {
            String term = parts[2];
            int section = Integer.parseInt(parts[3]);
            selected = classDAO.findSpecificClass(course, term, section);
        }

        if (selected == null) {
            System.out.println("Could not unambiguously select class.");
        } else {
            this.activeClass = selected;
            this.activeClassId = selected.getClassId();
            System.out.println("Active class set to: " + selected);
        }
    }

    private void cmdShowClass() {
        if (activeClass == null) {
            System.out.println("No active class. Use select-class first.");
        } else {
            System.out.println("Current class: " + activeClass);
        }
    }

    // -------------------- Student Management --------------------

    private void cmdAddStudent(String[] parts) throws SQLException {
        if (activeClassId == null) {
            System.out.println("No active class. Use select-class first.");
            return;
        }

        if (parts.length == 2) {
            String username = parts[1];
            Student s = studentDAO.findByUsername(username);
            if (s == null) {
                System.out.println("ERROR: student does not exist: " + username);
                return;
            }
            studentDAO.enrollStudentInClass(username, activeClassId);
            System.out.println("Enrolled existing student: " + s);
        } else if (parts.length == 5) {
            String username = parts[1];
            String studentId = parts[2];
            String last = parts[3];
            String first = parts[4];

            Student s = studentDAO.insertOrUpdateStudent(username, studentId, last, first);
            studentDAO.enrollStudentInClass(username, activeClassId);
            System.out.println("Added/enrolled student: " + s);
        } else {
            System.out.println("Usage: add-student username studentid Last First");
            System.out.println("   or: add-student username");
        }
    }

    private void cmdShowStudents(String[] parts) throws SQLException {
        if (activeClassId == null) {
            System.out.println("No active class. Use select-class first.");
            return;
        }

        List<Student> students;
        if (parts.length == 1) {
            students = studentDAO.listStudentsInClass(activeClassId);
        } else {
            String q = parts[1];
            students = studentDAO.searchStudentsInClass(activeClassId, q);
        }

        for (Student s : students) {
            System.out.println(s);
        }
    }

    // -------------------- Category & Assignment Management --------------------

    private void cmdShowCategories() throws SQLException {
        if (activeClassId == null) {
            System.out.println("No active class. Use select-class first.");
            return;
        }

        List<Category> cats = categoryDAO.listCategories(activeClassId);
        if (cats.isEmpty()) {
            System.out.println("No categories defined.");
            return;
        }
        System.out.println("Categories (for weight, raw, not yet rescaled to 100):");
        for (Category c : cats) {
            System.out.println("  " + c.getName() + "  weight=" + c.getWeight());
        }
    }

    private void cmdAddCategory(String[] parts) throws SQLException {
        if (activeClassId == null) {
            System.out.println("No active class. Use select-class first.");
            return;
        }
        if (parts.length < 3) {
            System.out.println("Usage: add-category Name weight");
            return;
        }
        String name = parts[1];
        double weight = Double.parseDouble(parts[2]);
        categoryDAO.addCategory(activeClassId, name, weight);
        System.out.println("Added category '" + name + "' with weight " + weight);
    }

    private void cmdShowAssignments() throws SQLException {
        if (activeClassId == null) {
            System.out.println("No active class. Use select-class first.");
            return;
        }

        List<Assignment> list = assignmentDAO.listAssignmentsByClass(activeClassId);
        if (list.isEmpty()) {
            System.out.println("No assignments defined.");
            return;
        }

        // Load categories to print names
        List<Category> categories = categoryDAO.listCategories(activeClassId);

        System.out.println("Assignments by category:");
        int currentCatId = -1;
        for (Assignment a : list) {
            if (a.getCategoryId() != currentCatId) {
                currentCatId = a.getCategoryId();
                String catName = "UnknownCategory";
                for (Category c : categories) {
                    if (c.getCategoryId() == currentCatId) {
                        catName = c.getName();
                        break;
                    }
                }

                System.out.println("[" + catName + "]");
            }
            System.out.println("  " + a.getName() + " (" + a.getPoints() + " pts) - " + a.getDescription());
        }
    }

    private void cmdAddAssignment(String[] parts) throws SQLException {
        if (activeClassId == null) {
            System.out.println("No active class. Use select-class first.");
            return;
        }
        if (parts.length < 5) {
            System.out.println("Usage: add-assignment name CategoryName Description points");
            return;
        }

        String name = parts[1];
        String categoryName = parts[2];
        // everything from parts[3] to parts[len-2] is description
        StringBuilder sb = new StringBuilder();
        for (int i = 3; i < parts.length - 1; i++) {
            if (i > 3) sb.append(' ');
            sb.append(parts[i]);
        }
        String description = sb.toString();
        double points = Double.parseDouble(parts[parts.length - 1]);

        Category cat = categoryDAO.findByName(activeClassId, categoryName);
        if (cat == null) {
            System.out.println("ERROR: category not found: " + categoryName);
            return;
        }

        assignmentDAO.addAssignment(activeClassId, cat.getCategoryId(), name, description, points);
        System.out.println("Added assignment '" + name + "' in category '" + categoryName + "'");
    }

    // -------------------- Grading --------------------

    private void cmdGrade(String[] parts) throws SQLException {
        if (activeClassId == null) {
            System.out.println("No active class. Use select-class first.");
            return;
        }
        if (parts.length < 4) {
            System.out.println("Usage: grade assignmentName username points");
            return;
        }

        String assignmentName = parts[1];
        String username = parts[2];
        double points = Double.parseDouble(parts[3]);

        // Check assignment exists
        Assignment assignment = assignmentDAO.findByName(activeClassId, assignmentName);
        if (assignment == null) {
            System.out.println("ERROR: assignment not found in current class: " + assignmentName);
            return;
        }

        // Check student exists + enrolled
        Student s = studentDAO.findByUsername(username);
        if (s == null) {
            System.out.println("ERROR: student does not exist: " + username);
            return;
        }
        if (!studentDAO.isEnrolledInClass(username, activeClassId)) {
            System.out.println("ERROR: student is not enrolled in this class: " + username);
            return;
        }

        if (points > assignment.getPoints()) {
            System.out.println("WARNING: grade points (" + points +
                    ") exceed assignment max (" + assignment.getPoints() + ")");
        }

        gradeDAO.setGrade(assignment.getAssignmentId(), username, points);
        System.out.println("Recorded grade " + points + " for " + username +
                " on " + assignmentName);
    }

    // -------------------- Grade Reporting --------------------

    private void cmdStudentGrades(String[] parts) throws SQLException {
        if (activeClassId == null) {
            System.out.println("No active class. Use select-class first.");
            return;
        }
        if (parts.length < 2) {
            System.out.println("Usage: student-grades username");
            return;
        }

        String username = parts[1];

        // Check student enrolled
        if (!studentDAO.isEnrolledInClass(username, activeClassId)) {
            System.out.println("ERROR: student is not enrolled in this class.");
            return;
        }

        // One query: per category, total/attempted earned/possible and weight
        String sql = """
                SELECT
                    cat.name AS category_name,
                    cat.weight,
                    SUM(COALESCE(g.grade_points, 0)) AS earned_all,
                    SUM(a.points) AS possible_all,
                    SUM(CASE WHEN g.grade_points IS NOT NULL THEN g.grade_points ELSE 0 END) AS earned_attempted,
                    SUM(CASE WHEN g.grade_points IS NOT NULL THEN a.points ELSE 0 END) AS possible_attempted
                FROM Category cat
                JOIN Assignment a ON a.category_id = cat.category_id
                LEFT JOIN Grade g
                    ON g.assignment_id = a.assignment_id
                   AND g.username = ?
                WHERE cat.class_id = ?
                GROUP BY cat.category_id, cat.name, cat.weight
                ORDER BY cat.name
                """;

        double totalWeightRaw = 0.0;
        double totalWeightedAll = 0.0;
        double totalWeightedAttempted = 0.0;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setInt(2, activeClassId);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("Grades for " + username + ":");
                while (rs.next()) {
                    String catName = rs.getString("category_name");
                    double weight = rs.getDouble("weight");
                    double earnedAll = rs.getDouble("earned_all");
                    double possibleAll = rs.getDouble("possible_all");
                    double earnedAtt = rs.getDouble("earned_attempted");
                    double possibleAtt = rs.getDouble("possible_attempted");

                    totalWeightRaw += weight;

                    double fracAll = (possibleAll > 0) ? (earnedAll / possibleAll) : 0.0;
                    double fracAtt = (possibleAtt > 0) ? (earnedAtt / possibleAtt) : 0.0;

                    totalWeightedAll += fracAll * weight;
                    totalWeightedAttempted += fracAtt * weight;

                    System.out.printf("  Category: %s (weight=%.2f)%n", catName, weight);
                    System.out.printf("    All      : %.2f / %.2f (%.2f%%)%n",
                            earnedAll, possibleAll, fracAll * 100.0);
                    System.out.printf("    Attempted: %.2f / %.2f (%.2f%%)%n",
                            earnedAtt, possibleAtt, fracAtt * 100.0);
                }
            }
        }

        if (totalWeightRaw > 0) {
            double scale = 100.0 / totalWeightRaw;  // rescale weights to sum to 100
            double finalAll = totalWeightedAll * scale / 100.0 * 100.0;          // already percent
            double finalAttempted = totalWeightedAttempted * scale / 100.0 * 100.0;

            System.out.printf("Overall total-grade    : %.2f%%%n", finalAll);
            System.out.printf("Overall attempted-grade: %.2f%%%n", finalAttempted);
        } else {
            System.out.println("No categories defined; cannot compute overall grade.");
        }
    }

    private void cmdGradebook() throws SQLException {
        if (activeClassId == null) {
            System.out.println("No active class. Use select-class first.");
            return;
        }

        // First, compute total raw weight in this class
        double totalWeightRaw = 0.0;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT SUM(weight) FROM Category WHERE class_id = ?")) {
            ps.setInt(1, activeClassId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalWeightRaw = rs.getDouble(1);
                }
            }
        }

        if (totalWeightRaw == 0.0) {
            System.out.println("No categories defined; cannot compute gradebook.");
            return;
        }

        double scale = 100.0 / totalWeightRaw;

        // Single query: per student aggregate weighted grades
        String sql = """
                SELECT
                    e.username,
                    s.student_id,
                    s.first_name,
                    s.last_name,
                    SUM(
                        COALESCE(g.grade_points, 0) / a.points * cat.weight
                    ) AS weighted_all,
                    SUM(
                        CASE
                            WHEN g.grade_points IS NOT NULL
                            THEN g.grade_points / a.points * cat.weight
                            ELSE 0
                        END
                    ) AS weighted_attempted
                FROM Enrollment e
                JOIN Student s ON s.username = e.username
                JOIN Category cat ON cat.class_id = e.class_id
                JOIN Assignment a ON a.category_id = cat.category_id
                LEFT JOIN Grade g
                    ON g.assignment_id = a.assignment_id
                   AND g.username = e.username
                WHERE e.class_id = ?
                GROUP BY e.username, s.student_id, s.first_name, s.last_name
                ORDER BY s.last_name, s.first_name
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activeClassId);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("Gradebook for class " + activeClass);
                System.out.println("username | student_id | name | total% | attempted%");
                while (rs.next()) {
                    String username = rs.getString("username");
                    String sid = rs.getString("student_id");
                    String first = rs.getString("first_name");
                    String last = rs.getString("last_name");
                    double weightedAll = rs.getDouble("weighted_all");
                    double weightedAttempted = rs.getDouble("weighted_attempted");

                    double finalAll = weightedAll * scale;        // already percentage basis
                    double finalAtt = weightedAttempted * scale;

                    System.out.printf("%s | %s | %s, %s | %.2f | %.2f%n",
                            username, sid, last, first, finalAll, finalAtt);
                }
            }
        }
    }
}
