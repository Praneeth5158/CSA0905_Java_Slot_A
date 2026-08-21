import java.applet.Applet;
import java.awt.*;

public class SceneApplet extends Applet {

    public void paint(Graphics g) {

        // Sky
        g.setColor(Color.cyan);
        g.fillRect(0, 0, 600, 400);

        // Sun
        g.setColor(Color.yellow);
        g.fillOval(450, 40, 70, 70);

        // Clouds
        g.setColor(Color.white);
        g.fillOval(80, 50, 70, 40);
        g.fillOval(120, 40, 80, 50);
        g.fillOval(160, 50, 70, 40);

        // Ground
        g.setColor(Color.green);
        g.fillRect(0, 250, 600, 150);

        // Road
        g.setColor(Color.gray);
        g.fillRect(0, 320, 600, 80);

        // Road lines
        g.setColor(Color.white);
        for (int i = 0; i < 600; i += 80) {
            g.fillRect(i, 355, 40, 5);
        }

        // House body
        g.setColor(new Color(255, 200, 150));
        g.fillRect(220, 170, 180, 150);

        // Roof
        g.setColor(Color.red);
        int x[] = {200, 310, 420};
        int y[] = {170, 80, 170};
        g.fillPolygon(x, y, 3);

        // Door
        g.setColor(Color.darkGray);
        g.fillRect(285, 240, 50, 80);

        // Windows
        g.setColor(Color.white);
        g.fillRect(235, 205, 40, 40);
        g.fillRect(345, 205, 40, 40);

        // Window lines
        g.setColor(Color.black);
        g.drawLine(255, 205, 255, 245);
        g.drawLine(235, 225, 275, 225);
        g.drawLine(365, 205, 365, 245);
        g.drawLine(345, 225, 385, 225);

        // Tree 1
        g.setColor(new Color(120, 70, 20));
        g.fillRect(80, 200, 30, 120);

        g.setColor(Color.green);
        g.fillOval(40, 150, 90, 80);
        g.fillOval(70, 130, 90, 90);

        // Tree 2
        g.setColor(new Color(120, 70, 20));
        g.fillRect(480, 210, 30, 110);

        g.setColor(Color.green);
        g.fillOval(440, 160, 90, 80);
        g.fillOval(470, 140, 90, 90);

        // Small birds
        g.setColor(Color.black);
        g.drawLine(300, 50, 310, 45);
        g.drawLine(310, 45, 320, 50);

        g.drawLine(350, 70, 360, 65);
        g.drawLine(360, 65, 370, 70);
    }
}