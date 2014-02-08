package WorldRepresentation;

import Exceptions.NaNException;
import Exceptions.NoGoalException;
import Exceptions.PersonOverlapException;
import ForceModel.Model;
import NewDijkstra.Node;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.LinkedList;

public class Person {

    private Point2d location;
    public ArrayList<Point2d> locations;

    private double size;
    private double mass;

    private double desiredSpeed;
    private double relaxTime;
    private Vector2d actualVelocity;

    private LinkedList<Node> goalList;
    private int goalIndex;

    public ArrayList<Boolean> blockedList;
    
    private Model forceModel;

    private double distanceToNextGoal;
    private double expectedTimeStepAtNextGoal;

    public Person(double x1, double y1) {
        location = new Point2d(x1, y1);
        locations = new ArrayList<Point2d>();
        locations.add(new Point2d(location));
        size = Math.random() * 0.2 + 0.5;	// metres
        mass = 80;	// kilograms

        desiredSpeed = Math.random() * 0.52 + 1.08;    // metres per second
        relaxTime = 0.5;
        actualVelocity = new Vector2d(0, 0);

        goalList = new LinkedList<Node>();
        goalIndex = 0;

        forceModel = new Model();

        distanceToNextGoal = location.distance(getNextGoal());
        expectedTimeStepAtNextGoal = (desiredSpeed / distanceToNextGoal) + 10;

        blockedList = new ArrayList<Boolean>();
    }

    private void goalUpdate(ArrayList<Wall> walls) {
        while ( goalVisible(goalIndex+1, walls, size+1.0) || (goalIndex < goalList.size() &&
        		location.distance(goalList.get(goalIndex).toPoint2d()) < (size * 2.0)) )
            goalIndex++;
        
        // Path recovery
    	int diff = goalIndex;
        while ( !goalVisible(goalIndex, walls) && goalIndex > 0 && goalIndex < goalList.size() ) {
            goalIndex--;
        }
        if ( goalIndex == 0 ) {
        	goalIndex += diff;
        	diff = goalList.size()-goalIndex;
        	while ( !goalVisible(goalIndex, walls) && goalIndex < goalList.size() )
        		goalIndex++;
        	if ( goalIndex == goalList.size() )
        		goalIndex -= diff;
        }
    }

    private void goalUpdate() {
        while (goalIndex < goalList.size() &&
        		location.distance(goalList.get(goalIndex).toPoint2d()) < (size * 2.0))
            goalIndex++;
    }


    // TODO: Write test case(s) for this function
    public Point2d advance(ArrayList<Wall> walls, ArrayList<Person> people, double timeStep) throws NaNException,
            PersonOverlapException, NoGoalException {

        if (goalIndex == goalList.size() || location.distance(goalList.getLast().toPoint2d()) < (size * 2.0)) {
            locations.add(new Point2d(location));
            return location;
        }


        distanceToNextGoal = location.distance(getNextGoal());
        expectedTimeStepAtNextGoal = (desiredSpeed / distanceToNextGoal) + locations.size();
        // System.out.println("I am " + this.toString() + " and my speed is " + actualVelocity.length());

        int currentGoal = goalIndex;
        goalUpdate();
//        if (goalIndex != currentGoal) {
//            System.out.println("I've reached a goal bitches, I am : " + this.toString());
//        }

        if (goalIndex < goalList.size()) {
        	Vector2d accTerm = new Vector2d(0,0);
        	accTerm.add(desiredAcceleration());

            for (Person p : people) {
                if (this != p) {
                	accTerm.add(forceModel.socialForce(this, p));
                	
                }
            }

            for (Wall wall : walls) {
                accTerm.add(forceModel.obstacleAvoidance(this, wall));
            }
            
            accTerm.scale(1.0 / mass);
            
            if (accTerm.length() > 1.3*desiredSpeed) {
            	accTerm.normalize();
            	accTerm.scale(1.3*desiredSpeed);
            }

            actualVelocity.add(accTerm);
            
            if (actualVelocity.length() > 1.3*desiredSpeed) {
                actualVelocity.normalize();
                actualVelocity.scale(1.3*desiredSpeed);
            }

            Vector2d motion = new Vector2d(actualVelocity);
            motion.scale(timeStep);

            location.add(motion);
            
        }

        locations.add(new Point2d(location.x, location.y));

        Boolean stuckStatus = false;
        goalUpdate();
        if (goalIndex != currentGoal) {
//            System.out.println("I expected to reach my first goal at: " + expectedTimeStepAtNextGoal + ", bitches");
//            System.out.println("I've reached a goal bitches, I am: " + this.toString());
//            System.out.println("Current timestep is " + locations.size());
            if (expectedTimeStepAtNextGoal + 4 < locations.size()) {
                System.out.println(this.toString() + ": I'm stuck pls halp :(");
                stuckStatus = true;
            }
            else {
                System.out.println(this.toString() + ": Made it to my goal on time :)");
            }
            distanceToNextGoal = location.distance(getNextGoal());
            expectedTimeStepAtNextGoal = (desiredSpeed / distanceToNextGoal) + 10;
        }

        blockedList.add(stuckStatus);
        return location;
    }

    public Point2d advance(World world, ArrayList<Person> people, double timeStep) throws NaNException,
            PersonOverlapException, NoGoalException {
        return advance(world.getWalls(), people, timeStep);
    }


    public Vector2d desiredAcceleration() {
        // get next goal on path
        Vector2d v = getDesiredDirection();

        // calculate desired velocity
        v.scale(desiredSpeed);

        // calculate acceleration term
        v.sub(actualVelocity);
        v.scale(mass*relaxTime);

        return v;
    }

    public boolean goalVisible(int index, ArrayList<Wall> walls, double addedLength) {
    	if ( 0 <= index && index < goalList.size() ) {
    		for (Wall w : walls) {
    			if ( w.intersects(location, goalList.get(index).toPoint2d(), addedLength) ) {
    				return false;
				}
    		}
    		return true;
    	}
    	else {
    		return false;
    	}
    }

    public boolean goalVisible(int index, ArrayList<Wall> walls) {
    	return goalVisible(index, walls, 0.0);
    }
    
    public Vector2d getDesiredDirection() {
        Vector2d v = new Vector2d(getNextGoal());
        v.sub(new Vector2d(location));
        v.normalize();
        return v;
    }

    public double getNextSpeed() {
        goalUpdate();
        if (goalIndex == goalList.size())
            return 0;
        Vector2d nextVelocity = new Vector2d(actualVelocity);
        nextVelocity.add(desiredAcceleration());
        return nextVelocity.length();
    }

    public Point2d getNextGoal() {
        if (goalIndex < goalList.size())
            return goalList.get(goalIndex).toPoint2d();
        return location;
    }

    public void setGoalList(LinkedList<Node> goalList) {
        this.goalList = goalList;
        goalIndex = 0;
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
    
    public Vector2d getDirection() {
    	Vector2d v = new Vector2d(actualVelocity);
    	v.normalize();
        return v;
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