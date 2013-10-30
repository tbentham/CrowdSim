import javax.vecmath.Point2d;

public class Person {

    public Point2d location;
    private double size = 5.0;
    public Point2d goal;
    public boolean goalSet;

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

}
