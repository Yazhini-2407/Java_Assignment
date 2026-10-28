
import java.sql.*;

public class StudentDAO {

    public void addStudent(String roll, String name,
                           String dept, int semester) {
        String sql = "INSERT INTO students " +
                "(roll_no, student_name, department, current_semester) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, roll);
            ps.setString(2, name);
            ps.setString(3, dept);
            ps.setInt(4, semester);
            ps.executeUpdate();

            System.out.println("Student added successfully!");

        } catch (SQLException e) {
            System.out.println("Student error: " + e.getMessage());
        }
    }

    public void addCourse(String code, String title,
                          int credits, String type) {
        String sql = "INSERT INTO courses " +
                "(course_code, title, credits, course_type) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code);
            ps.setString(2, title);
            ps.setInt(3, credits);
            ps.setString(4, type.toUpperCase());
            ps.executeUpdate();

            System.out.println("Course added successfully!");

        } catch (SQLException e) {
            System.out.println("Course error: " + e.getMessage());
        }
    }

    public void viewStudents() {
        String sql = "SELECT * FROM students ORDER BY roll_no";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n--- STUDENT DETAILS ---");

            while (rs.next()) {
                System.out.println(
                    rs.getString("roll_no") + " | " +
                    rs.getString("student_name") + " | " +
                    rs.getString("department") + " | Semester " +
                    rs.getInt("current_semester")
                );
            }

        } catch (SQLException e) {
            System.out.println("View error: " + e.getMessage());
        }
    }
}
