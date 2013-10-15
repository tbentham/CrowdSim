import javafx.scene.canvas.GraphicsContext;

public class Person {

    private double xPos;
    private double yPos;
    private int zPos;    // i.e. floor
    private double size = 5.0;

    public Person(double x, double y, int z) {
        xPos = x;
        yPos = y;
        zPos = z;
    }

    public Person(double x, double y) {
        xPos = x;
        yPos = y;
        zPos = 0;
    }

    public double getPosX() {
        return xPos;
    }

    public double getPosY() {
        return yPos;
    }

    public int getPosZ() {
        return zPos;
    }

    public double getSize() {
        return size;
    }

    public void draw(GraphicsContext gc) {
        gc.fillOval(xPos - (size / 2.0), yPos - (size / 2.0), size, size);
    }


}
