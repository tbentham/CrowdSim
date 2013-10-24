import javafx.scene.canvas.GraphicsContext;

import javax.vecmath.Point2d;

public class Person {

    private Point2d location;
    private double size = 5.0;
    private Point2d goal;
    private boolean goalSet;

    public Person(double x1, double y1) {
        location = new Point2d(x1, y1);
        goalSet = false;
    }

    public void setGoal(double x1, double y1) {
        goal = new Point2d(x1, y1);
        goalSet = true;
    }

    public Point2d getGoal() {
        return goal;
    }

    public void setLocation(double x1, double y1) {
        location = new Point2d(x1, y1);
    }

    public Point2d getLocation() {
        return location;
    }

    public double getSize() {
        return size;
    }

    public boolean hasGoal() {
        return goalSet;
    }

    public void advance(double distanceToMove) throws NoGoalException {
        // TODO: Advance towards goal naively
    }

    public void draw(GraphicsContext gc) {
        double[] t = new double[2];
        location.get(t);
        gc.fillOval(t[0] - (size / 2.0), t[1] - (size / 2.0), size, size);
    }


}
