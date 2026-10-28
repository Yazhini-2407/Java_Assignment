
import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StudentDAO studentDAO = new StudentDAO();
        AcademicDAO academicDAO = new AcademicDAO(sc);

        while (true) {
            System.out.println("\n======================================");
            System.out.println(" KEC R2024 ACADEMIC EVALUATION SYSTEM");
            System.out.println("======================================");
            System.out.println("1. Register Student");
            System.out.println("2. Add Course");
            System.out.println("3. View Students");
            System.out.println("4. Enroll Student in Course");
            System.out.println("5. Update Attendance");
            System.out.println("6. Approve Medical Condonation");
            System.out.println("7. Enter / Update Assessment Marks");
            System.out.println("8. Generate Student Mark Sheet");
            System.out.println("9. Attendance Defaulter Report");
            System.out.println("10. Department Result Summary");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine();

            try {
                switch (choice) {
                    case "1":
                        System.out.print("Roll number: ");
                        String roll = sc.nextLine();

                        System.out.print("Student name: ");
                        String name = sc.nextLine();

                        System.out.print("Department: ");
                        String dept = sc.nextLine();

                        System.out.print("Current semester: ");
                        int sem = Integer.parseInt(sc.nextLine());

                        if (sem < 1 || sem > 8) {
                            System.out.println("Enter semester 1-8.");
                            break;
                        }

                        studentDAO.addStudent(roll, name, dept, sem);
                        break;

                    case "2":
                        System.out.print("Course code: ");
                        String code = sc.nextLine();

                        System.out.print("Course title: ");
                        String title = sc.nextLine();

                        System.out.print("Credits: ");
                        int credits = Integer.parseInt(sc.nextLine());

                        System.out.print("Course type (THEORY/PRACTICAL/EMBEDDED): ");
                        String type = sc.nextLine().toUpperCase();

                        if (credits <= 0 ||
                            !(type.equals("THEORY") ||
                              type.equals("PRACTICAL") ||
                              type.equals("EMBEDDED"))) {
                            System.out.println("Invalid credits or course type.");
                            break;
                        }

                        studentDAO.addCourse(code, title, credits, type);
                        break;

                    case "3":
                        studentDAO.viewStudents();
                        break;

                    case "4":
                        academicDAO.enrollStudent();
                        break;

                    case "5":
                        academicDAO.updateAttendance();
                        break;

                    case "6":
                        academicDAO.approveMedicalCondonation();
                        break;

                    case "7":
                        academicDAO.enterMarks();
                        break;

                    case "8":
                        academicDAO.printMarkSheet();
                        break;

                    case "9":
                        academicDAO.attendanceDefaulters();
                        break;

                    case "10":
                        academicDAO.classSummary();
                        break;

                    case "0":
                        System.out.println("Application closed.");
                        sc.close();
                        return;

                    default:
                        System.out.println("Invalid choice. Try again.");
                }

            } catch (Exception e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        }
    }
}
