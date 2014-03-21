package WorldRepresentation;

import Exceptions.NaNException;
import Exceptions.NoGoalException;
import Exceptions.PersonOverlapException;
import Exceptions.RoutesNotComputedException;
import ForceModel.Model;
import NewDijkstra.Node;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.LinkedList;

public class Person {

    public Point2d location;
    public ArrayList<Point2d> locations;
    public boolean astarCheck;
    public ArrayList<Integer> floors;

    private double size;
    private double mass;

    private double desiredSpeed;
    private double relaxTime;
    private Vector2d actualVelocity;
    public int stuckOnWallSince;

    private int goalID;
    private LinkedList<Node> goalList;
    private int goalIndex;
    public int floor;



    public ArrayList<Boolean> blockedList;
    
    private Model forceModel;

    public double distanceToNextGoal;
    public double expectedTimeStepAtNextGoal;
    public int lastAStar;

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
        size = Math.random() * 0.2 + 0.5;	// metres
        mass = 80;	// kilograms
        astarCheck = false;
        
        desiredSpeed = Math.random() * 0.52 + 1.08;    // metres per second
        relaxTime = 0.5;
        actualVelocity = new Vector2d(0, 0);

        goalList = new LinkedList<Node>();
        goalIndex = 0;

        forceModel = new Model();

        blockedList = new ArrayList<Boolean>();
        stuckOnWallSince = 0;
        evacBool = false;
    }

    private void goalUpdate() {
        while (goalIndex < goalList.size() - 1 &&
        		location.distance(goalList.get(goalIndex).toPoint2d()) < (size * 2.0) && this.floor == goalList.get(goalIndex).floor)   {
            goalIndex++;
        }
        if (floor != goalList.get(goalIndex).floor) {
            floor = goalList.get(goalIndex).floor;
        }
    }

    public Point2d advance(ArrayList<ArrayList<Wall>> walls, ArrayList<Person> people, double timeStep, World w) throws NaNException,
            PersonOverlapException, NoGoalException, RoutesNotComputedException{

        if (goalIndex == goalList.size() || (location.distance(goalList.getLast().toPoint2d()) < (size * 2.0) && floor == goalList.getLast().floor)) {
            if (evacBool) {
                location = null;
                return location;
            }
            else {
                if(this.goalList.size() == 0) {
                    System.out.println("/SHIZ");
                }
                //pick a new random goal
                int tGoalID = (int)Math.round(Math.random() * (w.fdPOIList.size() -1));

                //Randomize new goal until it works
                while(tGoalID == this.goalID) {
                    tGoalID = (int)Math.round(Math.random() * (w.fdPOIList.size() -1));
                }
                if (tGoalID == -1) {
                    System.out.println("");
                }
                Path path = w.getPath((int)Math.round(location.x), (int)Math.round(location.y), floor, tGoalID, evacBool);
                this.goalID = tGoalID;
                this.setGoalList(path.getSubGoals());
                if(this.goalList.size() == 0) {
                    System.out.println("/shiz");
                }
            }
        }

        int currentGoal = goalIndex;
        goalUpdate();


        if (goalIndex < goalList.size()) {
        	Vector2d accTerm = new Vector2d(0,0);
        	accTerm.add(desiredAcceleration());

            for (Person p : people) {
                if (this != p && p.getLocation() != null && p.floor == this.floor) {
                	accTerm.add(forceModel.socialForce(this, p));
                }
            }

            for (Wall wall : walls.get(floor)) {
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
        floors.add(floor);

        Boolean stuckStatus = false;

        currentGoal = goalIndex;
        goalUpdate();
        if (goalIndex != currentGoal) {
//            System.out.println("I expected to reach my first goal at: " + expectedTimeStepAtNextGoal + ", bitches");
//            System.out.println("I've reached a goal bitches, I am: " + this.toString());
//            System.out.println("Current timestep is " + locations.size());
            distanceToNextGoal = location.distance(goalList.get(goalIndex).toPoint2d());

            expectedTimeStepAtNextGoal = (distanceToNextGoal/ (desiredSpeed * 0.1)) + 5 + (locations.size());
        }
        else {
            //System.out.println(this.toString() + " didn't reach goal, expect to reach next goal at " + expectedTimeStepAtNextGoal + ", it is " + distanceToNextGoal + " away, current timestep: " + locations.size());
            if (expectedTimeStepAtNextGoal + 1 < locations.size()) {
                //System.out.println(this.toString() + "expected: " + expectedTimeStepAtNextGoal + " current timestep: " + locations.size());
                //System.out.println(this.toString() + ": I'm stuck pls halp :(");
                stuckStatus = true;
            }
        }

        //Turn this back to stuck status

        blockedList.add(false);
        return location;
    }

    public Point2d advance(World world, ArrayList<Person> people, double timeStep) throws NaNException,
            PersonOverlapException, NoGoalException, RoutesNotComputedException {
        return advance(world.getWalls(), people, timeStep, world);
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
        distanceToNextGoal = location.distance(getNextGoal());
        expectedTimeStepAtNextGoal = (distanceToNextGoal / (desiredSpeed * 0.1)) + 5;
//        System.out.println("");
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