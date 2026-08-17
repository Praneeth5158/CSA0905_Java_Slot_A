import java.applet.Applet;
import java.awt.Graphics;

public class LifeCycle extends Applet {

    public void init() {
        System.out.println("INIT");
    }

    public void start() {
        System.out.println("START");
    }

    public void paint(Graphics g) {
        g.drawString("Applet Life Cycle", 150, 100);
    }

    public void stop() {
        System.out.println("STOP");
    }

    public void destroy() {
        System.out.println("DESTROY");
    }
}