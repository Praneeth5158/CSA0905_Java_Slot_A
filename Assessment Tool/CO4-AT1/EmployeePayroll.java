import java.awt.*;
import java.awt.event.*;

public class EmployeePayroll extends Frame implements ActionListener {

    TextField id, name, salary, days;
    Choice department, designation, rating;
    TextArea report;
    Button generate, reset;

    EmployeePayroll() {

        setTitle("Employee Performance and Payroll");
        setSize(600, 600);
        setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);

        Label title = new Label("EMPLOYEE PAYROLL DASHBOARD");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        g.gridx = 0;
        g.gridy = 0;
        g.gridwidth = 2;
        add(title, g);

        g.gridwidth = 1;

        // Employee ID
        g.gridx = 0;
        g.gridy = 1;
        add(new Label("Employee ID:"), g);

        id = new TextField(15);
        g.gridx = 1;
        add(id, g);

        // Name
        g.gridx = 0;
        g.gridy = 2;
        add(new Label("Employee Name:"), g);

        name = new TextField(15);
        g.gridx = 1;
        add(name, g);

        // Department
        g.gridx = 0;
        g.gridy = 3;
        add(new Label("Department:"), g);

        department = new Choice();
        department.add("CSE");
        department.add("HR");
        department.add("Finance");
        department.add("Marketing");

        g.gridx = 1;
        add(department, g);

        // Designation
        g.gridx = 0;
        g.gridy = 4;
        add(new Label("Designation:"), g);

        designation = new Choice();
        designation.add("Manager");
        designation.add("Developer");
        designation.add("Tester");
        designation.add("Intern");

        g.gridx = 1;
        add(designation, g);

        // Salary
        g.gridx = 0;
        g.gridy = 5;
        add(new Label("Basic Salary:"), g);

        salary = new TextField(15);
        g.gridx = 1;
        add(salary, g);

        // Working Days
        g.gridx = 0;
        g.gridy = 6;
        add(new Label("Working Days:"), g);

        days = new TextField(15);
        g.gridx = 1;
        add(days, g);

        // Rating
        g.gridx = 0;
        g.gridy = 7;
        add(new Label("Performance Rating:"), g);

        rating = new Choice();
        rating.add("Excellent");
        rating.add("Good");
        rating.add("Average");
        rating.add("Poor");

        g.gridx = 1;
        add(rating, g);

        // Buttons
        generate = new Button("Generate Report");
        reset = new Button("Reset");

        g.gridx = 0;
        g.gridy = 8;
        add(generate, g);

        g.gridx = 1;
        add(reset, g);

        // Report
        report = new TextArea(8, 35);

        g.gridx = 0;
        g.gridy = 9;
        g.gridwidth = 2;
        add(report, g);

        generate.addActionListener(this);
        reset.addActionListener(this);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == generate) {

            if (id.getText().equals("") ||
                name.getText().equals("") ||
                salary.getText().equals("") ||
                days.getText().equals("")) {

                report.setText("Please fill all fields!");
                return;
            }

            try {

                double basic = Double.parseDouble(salary.getText());
                int workingDays = Integer.parseInt(days.getText());

                double gross = basic + (basic * 0.10);

                double bonus = 0;

                if (rating.getSelectedItem().equals("Excellent"))
                    bonus = basic * 0.20;
                else if (rating.getSelectedItem().equals("Good"))
                    bonus = basic * 0.10;
                else if (rating.getSelectedItem().equals("Average"))
                    bonus = basic * 0.05;

                double net = gross + bonus;

                report.setText(
                    "EMPLOYEE PAYROLL REPORT\n\n" +
                    "Employee ID   : " + id.getText() + "\n" +
                    "Name          : " + name.getText() + "\n" +
                    "Department    : " + department.getSelectedItem() + "\n" +
                    "Designation   : " + designation.getSelectedItem() + "\n" +
                    "Basic Salary  : " + basic + "\n" +
                    "Working Days  : " + workingDays + "\n" +
                    "Rating        : " + rating.getSelectedItem() + "\n\n" +
                    "Gross Salary  : " + gross + "\n" +
                    "Bonus         : " + bonus + "\n" +
                    "Net Salary    : " + net
                );

            } catch (NumberFormatException ex) {
                report.setText("Enter valid numbers for salary and working days!");
            }
        }

        if (e.getSource() == reset) {
            id.setText("");
            name.setText("");
            salary.setText("");
            days.setText("");
            report.setText("");
        }
    }

    public static void main(String[] args) {
        new EmployeePayroll();
    }
}