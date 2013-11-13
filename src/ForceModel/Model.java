package ForceModel;

import WorldRepresentation.Person;
import WorldRepresentation.Wall;

import javax.vecmath.Vector2d;

public class Model {

    public Model() {
    }

    public Vector2d socialForce(Person aPerson, Person bPerson, double timeStep) {
        double d = 2.1 * Math.exp((-b(aPerson, bPerson, timeStep)) / 0.3);
//      double d = 2.0 / b(bPerson);

        Vector2d aVector = new Vector2d(aPerson.getLocation());
        aVector.sub(new Vector2d(bPerson.getLocation()));

        aVector.normalize();
        aVector.scale(d);

        // Consider field of vision
        Vector2d direction = new Vector2d(aPerson.getVelocity());
        direction.normalize();
        if (direction.dot(aVector) < aVector.length() * Math.cos(100 * Math.PI / 180))
            aVector.scale(0.5);

        return aVector;
    }

    public double b(Person aPerson, Person bPerson, double timeStep) {
        Vector2d aVector = new Vector2d(aPerson.getLocation());
        aVector.sub(new Vector2d(bPerson.getLocation()));

        Vector2d bVector = bPerson.getDesiredDirection();
        double bSpeed = bPerson.getNextSpeed();
        bVector.scale(bSpeed*timeStep);
        Vector2d cVector = new Vector2d(aVector);
        cVector.sub(bVector);

        double squareRootMe = Math.pow(aVector.length() + cVector.length(), 2) - Math.pow(bSpeed*timeStep, 2);
        return Math.sqrt(squareRootMe) / 2.0;
    }


    public Vector2d obstacleAvoidance(Person aPerson, Wall wall) {
        Vector2d aVector = new Vector2d(aPerson.getLocation());
        aVector.sub(new Vector2d(wall.nearestPoint(aPerson)));

        double d = 10 * Math.exp(-aVector.length() / 0.2);

        aVector.normalize();
        aVector.scale(d);

        return aVector;
    }

}
