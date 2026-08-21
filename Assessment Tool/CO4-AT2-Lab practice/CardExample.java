import java.awt.*;
import java.awt.event.*;

public class CardExample extends Frame implements ActionListener {

    CardLayout card;
    Panel panel;
    Button next, previous;

    CardExample() {
        setTitle("Card Layout Example");
        setSize(400, 300);
        setLayout(new BorderLayout());

        card = new CardLayout();
        panel = new Panel();
        panel.setLayout(card);

        Panel p1 = new Panel();
        p1.add(new Label("This is First Page"));

        Panel p2 = new Panel();
        p2.add(new Label("This is Second Page"));

        Panel p3 = new Panel();
        p3.add(new Label("This is Third Page"));

        panel.add(p1, "one");
        panel.add(p2, "two");
        panel.add(p3, "three");

        add(panel, BorderLayout.CENTER);

        Panel buttons = new Panel();

        previous = new Button("Previous");
        next = new Button("Next");

        buttons.add(previous);
        buttons.add(next);

        add(buttons, BorderLayout.SOUTH);

        previous.addActionListener(this);
        next.addActionListener(this);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == next)
            card.next(panel);
        else
            card.previous(panel);
    }

    public static void main(String[] args) {
        new CardExample();
    }
}