package ForceModel;

import Exceptions.NaNException;
import Exceptions.PersonOverlapException;
import WorldRepresentation.Person;
import WorldRepresentation.Wall;

import javax.vecmath.Vector2d;

// TODO: Merge this whole file with new model
public class Model {

    public Model() {
    }

    public Vector2d socialForce(Person aPerson, Person bPerson) throws NaNException,
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
        Vector2d v2 = new Vector2d(-v1.y, v1.x);
        v2.scale(240000 * g * relVelocity.dot(v2));
        
        // calculate direct force
        v1.scale(2000 * Math.exp(-distance / 0.08) + 120000 * g);

        // sum forces
        v1.add(v2);
        
        return v1;
    }

/*    public double b(Person aPerson, Person bPerson, double timeStep) {

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
        Vector2d v2 = new Vector2d(-v1.y, v1.x);
        v2.scale(240000 * g * aPerson.getVelocity().dot(v2));
        
        // calculate direct force
        v1.scale(2000 * Math.exp(-distance / 0.08) + 480000 * g);
        
        // subtract tangential force
        v1.sub(v2);
        
/*      Vector2d aVector = new Vector2d(aPerson.getLocation());
        aVector.sub(new Vector2d(wall.nearestPoint(aPerson)));

        double d = 10 * Math.exp(-aVector.length() / 0.2);

        aVector.normalize();
        aVector.scale(d);*/

        return v1;
    }

}
