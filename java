import java.sql.*;
import java.util.Scanner;

public class ToDoListApp {

    static final String DB_URL = "jdbc:mysql://localhost:3306/todo_db";
    static final String DB_USER = "root";
    static final String DB_PASS = ""; // Change if your MySQL password is not empty

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- To-Do List ---");
            System.out.println("1. Add Task");
            System.out.println("2. View Tasks");
            System.out.println("3. Mark as Completed");
            System.out.println("4. Delete Task");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine(); // Clear buffer

            switch (choice) {
                case 1 -> addTask(sc);
                case 2 -> viewTasks();
                case 3 -> markCompleted(sc);
                case 4 -> deleteTask(sc);
                case 5 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    static void addTask(Scanner sc) {
        System.out.print("Enter task: ");
        String taskText = sc.nextLine();
        String sql = "INSERT INTO tasks (task_text) VALUES (?)";

        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, taskText);
            stmt.executeUpdate();
            System.out.println("Task added.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void viewTasks() {
        String sql = "SELECT * FROM tasks";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nYour Tasks:");
            while (rs.next()) {
                String status = rs.getBoolean("is_completed") ? "[✔]" : "[ ]";
                System.out.printf("%d. %s %s\n", rs.getInt("task_id"), status, rs.getString("task_text"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void markCompleted(Scanner sc) {
        System.out.print("Enter task ID to mark as completed: ");
        int id = sc.nextInt();
        String sql = "UPDATE tasks SET is_completed = TRUE WHERE task_id = ?";

        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int updated = stmt.executeUpdate();
            if (updated > 0) System.out.println("Task marked as completed.");
            else System.out.println("Task not found.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void deleteTask(Scanner sc) {
        System.out.print("Enter task ID to delete: ");
        int id = sc.nextInt();
        String sql = "DELETE FROM tasks WHERE task_id = ?";

        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int deleted = stmt.executeUpdate();
            if (deleted > 0) System.out.println("Task deleted.");
            else System.out.println("Task not found.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
