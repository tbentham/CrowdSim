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

        double d = 3.5 * Math.exp((-b(aPerson, bPerson, timeStep)) / 0.3);
//      double d = 2.0 / b(bPerson);

        Vector2d aVector = new Vector2d(aPerson.getLocation());
        aVector.sub(new Vector2d(bPerson.getLocation()));

        aVector.normalize();
        aVector.scale(d);

        // Consider field of vision
        Vector2d direction = new Vector2d(aPerson.getVelocity());
        direction.normalize();
        if (direction.dot(aVector) < aVector.length() * Math.cos(100.0 * Math.PI / 180.0))
            aVector.scale(0.5);

        if (Double.isNaN(aVector.x)) {
            throw new NaNException("socialForce between person at " + aPerson.getLocation() +
                    " with velocity: " + aPerson.getVelocity() + " and person at " + bPerson.getLocation() +
                    " with velocity: " + bPerson.getVelocity() + ": " + aVector);
        }
        return aVector;
    }

    public double b(Person aPerson, Person bPerson, double timeStep) throws NaNException, PersonOverlapException {
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
    }


    public Vector2d obstacleAvoidance(Person aPerson, Wall wall) throws NaNException {
        if (Double.isNaN(aPerson.getLocation().x)) {
            throw new NaNException("obstacleAvoidance called with aPerson at NaN location");
        }
        Vector2d aVector = new Vector2d(aPerson.getLocation());
        aVector.sub(new Vector2d(wall.nearestPoint(aPerson)));

        double d = 10 * Math.exp(-aVector.length() / 0.2);

        aVector.normalize();
        aVector.scale(d);

        return aVector;
    }

}
