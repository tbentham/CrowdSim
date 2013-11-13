package WorldRepresentation;

import Dijkstra.Vertex;
import Exceptions.NaNException;
import Exceptions.PersonOverlapException;
import ForceModel.Model;

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
    public ArrayList<Point2d> locations;
    private Model forceModel;

    public Person(double x1, double y1) {
        locations = new ArrayList<Point2d>();
        location = new Point2d(x1, y1);
        size = 4.0;

        goalList = new LinkedList<Vertex>();

        desiredSpeed = 1.34;	// metres per second
        actualVelocity = new Vector2d(0, 0);

        forceModel = new Model();
    }

    private void goalUpdate() {
        while (goalList.size() > 0 && location.distance(goalList.get(0).toPoint2d()) < (size / 2.0))
            goalList.remove(0);
    }

    public Point2d advance(World world, ArrayList<Person> people, double timeStep) throws NaNException,
            PersonOverlapException {
        goalUpdate();

        if (goalList.size() > 0) {
            actualVelocity.add(desiredAcceleration());

            for (Person p : people) {
                if (this != p)
                    actualVelocity.add(forceModel.socialForce(this, p, timeStep));
            }

            for (Wall wall : world.getWalls()) {
                actualVelocity.add(forceModel.obstacleAvoidance(this, wall));

            }

            Vector2d motion = new Vector2d(actualVelocity);

            motion.scale(timeStep);

            location.add(motion);

            goalUpdate();
        }
        locations.add(new Point2d(location.x, location.y));

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

    public void setActualVelocity(Vector2d velocity) {
        actualVelocity = velocity;
    }
}