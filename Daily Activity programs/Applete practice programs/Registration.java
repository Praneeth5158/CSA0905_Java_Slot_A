import java.awt.*;
import java.awt.event.*;

public class Registration extends Frame implements ActionListener {

    TextField name, email, password;
    Checkbox male, female;
    Choice course;
    Button register;

    Registration() {
        setTitle("Registration Form");
        setSize(400, 400);
        setLayout(new FlowLayout());

        add(new Label("Registration Form"));

        add(new Label("Name:"));
        name = new TextField(20);
        add(name);

        add(new Label("Email:"));
        email = new TextField(20);
        add(email);

        add(new Label("Password:"));
        password = new TextField(20);
        add(password);

        add(new Label("Gender:"));

        CheckboxGroup cg = new CheckboxGroup();

        male = new Checkbox("Male", cg, true);
        female = new Checkbox("Female", cg, false);

        add(male);
        add(female);

        add(new Label("Course:"));

        course = new Choice();
        course.add("CSE");
        course.add("ECE");
        course.add("EEE");
        course.add("MECH");

        add(course);

        register = new Button("Register");
        register.addActionListener(this);
        add(register);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("Registration Successful");
    }

    public static void main(String[] args) {
        new Registration();
    }
}