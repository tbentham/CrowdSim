import javafx.scene.canvas.GraphicsContext;

public class Person {

    public double xPos;
    public double yPos;
    public double size = 5.0;

    public Person(double x, double y) {
        xPos = x;
        yPos = y;
    }

    public void draw(GraphicsContext gc) {
        gc.fillOval(xPos - (size / 2.0), yPos - (size / 2.0), size, size);
    }


}
