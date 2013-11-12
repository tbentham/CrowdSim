import Dijkstra.Vertex;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.LinkedList;

public class Person {

    private Point2d location;
    private double size;
    private LinkedList<Vertex> goalList;

    private double desiredSpeed;
    private Vector2d actualVelocity;

    public Person(double x1, double y1) {
        location = new Point2d(x1, y1);
        size = 4.0;

        goalList = new LinkedList<Vertex>();

        desiredSpeed = 5.0;
        actualVelocity = new Vector2d(0, 0);
    }

    private void goalUpdate() {
        while (goalList.size() > 0 && location.distance(goalList.get(0).toPoint2d()) < (size / 2.0))
            goalList.remove(0);
    }

    public Point2d advance(ArrayList<Person> people) {
    	goalUpdate();
        
        if (goalList.size() > 0) {
            actualVelocity.add(desiredAcceleration());
        
            for (Person p : people) {
            	if (this != p)
            		actualVelocity.add(socialForce(p));
            }
        
            location.add(actualVelocity);
        }

        goalUpdate();
        
        return location;
    }

    public Vector2d desiredAcceleration() {
        // get next goal on path
        Vector2d v = getDesiredDirection();

        // calculate desired velocity
        v.scale(desiredSpeed);

        // calculate acceleration term
        v.sub(actualVelocity);
        v.scale(1.0 / 4.0);

        return v;
    }

    public Vector2d getDesiredDirection() {
        Vector2d v = new Vector2d(getNextGoal());
        v.sub(new Vector2d(location));
        if (v.length() != 0.0)
            v.scale(1.0 / v.length());
        return v;
    }

    public double getNextSpeed() {
        goalUpdate();
        if (goalList.size() == 0)
        	return 0;
        Vector2d nextVelocity = new Vector2d(actualVelocity);
        nextVelocity.add(desiredAcceleration());
        return nextVelocity.length();
    }

    public Vector2d socialForce(Person bPerson) {
    	double d = 2.1 * Math.exp((-b(bPerson)) / 0.3);
//      double d = 2.0 / b(bPerson);
        
        Vector2d aVector = new Vector2d(this.location);
        aVector.sub(new Vector2d(bPerson.getLocation()));
        
        aVector.normalize();
        aVector.scale(d);
        
        Vector2d direction = new Vector2d(actualVelocity);
        direction.normalize();       
        if (direction.dot(aVector) < aVector.length() * Math.cos(100 * Math.PI / 180))
        	aVector.scale(0.5);
        
        return aVector;
    }

    public double b(Person bPerson) {
        Vector2d aVector = new Vector2d(this.location);
        aVector.sub(new Vector2d(bPerson.getLocation()));
        
        Vector2d bVector = bPerson.getDesiredDirection();
        double bSpeed = bPerson.getNextSpeed();
        bVector.scale(bSpeed);
        Vector2d cVector = new Vector2d(aVector);
        cVector.sub(bVector);
        
        double squareRootMe = Math.pow(aVector.length() + cVector.length(), 2) - Math.pow(bSpeed, 2);
        return Math.sqrt(squareRootMe) / 2.0;
    }


/*
    public Vector2d obstacleAvoidance(Wall wall) {
        Vector2d aVector = new Vector2d(this.location);
        aVector.sub(new Vector2d(wall.nearestPoint(this)));

        double d = 10 * Math.exp(-aVector.length() / 0.2);
        
        aVector.normalize();
        aVector.scale(d);
        
        return aVector;
    }
*/

    public Point2d getNextGoal() {
        if (goalList.size() > 0)
            return goalList.get(0).toPoint2d();
        return location;
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

    public double getSize() {
        return size;
    }

    public Vector2d getVelocity() {
        return actualVelocity;
    }

    public double getSpeed() {
        return actualVelocity.length();
    }
}
