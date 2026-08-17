import java.applet.Applet;
import java.awt.*;

public class SampleApplet extends Applet {
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawString("CALCULATOR", 100, 50);

        g.setColor(Color.RED);
        g.drawString("Value1 = 10", 50, 100);

        g.setColor(Color.BLACK);
        g.drawString("Value2 = 22", 50, 130);

        g.setColor(Color.GREEN);
        g.drawString("Addition = 32", 50, 160);

        g.setColor(Color.BLUE);
        g.drawString("Subtraction = -12", 50, 190);
    }
}