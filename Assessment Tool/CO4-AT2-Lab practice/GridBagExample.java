import java.awt.*;
import java.awt.event.*;

public class GridBagLogin extends Frame implements ActionListener {

    TextField user, pass;
    Button login;
    Label msg;

    GridBagLogin() {
        setTitle("Login Form");
        setSize(400, 250);
        setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);

        Label l1 = new Label("Username:");
        Label l2 = new Label("Password:");

        user = new TextField(15);
        pass = new TextField(15);
        pass.setEchoChar('*');

        login = new Button("Login");
        msg = new Label("");

        g.gridx = 0;
        g.gridy = 0;
        add(l1, g);

        g.gridx = 1;
        add(user, g);

        g.gridx = 0;
        g.gridy = 1;
        add(l2, g);

        g.gridx = 1;
        add(pass, g);

        g.gridx = 1;
        g.gridy = 2;
        add(login, g);

        g.gridx = 0;
        g.gridy = 3;
        g.gridwidth = 2;
        add(msg, g);

        login.addActionListener(this);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (user.getText().equals("admin") &&
            pass.getText().equals("1234")) {
            msg.setText("Login Successful!");
        } else {
            msg.setText("Invalid Login!");
        }
    }

    public static void main(String[] args) {
        new GridBagLogin();
    }
}