package WorldRepresentation;

import Exceptions.NaNException;
import Exceptions.PersonOverlapException;
import Exceptions.RoutesNotComputedException;
import ForceModel.Model;
import NewDijkstra.Node;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.LinkedList;

// Represents the concept of a person in the simulation
public class Person {

    // Stores the x, y coordinates of this person
    public Point2d location;
    // Stores the position of each person for all past timesteps
    public ArrayList<Point2d> locations;
    // Records the floor that the person was on at each timestep
    public ArrayList<Integer> floors;
    // Represents the real life size and mass this person is representing
    private double size;
    private double mass;
    // Represents maxium walking speed
    private double desiredSpeed;
    // Variable used to work out acceleration
    private double relaxTime;
    // Stores current velocity as a vector
    private Vector2d actualVelocity;
    // Stores how long the person has been stuck on a wall
    public int stuckOnWallSince;
    // Represents the goal the person is heading towards with respect to the world
    private int goalID;
    // Stores the path this person is following
    private LinkedList<Node> goalList;
    // Stores the next goal in the list the person is aiming for
    private int goalIndex;
    // Stores the current floor the person is on
    public int floor;
    // Stores whether the person was blocked for each timestep
    public ArrayList<Boolean> blockedList;
    // Stores a reference to the force model so it can be called upon for computation
    private Model forceModel;
    // Records distance to the next goal and expected time to get there in order to work out if someone is stuck
    public double distanceToNextGoal;
    public double expectedTimeStepAtNextGoal;
    // Stores the last timestep that the person had A* performed on them
    public int lastAStar;
    // Represents whether the person has started the evacuation phase
    public boolean evacBool;

    public Person(double x1, double y1, int z, int goalID) {
        this.goalID = goalID;
        this.floor = z;
        floors = new ArrayList<Integer>();
        floors.add(z);
        lastAStar = 0;
        location = new Point2d(x1, y1);
        locations = new ArrayList<Point2d>();
        locations.add(new Point2d(location));
        // Randomise the size and mass of each person in order to create more realistic diversity in people
        size = Math.random() * 0.2 + 0.5; // metres
        mass = 80; // kilograms
        // Randomise walking speed
        desiredSpeed = Math.random() * 0.52 + 1.08; // metres per second
        relaxTime = 0.5;
        actualVelocity = new Vector2d(0, 0);

        goalList = new LinkedList<Node>();
        goalIndex = 0;

        forceModel = new Model();

        blockedList = new ArrayList<Boolean>();
        stuckOnWallSince = 0;
        evacBool = false;
    }

    // Remove the set of goals which the person has passed through since this method was last called
    private void goalUpdate() {
        while (goalIndex < goalList.size() - 1 &&
                location.distance(goalList.get(goalIndex).toPoint2d()) < (size * 2.0) && this.floor == goalList.get(goalIndex).getZ()) {
            goalIndex++;
        }
        if (floor != goalList.get(goalIndex).getZ()) {
            floor = goalList.get(goalIndex).getZ();
        }
    }

    // Update persons location by calling upon the force model
    public Point2d advance(ArrayList<ArrayList<Wall>> walls, ArrayList<Person> people, double timeStep, World w) throws NaNException,
            PersonOverlapException, RoutesNotComputedException {

        // If the person has reached their next goal, set them off towards a new one
        if (goalIndex == goalList.size() || (location.distance(goalList.getLast().toPoint2d()) < (size * 2.0) && floor == goalList.getLast().getZ())) {
            if (evacBool) {
                location = null;
                return location;
            } else {
                // pick a new random goal
                int tGoalID = (int) Math.round(Math.random() * (w.fdPOIList.size() - 1));

                if (w.fdPOIList.size() != 1) {
                    // Randomize new goal until it works
                    while (tGoalID == this.goalID) {
                        tGoalID = (int) Math.round(Math.random() * (w.fdPOIList.size() - 1));
                    }
                }
                Path path = w.getPath((int) Math.round(location.x), (int) Math.round(location.y), floor, tGoalID, evacBool);
                this.goalID = tGoalID;
                this.setGoalList(path.getSubGoals());
            }
        }

        // Attempt to remove any redundant goals
        goalUpdate();

        if (goalIndex < goalList.size()) {
            Vector2d accTerm = new Vector2d(0, 0);
            accTerm.add(desiredAcceleration());
            // Pass over each person and add the force that the force model determines they will exert on the person
            // to the total force that will be experienced by the person at this timestep
            for (Person p : people) {
                try {
                    if (this != p && p.getLocation() != null && p.floor == this.floor) {
                        accTerm.add(forceModel.socialForce(this, p));
                    }
                } catch (NullPointerException ignored) {
                    // This simply avoids null pointer exceptions when != null fails
                }

            }
            // Repeat the above but for the walls
            for (Wall wall : walls.get(floor)) {
                accTerm.add(forceModel.obstacleAvoidance(this, wall));
            }
            // The following equations are an implementation of the social force model specified in the report
            accTerm.scale(1.0 / mass);

            if (accTerm.length() > 1.3 * desiredSpeed) {
                accTerm.normalize();
                accTerm.scale(1.3 * desiredSpeed);
            }

            actualVelocity.add(accTerm);

            if (actualVelocity.length() > 1.3 * desiredSpeed) {
                actualVelocity.normalize();
                actualVelocity.scale(1.3 * desiredSpeed);
            }

            Vector2d motion = new Vector2d(actualVelocity);
            motion.scale(timeStep);
            // Move the person according to the force that the model exerts on them
            location.add(motion);
        }

        // Record the current location of the person for the simulation output
        locations.add(new Point2d(location.x, location.y));
        // Record the current floor o the person for the simulation output
        floors.add(floor);

        int currentGoal = goalIndex;
        // Remove any goals reached in this timestep
        goalUpdate();
        // If the person now has a different goal, estimate when they should reach it
        // This allows for being able to determine if the person is stuck
        if (goalIndex != currentGoal) {
            distanceToNextGoal = location.distance(goalList.get(goalIndex).toPoint2d());
            expectedTimeStepAtNextGoal = (distanceToNextGoal / (desiredSpeed * 0.1)) + 5 + (locations.size());
        }
        blockedList.add(false);
        return location;
    }

    public int getGoalIndex() {
        return goalIndex;
    }

    public Vector2d desiredAcceleration() {
        // get next goal on path
        Vector2d v = getDesiredDirection();

        // calculate desired velocity
        v.scale(desiredSpeed);

        // calculate acceleration term
        v.sub(actualVelocity);
        v.scale(mass * relaxTime);

        return v;
    }

    public Vector2d getDesiredDirection() {
        Vector2d v = new Vector2d(getNextGoal());
        v.sub(new Vector2d(location));
        v.normalize();
        return v;
    }

    public Point2d getNextGoal() {
        if (goalIndex < goalList.size())
            return goalList.get(goalIndex).toPoint2d();
        return location;
    }

    public void setGoalList(LinkedList<Node> goalList) {
        this.goalList = goalList;
        goalIndex = 0;
        distanceToNextGoal = location.distance(getNextGoal());
        expectedTimeStepAtNextGoal = (distanceToNextGoal / (desiredSpeed * 0.1)) + 5;
    }

    public LinkedList<Node> getGoalList() {
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

    public double getDesiredSpeed() {
        return desiredSpeed;
    }

    public void setDesiredSpeed(double desiredSpeed) {
        this.desiredSpeed = desiredSpeed;
    }

    public void setSize(double size) {
        this.size = size;
    }
}