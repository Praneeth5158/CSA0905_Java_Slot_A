import java.awt.*;
import java.awt.event.*;

public class EmployeePayroll extends Frame implements ActionListener {

    TextField id, name, basic, allowance, deduction;
    Choice gender, department, designation, status;
    TextArea output;

    Button add, search, update, delete, view, payroll, clear;

    EmployeeDAO dao = new EmployeeDAO();

    EmployeePayroll() {

        setTitle("Employee Performance and Payroll");
        setSize(650, 650);
        setLayout(new FlowLayout());

        add(new Label("Employee ID"));
        id = new TextField(20);
        add(id);

        add(new Label("Employee Name"));
        name = new TextField(20);
        add(name);

        add(new Label("Gender"));
        gender = new Choice();
        gender.add("Male");
        gender.add("Female");
        add(gender);

        add(new Label("Department"));
        department = new Choice();
        department.add("CSE");
        department.add("ECE");
        department.add("EEE");
        department.add("MECH");
        add(department);

        add(new Label("Designation"));
        designation = new Choice();
        designation.add("Manager");
        designation.add("Developer");
        designation.add("Tester");
        designation.add("HR");
        add(designation);

        add(new Label("Basic Salary"));
        basic = new TextField(20);
        add(basic);

        add(new Label("Allowances"));
        allowance = new TextField(20);
        add(allowance);

        add(new Label("Deductions"));
        deduction = new TextField(20);
        add(deduction);

        add(new Label("Status"));
        status = new Choice();
        status.add("Active");
        status.add("Inactive");
        add(status);

        add = new Button("Add");
        search = new Button("Search");
        update = new Button("Update");
        delete = new Button("Delete");
        view = new Button("View All");
        payroll = new Button("Payroll");
        clear = new Button("Clear");

        add(add);
        add(search);
        add(update);
        add(delete);
        add(view);
        add(payroll);
        add(clear);

        output = new TextArea(10, 55);
        add(output);

        add.addActionListener(this);
        search.addActionListener(this);
        update.addActionListener(this);
        delete.addActionListener(this);
        view.addActionListener(this);
        payroll.addActionListener(this);
        clear.addActionListener(this);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    public Employee getEmployee() {

        return new Employee(
                Integer.parseInt(id.getText()),
                name.getText(),
                gender.getSelectedItem(),
                department.getSelectedItem(),
                designation.getSelectedItem(),
                Double.parseDouble(basic.getText()),
                Double.parseDouble(allowance.getText()),
                Double.parseDouble(deduction.getText()),
                status.getSelectedItem()
        );
    }

    public void actionPerformed(ActionEvent e) {

        try {

            if (e.getSource() == add) {
                if (dao.insert(getEmployee()))
                    output.setText("Employee added successfully!");
            }

            else if (e.getSource() == search) {
                Employee x = dao.search(Integer.parseInt(id.getText()));

                if (x == null)
                    output.setText("Employee not found!");
                else {
                    name.setText(x.name);
                    basic.setText("" + x.basicSalary);
                    allowance.setText("" + x.allowances);
                    deduction.setText("" + x.deductions);
                    output.setText("Employee found!");
                }
            }

            else if (e.getSource() == update) {
                if (dao.update(getEmployee()))
                    output.setText("Employee updated successfully!");
                else
                    output.setText("Employee not found!");
            }

            else if (e.getSource() == delete) {
                if (dao.delete(Integer.parseInt(id.getText())))
                    output.setText("Employee deleted!");
                else
                    output.setText("Employee not found!");
            }

            else if (e.getSource() == view) {
                output.setText(dao.viewAll());
            }

            else if (e.getSource() == payroll) {

                double b = Double.parseDouble(basic.getText());
                double a = Double.parseDouble(allowance.getText());
                double d = Double.parseDouble(deduction.getText());

                double gross = b + a;
                double net = gross - d;

                output.setText(
                        "Basic Salary : " + b +
                                "\nAllowances   : " + a +
                                "\nDeductions   : " + d +
                                "\nGross Salary : " + gross +
                                "\nNet Salary   : " + net
                );
            }

            else if (e.getSource() == clear) {
                id.setText("");
                name.setText("");
                basic.setText("");
                allowance.setText("");
                deduction.setText("");
                output.setText("");
            }

        } catch (Exception ex) {
            output.setText("Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new EmployeePayroll();
    }
}