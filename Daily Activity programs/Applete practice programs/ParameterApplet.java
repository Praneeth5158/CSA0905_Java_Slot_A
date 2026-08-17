import java.applet.Applet;
import java.awt.*;

public class ParameterApplet extends Applet {

    String name, age;

    public void init() {
        name = getParameter("name");
        age = getParameter("age");
    }

    public void paint(Graphics g) {
        g.setColor(Color.blue);
        g.drawString("Name: " + name, 100, 100);

        g.setColor(Color.red);
        g.drawString("Age: " + age, 100, 140);
    }
}