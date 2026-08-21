package LAYOUT;

import java.awt.*;
import javax.swing.*;

public class Border {
    public static void main(String[] args) {
        JFrame f = new JFrame("Border Layout");

        f.setLayout(new BorderLayout());

        f.add(new JButton("North"), BorderLayout.NORTH);
        f.add(new JButton("South"), BorderLayout.SOUTH);
        f.add(new JButton("East"), BorderLayout.EAST);
        f.add(new JButton("West"), BorderLayout.WEST);
        f.add(new JButton("Center"), BorderLayout.CENTER);

        f.setSize(400, 300);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}