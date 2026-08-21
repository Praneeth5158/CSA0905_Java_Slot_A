import java.awt.*;
import java.awt.event.*;

public class BorderExample extends Frame implements ActionListener {

    Button north, south, east, west, center;
    Label result;

    BorderExample() {

        setTitle("Border Layout Example");
        setSize(500, 400);
        setLayout(new BorderLayout(10, 10));

        north = new Button("North");
        south = new Button("South");
        east = new Button("East");
        west = new Button("West");
        center = new Button("Click Me");

        result = new Label("Click any button", Label.CENTER);

        add(north, BorderLayout.NORTH);
        add(south, BorderLayout.SOUTH);
        add(east, BorderLayout.EAST);
        add(west, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);

        north.addActionListener(this);
        south.addActionListener(this);
        east.addActionListener(this);
        west.addActionListener(this);
        center.addActionListener(this);

        add(result, BorderLayout.AFTER_LAST_LINE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        result.setText(e.getActionCommand() + " button clicked");
    }

    public static void main(String[] args) {
        new BorderExample();
    }
}