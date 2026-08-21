import java.awt.*;
import java.awt.event.*;

public class GridExample extends Frame implements ActionListener {

    Button b1, b2, b3, b4;
    Label result;

    GridExample() {
        setTitle("Grid Layout Example");
        setSize(400, 300);
        setLayout(new GridLayout(3, 2, 10, 10));

        b1 = new Button("Button 1");
        b2 = new Button("Button 2");
        b3 = new Button("Button 3");
        b4 = new Button("Button 4");

        result = new Label("Click a button", Label.CENTER);

        add(b1);
        add(b2);
        add(b3);
        add(b4);
        add(result);

        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);
        b4.addActionListener(this);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        result.setText(e.getActionCommand() + " clicked");
    }

    public static void main(String[] args) {
        new GridExample();
    }
}