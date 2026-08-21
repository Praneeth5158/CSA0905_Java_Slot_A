import java.applet.Applet;
import java.awt.*;

public class SceneApplet extends Applet {

    public void paint(Graphics g) {

        // Sun
        g.setColor(Color.yellow);
        g.fillOval(450, 40, 60, 60);

        // Clouds
        g.setColor(Color.white);
        g.fillOval(80, 50, 80, 40);
        g.fillOval(130, 40, 80, 50);

        // Ground
        g.setColor(Color.green);
        g.fillRect(0, 250, 600, 150);

        // Road
        g.setColor(Color.gray);
        g.fillRect(0, 330, 600, 70);
        g.setColor(Color.white);
        g.drawLine(0, 365, 600, 365);

        // House
        g.setColor(Color.orange);
        g.fillRect(220, 170, 180, 150);

        // Roof
        g.setColor(Color.red);
        int x[] = {200, 310, 420};
        int y[] = {170, 80, 170};
        g.fillPolygon(x, y, 3);

        // Door
        g.setColor(Color.black);
        g.fillRect(285, 240, 50, 80);

        // Window
        g.setColor(Color.white);
        g.fillRect(240, 200, 40, 40);

        // Tree
        g.setColor(new Color(120, 70, 20));
        g.fillRect(70, 190, 30, 130);

        g.setColor(Color.green);
        g.fillOval(35, 140, 100, 90);

        // Another tree
        g.setColor(new Color(120, 70, 20));
        g.fillRect(480, 190, 30, 130);

        g.setColor(Color.green);
        g.fillOval(440, 140, 100, 90);
    }
}