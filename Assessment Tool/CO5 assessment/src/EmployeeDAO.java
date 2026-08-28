import java.sql.*;

public class EmployeeDAO {

    // ADD
    public boolean insert(Employee e) {
        String sql = "INSERT INTO employee VALUES(?,?,?,?,?,?,?,?,?)";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, e.id);
            ps.setString(2, e.name);
            ps.setString(3, e.gender);
            ps.setString(4, e.department);
            ps.setString(5, e.designation);
            ps.setDouble(6, e.basicSalary);
            ps.setDouble(7, e.allowances);
            ps.setDouble(8, e.deductions);
            ps.setString(9, e.status);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // SEARCH
    public Employee search(int id) {
        String sql = "SELECT * FROM employee WHERE id=?";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Employee(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("gender"),
                        rs.getString("department"),
                        rs.getString("designation"),
                        rs.getDouble("basic_salary"),
                        rs.getDouble("allowances"),
                        rs.getDouble("deductions"),
                        rs.getString("status")
                );
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return null;
    }

    // UPDATE
    public boolean update(Employee e) {
        String sql = "UPDATE employee SET name=?, gender=?, department=?, designation=?, basic_salary=?, allowances=?, deductions=?, status=? WHERE id=?";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, e.name);
            ps.setString(2, e.gender);
            ps.setString(3, e.department);
            ps.setString(4, e.designation);
            ps.setDouble(5, e.basicSalary);
            ps.setDouble(6, e.allowances);
            ps.setDouble(7, e.deductions);
            ps.setString(8, e.status);
            ps.setInt(9, e.id);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // DELETE
    public boolean delete(int id) {
        String sql = "DELETE FROM employee WHERE id=?";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // VIEW ALL
    public String viewAll() {
        String result = "";

        String sql = "SELECT * FROM employee";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result += "ID: " + rs.getInt("id") + "\n";
                result += "Name: " + rs.getString("name") + "\n";
                result += "Gender: " + rs.getString("gender") + "\n";
                result += "Department: " + rs.getString("department") + "\n";
                result += "Designation: " + rs.getString("designation") + "\n";
                result += "Basic Salary: " + rs.getDouble("basic_salary") + "\n";
                result += "Allowances: " + rs.getDouble("allowances") + "\n";
                result += "Deductions: " + rs.getDouble("deductions") + "\n";
                result += "Status: " + rs.getString("status") + "\n";
                result += "-----------------------------\n";
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return result;
    }
}