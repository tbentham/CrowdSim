import Dijkstra.Vertex;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.util.LinkedList;

public class Person {

    private Point2d location;
    private double radius = 2.0;

    private Point2d goal;
    private boolean goalSet;
    private LinkedList<Vertex> goalList;

    private double desiredSpeed;
    private Vector2d actualVelocity;

    public Person(double x1, double y1) {
    	location = new Point2d(x1, y1);
    	
        goalSet = false;
        goalList = new LinkedList<Vertex>();
        
        desiredSpeed = 5;
        actualVelocity = new Vector2d(0, 0);
    }

    public Vector2d desiredMotion() {
    	// get next goal on path
        Vector2d v = new Vector2d(goalList.get(0).getX(), goalList.get(0).getY());
        
        // calculate desired velocity
        v.sub(new Vector2d(location));
        v.scale(desiredSpeed / v.length());
        
        // calculate acceleration term
        v.sub(actualVelocity);
        v.scale(1 / 4.0);
        
        return v;
    }

    public Point2d advance() {
        if (location.distance(new Point2d(goalList.get(0).getX(), goalList.get(0).getY())) < radius)
            return location;
        actualVelocity.add(desiredMotion());
        location.add(actualVelocity);
        return location;
    }

    public void setGoal(double x1, double y1) {
        goal = new Point2d(x1, y1);
        goalSet = true;
    }

    public Point2d getGoal() {
        return goal;
    }

    public void setGoalList(LinkedList<Vertex> goalList) {
        this.goalList = goalList;
    }

    public LinkedList<Vertex> getGoalList() {
        return goalList;
    }

    public void setLocation(double x1, double y1) {
    	location = new Point2d(x1, y1);
    }

    public Point2d getLocation() {
        return location;
    }

    public double getRadius() {
        return radius;
    }

    public boolean isGoalSet() {
        return goalSet;
    }
}
