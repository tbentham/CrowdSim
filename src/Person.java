import Dijkstra.Vertex;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.util.LinkedList;

public class Person {

    public Point2d location;
    private double size = 2.0;

    public Point2d goal;
    public boolean goalSet;
    public LinkedList<Vertex> goalList;

    public double desiredSpeed;
    public Vector2d currentVelocity;

    public Person(double x1, double y1) {
        location = new Point2d(x1, y1);
        goalSet = false;
        goalList = new LinkedList<Vertex>();
        desiredSpeed = 5;
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
        Vertex nextGoal = goalList.get(0);
        Vector2d nextGoalVector = new Vector2d(nextGoal.getX(), nextGoal.getY());

        Vector2d currentVector = new Vector2d(location);
        nextGoalVector.sub(currentVector);
        nextGoalVector.scale(desiredSpeed / nextGoalVector.length());

        nextGoalVector.sub(currentVelocity);
        nextGoalVector.scale(1 / 4.0);
        return nextGoalVector;
    }

    public void advance() {
        if (location.distance(new Point2d(goalList.get(0).getX(), goalList.get(0).getY())) < size)
            return;
        currentVelocity.add(desiredMotion());
        location.add(currentVelocity);
    }

}
