import Dijkstra.Vertex;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.util.LinkedList;

public class Person {

    public Point2d location;
    private double size = 5.0;
    public Point2d goal;
    public boolean goalSet;
    public LinkedList<Vertex> goalList;
    public double desiredSpeed;
    public Vector2d currentVelocity;

    public Person(double x1, double y1) {
        location = new Point2d(x1, y1);
        goalSet = false;
        goalList = new LinkedList<Vertex>();
        desiredSpeed = 10;
        currentVelocity = new Vector2d(0, 0);
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

    public Vector2d desiredMotion() {
        Vertex nextGoal = goalList.get(1);
        Vector2d currentVector = new Vector2d(location);
        Vector2d nextGoalVector = new Vector2d(nextGoal.getX(), nextGoal.getY());
        nextGoalVector.sub(currentVector);
        double length = nextGoalVector.length();
        nextGoalVector.scale(1 / length);
        nextGoalVector.scale(desiredSpeed);
        nextGoalVector.sub(currentVelocity);
        nextGoalVector.scale(1 / 4.0);
        return nextGoalVector;
    }

}
