package Intent;

import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Student {
    int rollNo;
    String name;
    int[] marks = new int[5];
    String[] subjectGrades = new String[5];
    String overallGrade;

    public Student(int rollNo, String name, int[] marks) {
        this.rollNo = rollNo;
        this.name = name;
        this.marks = marks;
        this.subjectGrades = calculateSubjectGrades();
        this.overallGrade = calculateOverallGrade();
    }

    private String[] calculateSubjectGrades() {
        String[] grades = new String[5];
        for (int i = 0; i < 5; i++) {
            int m = marks[i];
            if (m >= 90) grades[i] = "A";
            else if (m >= 75) grades[i] = "B";
            else if (m >= 60) grades[i] = "C";
            else if (m >= 50) grades[i] = "D";
            else grades[i] = "Fail";
        }
        return grades;
    }

    private String calculateOverallGrade() {
        int total = 0;
        for (int m : marks) total += m;
        double avg = total / 5.0;

        if (avg >= 90) return "A";
        else if (avg >= 75) return "B";
        else if (avg >= 60) return "C";
        else if (avg >= 50) return "D";
        else return "Fail";
    }

    @Override
    public String toString() {
        return rollNo + "," + name + "," +
               marks[0] + "," + marks[1] + "," + marks[2] + "," + marks[3] + "," + marks[4] + "," +
               subjectGrades[0] + "," + subjectGrades[1] + "," + subjectGrades[2] + "," +
               subjectGrades[3] + "," + subjectGrades[4] + "," + overallGrade;
    }
}

public class test56756 extends JFrame implements ActionListener {
    JButton addBtn, updateBtn, deleteBtn, searchBtn, viewBtn;
    ArrayList<Student> students = new ArrayList<>();
    final String fileName = "students.txt";

    public test56756() {
        setTitle("Student Grading System");
        setSize(400, 300);
        setLayout(new GridLayout(5, 1, 10, 10));

        addBtn = new JButton("Add Student");
        updateBtn = new JButton("Update Student");
        deleteBtn = new JButton("Delete Student");
        searchBtn = new JButton("Search Student");
        viewBtn = new JButton("View All Students");

        add(addBtn);
        add(updateBtn);
        add(deleteBtn);
        add(searchBtn);
        add(viewBtn);

        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        searchBtn.addActionListener(this);
        viewBtn.addActionListener(this);

        loadFromFile();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addBtn) addStudent();
        else if (e.getSource() == updateBtn) updateStudent();
        else if (e.getSource() == deleteBtn) deleteStudent();
        else if (e.getSource() == searchBtn) searchStudent();
        else if (e.getSource() == viewBtn) viewAllStudents();
    }

    private void addStudent() {
        try {
            int roll = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Roll No:"));
            String name = JOptionPane.showInputDialog(this, "Enter Name:");
            int[] marks = new int[5];
            for (int i = 0; i < 5; i++) {
                marks[i] = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Marks for Subject " + (i + 1) + ":"));
            }
            Student s = new Student(roll, name, marks);
            students.add(s);
            saveToFile();
            JOptionPane.showMessageDialog(this,
                "Student Added Successfully!\nOverall Grade: " + s.overallGrade +
                "\nSubject Grades: " + Arrays.toString(s.subjectGrades));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error! " + ex.getMessage());
        }
    }

    private void updateStudent() {
        try {
            int roll = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Roll No to Update:"));
            boolean found = false;
            for (Student s : students) {
                if (s.rollNo == roll) {
                    String name = JOptionPane.showInputDialog(this, "Enter New Name:", s.name);
                    int[] marks = new int[5];
                    for (int i = 0; i < 5; i++) {
                        marks[i] = Integer.parseInt(JOptionPane.showInputDialog(this,
                                "Enter New Marks for Subject " + (i + 1) + ":", s.marks[i]));
                    }
                    s.name = name;
                    s.marks = marks;
                    s.subjectGrades = new Student(roll, name, marks).subjectGrades;
                    s.overallGrade = new Student(roll, name, marks).overallGrade;
                    saveToFile();
                    JOptionPane.showMessageDialog(this,
                        "Student Updated Successfully!\nNew Overall Grade: " + s.overallGrade +
                        "\nSubject Grades: " + Arrays.toString(s.subjectGrades));
                    found = true;
                    break;
                }
            }
            if (!found) JOptionPane.showMessageDialog(this, "Student Not Found!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error! " + ex.getMessage());
        }
    }

    private void deleteStudent() {
        try {
            int roll = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Roll No to Delete:"));
            boolean removed = students.removeIf(s -> s.rollNo == roll);
            if (removed) {
                saveToFile();
                JOptionPane.showMessageDialog(this, "Student Deleted Successfully!");
            } else JOptionPane.showMessageDialog(this, "Student Not Found!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error! " + ex.getMessage());
        }
    }

    private void searchStudent() {
        try {
            int roll = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Roll No to Search:"));
            for (Student s : students) {
                if (s.rollNo == roll) {
                    JOptionPane.showMessageDialog(this,
                            "Roll No: " + s.rollNo +
                            "\nName: " + s.name +
                            "\nMarks: " + Arrays.toString(s.marks) +
                            "\nSubject Grades: " + Arrays.toString(s.subjectGrades) +
                            "\nOverall Grade: " + s.overallGrade);
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Student Not Found!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error! " + ex.getMessage());
        }
    }

    private void viewAllStudents() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No Records Found!");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Student s : students) {
            sb.append("Roll No: ").append(s.rollNo)
              .append(", Name: ").append(s.name)
              .append(", Marks: ").append(Arrays.toString(s.marks))
              .append(", Subject Grades: ").append(Arrays.toString(s.subjectGrades))
              .append(", Overall Grade: ").append(s.overallGrade)
              .append("\n");
        }
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "All Students", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            for (Student s : students) pw.println(s.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error Saving File: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                int roll = Integer.parseInt(p[0]);
                String name = p[1];
                int[] marks = new int[5];
                for (int i = 0; i < 5; i++) marks[i] = Integer.parseInt(p[i + 2]);
                Student s = new Student(roll, name, marks);
                students.add(s);
            }
        } catch (IOException e) {
            // ignore if file not found
        }
    }

    public static void main(String[] args) {
        new test56756();
    }
}
