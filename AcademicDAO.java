
import java.sql.*;
import java.util.Scanner;

public class AcademicDAO {

    private final Scanner sc;

    public AcademicDAO(Scanner sc) {
        this.sc = sc;
    }

    public void enrollStudent() {
        try (Connection con = DBConnection.getConnection()) {
            System.out.print("Roll number: ");
            String roll = sc.nextLine();

            System.out.print("Course code: ");
            String course = sc.nextLine();

            System.out.print("Semester: ");
            int semester = Integer.parseInt(sc.nextLine());

            String sql = "INSERT INTO enrollments " +
                "(roll_no, course_code, semester) VALUES (?, ?, ?)";

            try (PreparedStatement ps = con.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, roll);
                ps.setString(2, course);
                ps.setInt(3, semester);
                ps.executeUpdate();

                int id;
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    id = keys.getInt(1);
                }

                try (PreparedStatement mark = con.prepareStatement(
                        "INSERT INTO assessment_marks (enrollment_id) VALUES (?)")) {
                    mark.setInt(1, id);
                    mark.executeUpdate();
                }
            }

            System.out.println("Enrollment successful!");

        } catch (Exception e) {
            System.out.println("Enrollment error: " + e.getMessage());
        }
    }

    public void updateAttendance() {
        try (Connection con = DBConnection.getConnection()) {
            System.out.print("Enrollment ID: ");
            int id = Integer.parseInt(sc.nextLine());

            System.out.print("Total classes held: ");
            int held = Integer.parseInt(sc.nextLine());

            System.out.print("Classes attended: ");
            int attended = Integer.parseInt(sc.nextLine());

            if (held < 0 || attended < 0 || attended > held) {
                System.out.println("Invalid attendance values.");
                return;
            }

            String sql = "UPDATE enrollments SET classes_held=?, " +
                    "classes_attended=? WHERE enrollment_id=?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, held);
                ps.setInt(2, attended);
                ps.setInt(3, id);

                if (ps.executeUpdate() == 0) {
                    System.out.println("Enrollment not found.");
                } else {
                    System.out.printf("Attendance: %.2f%%%n",
                            held == 0 ? 0.0 : attended * 100.0 / held);
                }
            }

        } catch (Exception e) {
            System.out.println("Attendance error: " + e.getMessage());
        }
    }

    public void approveMedicalCondonation() {
        try (Connection con = DBConnection.getConnection()) {
            System.out.print("Enrollment ID: ");
            int id = Integer.parseInt(sc.nextLine());

            String sql = "UPDATE enrollments SET medical_approved=TRUE " +
                    "WHERE enrollment_id=? AND classes_held>0 " +
                    "AND classes_attended*100.0/classes_held>=65 " +
                    "AND classes_attended*100.0/classes_held<75";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                int rows = ps.executeUpdate();
                System.out.println(rows == 1
                        ? "Medical condonation approved."
                        : "Not found or attendance is outside 65%-74.99%.");
            }

        } catch (Exception e) {
            System.out.println("Approval error: " + e.getMessage());
        }
    }

    public void enterMarks() {
        try (Connection con = DBConnection.getConnection()) {
            System.out.print("Enrollment ID: ");
            int id = Integer.parseInt(sc.nextLine());

            String find = "SELECT a.cia1, a.cia2, a.assignment_marks, " +
                    "a.ese_marks, a.ese_absent, e.classes_held, " +
                    "e.classes_attended, e.medical_approved " +
                    "FROM assessment_marks a JOIN enrollments e " +
                    "ON a.enrollment_id=e.enrollment_id " +
                    "WHERE a.enrollment_id=?";

            double old1, old2, oldAssignment, oldEse;
            boolean oldAbsent, approved;
            int held, attended;

            try (PreparedStatement ps = con.prepareStatement(find)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Enrollment not found.");
                        return;
                    }

                    old1 = rs.getDouble("cia1");
                    if (rs.wasNull()) old1 = -1;

                    old2 = rs.getDouble("cia2");
                    if (rs.wasNull()) old2 = -1;

                    oldAssignment = rs.getDouble("assignment_marks");
                    if (rs.wasNull()) oldAssignment = -1;

                    oldEse = rs.getDouble("ese_marks");
                    if (rs.wasNull()) oldEse = -1;

                    oldAbsent = rs.getBoolean("ese_absent");
                    approved = rs.getBoolean("medical_approved");
                    held = rs.getInt("classes_held");
                    attended = rs.getInt("classes_attended");
                }
            }

            double attendance = held == 0 ? 0 : attended * 100.0 / held;
            boolean eligible = attendance >= 75 ||
                    (attendance >= 65 && approved);

            System.out.printf("Attendance: %.2f%%%n", attendance);

            System.out.print("Update CIA 1? (y/n): ");
            if (sc.nextLine().equalsIgnoreCase("y")) {
                old1 = readMark("CIA 1 (0-100): ");
            }

            System.out.print("Update CIA 2? (y/n): ");
            if (sc.nextLine().equalsIgnoreCase("y")) {
                old2 = readMark("CIA 2 (0-100): ");
            }

            System.out.print("Update Assignment/Quiz/Lab? (y/n): ");
            if (sc.nextLine().equalsIgnoreCase("y")) {
                oldAssignment = readMark("Assignment (0-100): ");
            }

            System.out.print("Record ESE result? (y/n): ");
            if (sc.nextLine().equalsIgnoreCase("y")) {
                if (!eligible) {
                    System.out.println("Student is not eligible for ESE.");
                    System.out.println("ESE entry was not accepted.");
                } else {
                    System.out.print("Absent in ESE? (y/n): ");
                    oldAbsent = sc.nextLine().equalsIgnoreCase("y");

                    if (oldAbsent) {
                        oldEse = -1;
                    } else {
                        oldEse = readMark("ESE marks (0-100): ");
                    }
                }
            }

            String update = "UPDATE assessment_marks SET cia1=?, cia2=?, " +
                    "assignment_marks=?, ese_marks=?, ese_absent=? " +
                    "WHERE enrollment_id=?";

            try (PreparedStatement ps = con.prepareStatement(update)) {
                setOptionalMark(ps, 1, old1);
                setOptionalMark(ps, 2, old2);
                setOptionalMark(ps, 3, oldAssignment);
                setOptionalMark(ps, 4, oldEse);
                ps.setBoolean(5, oldAbsent);
                ps.setInt(6, id);
                ps.executeUpdate();
            }

            System.out.println("Marks saved successfully.");

        } catch (Exception e) {
            System.out.println("Marks error: " + e.getMessage());
        }
    }

    private double readMark(String prompt) {
        System.out.print(prompt);
        double mark = Double.parseDouble(sc.nextLine());
        if (mark < 0 || mark > 100) {
            throw new IllegalArgumentException("Marks must be 0-100.");
        }
        return mark;
    }

    private void setOptionalMark(PreparedStatement ps, int index,
                                 double mark) throws SQLException {
        if (mark < 0) {
            ps.setNull(index, Types.DECIMAL);
        } else {
            ps.setDouble(index, mark);
        }
    }

    private String grade(double total) {
        if (total >= 91) return "O";
        if (total >= 81) return "A+";
        if (total >= 71) return "A";
        if (total >= 61) return "B+";
        if (total >= 50) return "B";
        return "RA";
    }

    private double gradePoint(String grade) {
        switch (grade) {
            case "O": return 10;
            case "A+": return 9;
            case "A": return 8;
            case "B+": return 7;
            case "B": return 6;
            default: return 0;
        }
    }

    public void printMarkSheet() {
        System.out.print("Student roll number: ");
        String roll = sc.nextLine();

        String sql = "SELECT s.name, s.department, " +
                "c.course_code, c.title, c.credits, c.course_type, " +
                "e.enrollment_id, e.classes_held, e.classes_attended, " +
                "e.medical_approved, a.cia1, a.cia2, " +
                "a.assignment_marks, a.ese_marks, a.ese_absent " +
                "FROM students s JOIN enrollments e ON s.roll_no=e.roll_no " +
                "JOIN courses c ON c.course_code=e.course_code " +
                "JOIN assessment_marks a ON a.enrollment_id=e.enrollment_id " +
                "WHERE s.roll_no=? ORDER BY c.course_code";

        double points = 0;
        int creditsTotal = 0;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, roll);

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n===== SEMESTER MARK SHEET =====");
                boolean found = false;

                while (rs.next()) {
                    found = true;
                    int id = rs.getInt("enrollment_id");
                    String name = rs.getString("student_name");
                    String dept = rs.getString("department");
                    String code = rs.getString("course_code");
                    String title = rs.getString("title");
                    int credits = rs.getInt("credits");
                    String type = rs.getString("course_type");

                    double held = rs.getInt("classes_held");
                    double attended = rs.getInt("classes_attended");
                    double attendance = held == 0 ? 0 : attended * 100 / held;

                    double[] marks = new double[3];
                    String[] columns = {
                        "cia1", "cia2", "assignment_marks"
                    };
                    int count = 0;
                    double sum = 0;

                    for (String column : columns) {
                        double value = rs.getDouble(column);
                        if (!rs.wasNull()) {
                            sum += value;
                            count++;
                        }
                    }

                    double cia = count == 0 ? 0 : sum / count;
                    double ese = rs.getDouble("ese_marks");
                    boolean eseNull = rs.wasNull();
                    boolean absent = rs.getBoolean("ese_absent");
                    boolean approved = rs.getBoolean("medical_approved");

                    boolean eligible = attendance >= 75 ||
                            (attendance >= 65 && approved);

                    double ciaWeight, eseWeight, eseMinimum;
                    switch (type) {
                        case "PRACTICAL":
                            ciaWeight = 0.60;
                            eseWeight = 0.40;
                            eseMinimum = 50;
                            break;
                        case "EMBEDDED":
                            ciaWeight = 0.50;
                            eseWeight = 0.50;
                            eseMinimum = 45;
                            break;
                        default:
                            ciaWeight = 0.40;
                            eseWeight = 0.60;
                            eseMinimum = 45;
                    }

                    double total = cia * ciaWeight +
                            (eseNull || absent ? 0 : ese * eseWeight);

                    String result;
                    String finalGrade;

                    if (attendance < 65 ||
                            (attendance >= 65 && attendance < 75 && !approved)) {
                        result = "SA - Not eligible";
                        finalGrade = "SA";
                    } else if (absent) {
                        result = "AB";
                        finalGrade = "AB";
                    } else if (eseNull) {
                        result = "ESE Pending";
                        finalGrade = "Pending";
                    } else if ((type.equals("THEORY") && ese * 0.60 < 27) ||
                               (type.equals("PRACTICAL") && ese * 0.40 < 20) ||
                               (type.equals("EMBEDDED") && ese * 0.50 < 22.5) ||
                               total < 50) {
                        result = "FAIL";
                        finalGrade = "RA";
                    } else {
                        finalGrade = grade(total);
                        result = "PASS";
                    }

                    System.out.printf(
                        "%n%s | %s | %s | %s%n",
                        name, dept, code, title
                    );
                    System.out.printf(
                        "CIA Avg: %.2f | ESE: %s | Total: %.2f | Grade: %s | %s%n",
                        cia, eseNull ? "Pending" : absent ? "AB" :
                        String.format("%.2f", ese),
                        total, finalGrade, result
                    );

                    if (finalGrade.equals("Pending") ||
                            finalGrade.equals("SA") ||
                            finalGrade.equals("AB")) {
                        // Do not treat pending, SA or AB as earned credits.
                    } else {
                        points += credits * gradePoint(finalGrade);
                        creditsTotal += credits;
                    }
                }

                if (!found) {
                    System.out.println("No enrollment found for this roll number.");
                } else if (creditsTotal > 0) {
                    double sgpa = points / creditsTotal;
                    System.out.printf(
                        "%nSGPA: %.2f%nEquivalent Percentage: %.2f%%%n",
                        sgpa, sgpa * 10
                    );
                } else {
                    System.out.println("SGPA pending: no completed graded credits.");
                }
            }

        } catch (Exception e) {
            System.out.println("Report error: " + e.getMessage());
        }
    }

    public void attendanceDefaulters() {
        String sql = "SELECT s.roll_no, s.student_name, c.course_code, " +
                "e.classes_held, e.classes_attended " +
                "FROM students s JOIN enrollments e ON s.roll_no=e.roll_no " +
                "JOIN courses c ON c.course_code=e.course_code " +
                "WHERE e.classes_held>0 AND " +
                "e.classes_attended*100.0/e.classes_held<65";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n--- ATTENDANCE DEFAULTERS ---");

            while (rs.next()) {
                double pct = rs.getInt("classes_attended") * 100.0 /
                        rs.getInt("classes_held");

                System.out.printf("%s | %s | %s | %.2f%% | SA%n",
                        rs.getString("roll_no"),
                        rs.getString("student_name"),
                        rs.getString("course_code"), pct);
            }

        } catch (SQLException e) {
            System.out.println("Report error: " + e.getMessage());
        }
    }

    public void classSummary() {
        String sql = "SELECT s.department, COUNT(*) AS total, " +
                "SUM(CASE WHEN a.ese_absent=TRUE THEN 1 ELSE 0 END) AS absent " +
                "FROM students s JOIN enrollments e ON s.roll_no=e.roll_no " +
                "JOIN assessment_marks a ON a.enrollment_id=e.enrollment_id " +
                "GROUP BY s.department";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n--- DEPARTMENT RESULT SUMMARY ---");
            while (rs.next()) {
                System.out.printf("%s | Enrollments: %d | ESE Absentees: %d%n",
                        rs.getString("department"),
                        rs.getInt("total"), rs.getInt("absent"));
            }

            System.out.println(
                "Note: This summary counts enrollments and ESE absentees; " +
                "it does not yet calculate pass/fail counts or class-average SGPA."
            );

        } catch (SQLException e) {
            System.out.println("Summary error: " + e.getMessage());
        }
    }
}
