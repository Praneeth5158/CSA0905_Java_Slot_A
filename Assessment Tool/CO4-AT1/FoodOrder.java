import java.awt.*;
import java.awt.event.*;

public class FoodOrder extends Frame implements ActionListener {

    CardLayout card;
    Panel cards;

    TextField customer, table, quantity;
    Choice food;
    TextArea bill;

    Button next1, next2, previous, add, generate, clear;

    double total = 0;

    FoodOrder() {
        setTitle("Food Ordering and Billing");
        setSize(500, 400);

        card = new CardLayout();
        cards = new Panel(card);

        // FIRST SCREEN
        Panel p1 = new Panel(new FlowLayout());

        p1.add(new Label("Customer Name:"));
        customer = new TextField(15);
        p1.add(customer);

        p1.add(new Label("Table No:"));
        table = new TextField(5);
        p1.add(table);

        next1 = new Button("Next");
        p1.add(next1);

        // SECOND SCREEN
        Panel p2 = new Panel(new FlowLayout());

        p2.add(new Label("Food Item:"));

        food = new Choice();
        food.add("Pizza");
        food.add("Burger");
        food.add("Biryani");
        food.add("Coffee");
        p2.add(food);

        p2.add(new Label("Quantity:"));

        quantity = new TextField(5);
        p2.add(quantity);

        add = new Button("Add Item");
        previous = new Button("Previous");
        next2 = new Button("Next");

        p2.add(add);
        p2.add(previous);
        p2.add(next2);

        // THIRD SCREEN
        Panel p3 = new Panel(new FlowLayout());

        bill = new TextArea(12, 40);
        p3.add(bill);

        generate = new Button("Generate Bill");
        clear = new Button("Clear");

        p3.add(generate);
        p3.add(clear);

        cards.add(p1, "one");
        cards.add(p2, "two");
        cards.add(p3, "three");

        add(cards);

        next1.addActionListener(this);
        next2.addActionListener(this);
        previous.addActionListener(this);
        add.addActionListener(this);
        generate.addActionListener(this);
        clear.addActionListener(this);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == next1)
            card.show(cards, "two");

        if (e.getSource() == next2)
            card.show(cards, "three");

        if (e.getSource() == previous)
            card.show(cards, "one");

        if (e.getSource() == add) {

            try {
                int q = Integer.parseInt(quantity.getText());

                double price = 0;

                if (food.getSelectedItem().equals("Pizza"))
                    price = 250;
                else if (food.getSelectedItem().equals("Burger"))
                    price = 120;
                else if (food.getSelectedItem().equals("Biryani"))
                    price = 200;
                else
                    price = 80;

                double amount = price * q;
                total = total + amount;

                bill.append(food.getSelectedItem() +
                        " x " + q + " = Rs." + amount + "\n");

                quantity.setText("");

            } catch (Exception ex) {
                bill.setText("Enter valid quantity!");
            }
        }

        if (e.getSource() == generate) {

            double gst = total * 0.05;
            double finalAmount = total + gst;

            bill.append("\n----------------------");
            bill.append("\nSubtotal : Rs." + total);
            bill.append("\nGST 5%   : Rs." + gst);
            bill.append("\nFinal Bill: Rs." + finalAmount);
        }

        if (e.getSource() == clear) {
            customer.setText("");
            table.setText("");
            quantity.setText("");
            bill.setText("");
            total = 0;
        }
    }

    public static void main(String[] args) {
        new FoodOrder();
    }
}