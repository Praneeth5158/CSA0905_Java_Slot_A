import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

public class Calculator extends Applet implements ActionListener {

    TextField t1, t2, result;
    Button add, sub, mul, div;

    public void init() {
        t1 = new TextField(10);
        t2 = new TextField(10);
        result = new TextField(10);

        add = new Button("Add");
        sub = new Button("Sub");
        mul = new Button("Mul");
        div = new Button("Div");

        add(t1); add(t2);
        add(add); add(sub); add(mul); add(div);
        add(result);

        add.addActionListener(this);
        sub.addActionListener(this);
        mul.addActionListener(this);
        div.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        double a = Double.parseDouble(t1.getText());
        double b = Double.parseDouble(t2.getText());

        if(e.getSource() == add)
            result.setText("" + (a + b));
        else if(e.getSource() == sub)
            result.setText("" + (a - b));
        else if(e.getSource() == mul)
            result.setText("" + (a * b));
        else
            result.setText("" + (a / b));
    }
}