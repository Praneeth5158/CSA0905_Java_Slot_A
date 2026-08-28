public class Employee {
    int id;
    String name, gender, department, designation, status;
    double basicSalary, allowances, deductions;

    Employee(int id, String name, String gender, String department,
             String designation, double basicSalary, double allowances,
             double deductions, String status) {

        this.id = id;
        this.name = name;
        this.gender = gender;
        this.department = department;
        this.designation = designation;
        this.basicSalary = basicSalary;
        this.allowances = allowances;
        this.deductions = deductions;
        this.status = status;
    }
}