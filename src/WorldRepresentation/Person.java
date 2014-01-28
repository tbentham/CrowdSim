package WorldRepresentation;

import Dijkstra.Vertex;
import Exceptions.NaNException;
import Exceptions.NoGoalException;
import Exceptions.PersonOverlapException;
import ForceModel.Model;

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

    private LinkedList<Vertex> goalList;
    
    private Model forceModel;

    public Person(double x1, double y1) {
        location = new Point2d(x1, y1);
        locations = new ArrayList<Point2d>();
        locations.add(new Point2d(location));
        size = Math.random() * 0.2 + 0.5;	// metres
        mass = 80;	// kilograms

        desiredSpeed = Math.random() * 0.52 + 1.08;    // metres per second
        relaxTime = 0.5;
        actualVelocity = new Vector2d(0, 0);

        goalList = new LinkedList<Vertex>();

        forceModel = new Model();
    }

    private void goalUpdate() {
        while (goalList.size() > 0 &&
        		location.distance(goalList.get(0).toPoint2d()) < (size * 2.0))
            goalList.remove(0);
    }

    // TODO: Use this in BasicCanvas
    private void goalUpdate(ArrayList<Wall> walls) {
    	boolean nextGoalClear = true;
    	
    	if ( goalList.size() > 1 ) {
    		for (Wall w : walls) {
    			if ( w.intersects(location, goalList.get(1).toPoint2d(), size+1.0) ) {
    				nextGoalClear = false;
    				break;
    			}
    		}
    	}
    	
        while ( ( goalList.size() == 1 &&
        		location.distance(goalList.get(0).toPoint2d()) < (size * 2.0) ) ||
        		( goalList.size() > 1 && nextGoalClear ) ) {
        	
            goalList.remove(0);
            
            
            if ( goalList.size() > 1 ) {
            	nextGoalClear = true;
        		for (Wall w : walls) {
        			if ( w.intersects(location, goalList.get(1).toPoint2d(), size+1.0) ) {
        				nextGoalClear = false;
        				break;
        			}
        		}
        	}
        }
    }

    // TODO: Write test case(s) for this function
    public Point2d advance(ArrayList<Wall> walls, ArrayList<Person> people, double timeStep) throws NaNException,
            PersonOverlapException, NoGoalException {

        if (goalList.size() == 0 || location.distance(goalList.getLast().toPoint2d()) < (size * 2.0)) {
            locations.add(new Point2d(location));
            return location;
        }

        goalUpdate();

        if (goalList.size() > 0) {
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

        goalUpdate();

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

    public Vector2d getDesiredDirection() {
        Vector2d v = new Vector2d(getNextGoal());
        v.sub(new Vector2d(location));
        v.normalize();
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