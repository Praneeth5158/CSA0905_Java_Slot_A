import java.applet.Applet;
import java.awt.*;

public class ColorShape extends Applet {

    public void paint(Graphics g) {

        g.setColor(Color.red);
        g.fillRect(50, 50, 100, 60);

        g.setColor(Color.blue);
        g.fillOval(200, 50, 100, 60);

        g.setColor(Color.green);
        g.fillRect(50, 150, 100, 60);

        g.setColor(Color.yellow);
        g.fillOval(200, 150, 100, 60);

        g.setColor(Color.black);
        g.drawString("Colours and Shapes", 120, 250);
    }
}
