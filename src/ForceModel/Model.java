package ForceModel;

import Exceptions.NaNException;
import Exceptions.PersonOverlapException;
import WorldRepresentation.Person;
import WorldRepresentation.Wall;

import javax.vecmath.Vector2d;

public class Model {

    public Model() {
    }

    public Vector2d socialForce(Person aPerson, Person bPerson, double timeStep) throws NaNException,
            PersonOverlapException {
        if (aPerson.getLocation().equals(bPerson.getLocation())) {
            throw new PersonOverlapException("socialForce called on 2 people at " + aPerson.getLocation());
        }
        if (Double.isNaN(aPerson.getLocation().x)) {
            throw new NaNException("socialForce called with aPerson at NaN location");
        }
        if (Double.isNaN(bPerson.getLocation().x)) {
            throw new NaNException("socialForce called with bPerson at NaN location");
        }
        
        // get relative location of a from b
        Vector2d v1 = new Vector2d(aPerson.getLocation());
        v1.sub(bPerson.getLocation());
        
        // get distance, considering person size
        double distance = v1.length() - (aPerson.getSize() + bPerson.getSize()) / 2.0;
        
        double g = distance > 0 ? 0 : -distance;
        
        // get relative velocity
        Vector2d relVelocity = new Vector2d(bPerson.getVelocity());
        relVelocity.sub(aPerson.getVelocity());
        
        // get normalized vector from b to a
        v1.normalize();
        
        // calculate tangential sliding force
        Vector2d v2 = new Vector2d(-v1.getY(), v1.getX());
        v2.scale(240000 * g * relVelocity.dot(v2));
        
        // calculate direct force
        v1.scale(2000 * Math.exp(-distance / 0.08) + 120000 * g);

        // sum forces
        v1.add(v2);

/*      Vector2d aVector = new Vector2d(aPerson.getLocation());
        aVector.sub(new Vector2d(bPerson.getLocation()));
        
        double d = 3.5 * Math.exp((-b(aPerson, bPerson, timeStep)) / 0.3);
//      double d = 2.0 / b(bPerson);

        aVector.normalize();
        aVector.scale(d);

        // Consider field of vision
        Vector2d direction = new Vector2d(aPerson.getVelocity());
        direction.normalize();
        if (direction.dot(v) < v.length() * Math.cos(100.0 * Math.PI / 180.0))
            v.scale(0.5);*/

        if (Double.isNaN(v1.x)) {
            throw new NaNException("socialForce between person at " + aPerson.getLocation() +
                    " with velocity: " + aPerson.getVelocity() + " and person at " + bPerson.getLocation() +
                    " with velocity: " + bPerson.getVelocity() + ": " + v1);
        }
        return v1;
    }

/*    public double b(Person aPerson, Person bPerson, double timeStep) throws NaNException, PersonOverlapException {
        if (aPerson.getLocation().equals(bPerson.getLocation())) {
            throw new PersonOverlapException("b function called on 2 people at " + aPerson.getLocation());
        }
        if (Double.isNaN(aPerson.getLocation().x)) {
            throw new NaNException("b function called with aPerson at NaN location");
        }
        if (Double.isNaN(bPerson.getLocation().x)) {
            throw new NaNException("b function called with bPerson at NaN location");
        }

        Vector2d aVector = new Vector2d(aPerson.getLocation());

        aVector.sub(new Vector2d(bPerson.getLocation()));
        Vector2d bVector = bPerson.getDesiredDirection();
        double bSpeed = bPerson.getNextSpeed();
        bVector.scale(bSpeed * timeStep);
        Vector2d cVector = new Vector2d(aVector);
        cVector.sub(bVector);

        double squareRootMe = Math.pow(aVector.length() + cVector.length(), 2) - Math.pow(bSpeed * timeStep, 2);
        double ret = Math.sqrt(squareRootMe) / 2.0;

        if (squareRootMe < 0 && squareRootMe > -1) {
            return 0.0;
        } else if (Double.isNaN(ret)) {
            throw new NaNException("Tried to squareRoot negative number in b function between person at " +
                    aPerson.getLocation() + " with velocity: " + aPerson.getVelocity() + " and person at " +
                    bPerson.getLocation() + " with velocity: " + bPerson.getVelocity() + " using timeStep " +
                    timeStep + ": " + ret);
        }
        return ret;
    }*/


    public Vector2d obstacleAvoidance(Person aPerson, Wall wall) throws NaNException {
        if (Double.isNaN(aPerson.getLocation().x)) {
            throw new NaNException("obstacleAvoidance called with aPerson at NaN location");
        }
        
        // get relative location of a from b
        Vector2d v1 = new Vector2d(aPerson.getLocation());
        v1.sub(new Vector2d(wall.nearestPoint(aPerson)));
        
        // get distance, considering person size
        double distance = v1.length() - aPerson.getSize() / 2.0;
        
        // get function denoting physical contact
        double g = distance > 0 ? 0 : -distance;
        
        // get normalized vector from b to a
        v1.normalize();
        
        // calculate tangential sliding force
        Vector2d v2 = new Vector2d(-v1.getY(), v1.getX());
        v2.scale(240000 * g * aPerson.getVelocity().dot(v2));
        
        // calculate direct force
        v1.scale(2000 * Math.exp(-distance / 0.08) + 240000 * g);
        
        // sum forces
        v1.add(v2);
        
/*      Vector2d aVector = new Vector2d(aPerson.getLocation());
        aVector.sub(new Vector2d(wall.nearestPoint(aPerson)));

        double d = 10 * Math.exp(-aVector.length() / 0.2);

        aVector.normalize();
        aVector.scale(d);*/

        return v1;
    }

}
