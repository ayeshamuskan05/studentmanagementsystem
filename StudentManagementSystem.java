import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Student {
    private int studentId;
    private String name;
    private List<Grade> grades;

    public Student(int studentId, String name) {
        this.studentId = studentId;
        this.name = name;
        this.grades = new ArrayList<>();
    }

    public int getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void addGrade(Grade grade) {
        grades.add(grade);
    }

    public void removeGrade(Grade grade) {
        grades.remove(grade);
    }

    public double calculateGPA() {
        if (grades.isEmpty()) return 0.0;
        double total = 0.0;
        for (Grade grade : grades) {
            total += grade.getScore();
        }
        return total / grades.size();
    }
}

class Course {
    private int courseId;
    private String courseName;

    public Course(int courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }
}

class Grade {
    private Course course;
    private double score;

    public Grade(Course course, double score) {
        this.course = course;
        this.score = score;
    }

    public Course getCourse() {
        return course;
    }

    public double getScore() {
        return score;
    }
}

public class StudentManagementSystem {
    private List<Student> students;
    private static final String STUDENTS_FILE = "students.txt";
    private static final String GRADES_FILE = "grades.txt";

    public StudentManagementSystem() {
        this.students = new ArrayList<>();
        loadStudentsFromFile();
        loadGradesFromFile();
    }

    private void loadStudentsFromFile() {
        try (Scanner scanner = new Scanner(new File(STUDENTS_FILE))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                int studentId = Integer.parseInt(parts[0]);
                String name = parts[1];
                Student student = new Student(studentId, name);
                students.add(student);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Students file not found. Creating a new one...");
        }
    }

    private void loadGradesFromFile() {
        try (Scanner scanner = new Scanner(new File(GRADES_FILE))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                int studentId = Integer.parseInt(parts[0]);
                int courseId = Integer.parseInt(parts[1]);
                String courseName = parts[2];
                double score = Double.parseDouble(parts[3]);
                Course course = new Course(courseId, courseName);
                Grade grade = new Grade(course, score);
                Student student = findStudentById(studentId);
                if (student != null) {
                    student.addGrade(grade);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Grades file not found. Creating a new one...");
        }
    }

    private void saveStudentsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(STUDENTS_FILE))) {
            for (Student student : students) {
                writer.println(student.getStudentId() + "," + student.getName());
            }
        } catch (IOException e) {
            System.out.println("Error saving students: " + e.getMessage());
        }
    }

    private void saveGradesToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(GRADES_FILE))) {
            for (Student student : students) {
                for (Grade grade : student.getGrades()) {
                    writer.println(student.getStudentId() + "," + grade.getCourse().getCourseId() + "," + grade.getCourse().getCourseName() + "," + grade.getScore());
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving grades: " + e.getMessage());
        }
    }

    public void addStudent(int studentId, String name) {
        students.add(new Student(studentId, name));
        saveStudentsToFile();
    }

    public void deleteStudent(int studentId) {
        Student student = findStudentById(studentId);
        if (student != null) {
            students.remove(student);
            saveStudentsToFile();
            saveGradesToFile();
        } else {
            System.out.println("Student not found.");
        }
    }

    public void updateStudentName(int studentId, String newName) {
        Student student = findStudentById(studentId);
        if (student != null) {
            student = new Student(studentId, newName); // Updating student name
            saveStudentsToFile();
        } else {
            System.out.println("Student not found.");
        }
    }

    public void displayAllStudents() {
        for (Student student : students) {
            System.out.println("ID: " + student.getStudentId() + ", Name: " + student.getName() + ", GPA: " + student.calculateGPA());
        }
    }

    private Student findStudentById(int studentId) {
        for (Student student : students) {
            if (student.getStudentId() == studentId) {
                return student;
            }
        }
        return null;
    }

    public void addGrade(int studentId, int courseId, String courseName, double score) {
        Student student = findStudentById(studentId);
        if (student != null) {
            Course course = new Course(courseId, courseName);
            Grade grade = new Grade(course, score);
            student.addGrade(grade);
            saveGradesToFile();
        } else {
            System.out.println("Student not found.");
        }
    }

    public static void main(String[] args) {
        StudentManagementSystem sms = new StudentManagementSystem();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Add Student");
            System.out.println("2. Delete Student");
            System.out.println("3. Update Student Name");
            System.out.println("4. Display All Students");
            System.out.println("5. Add Grade");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter student ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter student name: ");
                    String name = scanner.nextLine();
                    sms.addStudent(id, name);
                    break;
                case 2:
                    System.out.print("Enter student ID to delete: ");
                    int deleteId = scanner.nextInt();
                    sms.deleteStudent(deleteId);
                    break;
                case 3:
                    System.out.print("Enter student ID to update: ");
                    int updateId = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter new name: ");
                    String newName = scanner.nextLine();
                    sms.updateStudentName(updateId, newName);
                    break;
                case 4:
                    sms.displayAllStudents();
                    break;
                case 5:
                    System.out.print("Enter student ID: ");
                    int studentId = scanner.nextInt();
                    System.out.print("Enter course ID: ");
                    int courseId = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter course name: ");
                    String courseName = scanner.nextLine();
                    System.out.print("Enter grade: ");
                    double score = scanner.nextDouble();
                    sms.addGrade(studentId, courseId, courseName, score);
                    break;
                case 6:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}

