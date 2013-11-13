package WorldRepresentation;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class Wall implements BuildingObject {

    private Vector2d startVector;
    private Vector2d endVector;

    public Wall(double x1, double y1, double x2, double y2) {
        startVector = new Vector2d(x1, y1);
        endVector = new Vector2d(x2, y2);
    }

    public double distance(Point2d point) {
        // Get lengths of triangle
        Vector2d startToEnd = new Vector2d(endVector);
        startToEnd.sub(new Vector2d(startVector));
        double a = startToEnd.length();

        Vector2d startToPoint = new Vector2d(point);
        startToPoint.sub(new Vector2d(endVector));
        double b = startToPoint.length();

        Vector2d endToPoint = new Vector2d(point);
        endToPoint.sub(new Vector2d(startVector));
        double c = endToPoint.length();


        if (b < c) {
            // Check if point is past start of wall
            if ( Math.acos((Math.pow(a,2) + Math.pow(b,2) - Math.pow(c,2)) / (2.0*a*b)) > Math.PI/2.0 )
                return b;
        }
        else {
            // Check if point is past end of wall
            if ( Math.acos((Math.pow(a,2) + Math.pow(c,2) - Math.pow(b,2)) / (2.0*a*c)) > Math.PI/2.0 )
                return c;
        }

        // Get distance to nearest point on wall (reusing variables)
        a = startVector.y - endVector.y;
        b = endVector.x - startVector.x;
        c = startVector.x * endVector.y - endVector.x * startVector.y;

        return Math.abs(a * point.x + b * point.y + c) / Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }

    public double distance(Person person) {
        return distance(person.getLocation());
    }
    	
/*
    	Vector2d v = new Vector2d(endVector);
        v.sub(new Vector2d(startVector));
        double l2 = v.lengthSquared();
        if (l2 == 0.0) {
            return person.getLocation().distance(new Point2d(startVector));
        }
        Vector2d p = new Vector2d(person.getLocation());
        v = new Vector2d(startVector);
        p.sub(v);
        Vector2d w = new Vector2d(endVector);
        w.sub(v);
        double t = p.dot(w) / l2;
        if (t < 0.0) {
            return person.getLocation().distance(new Point2d(startVector));
        } else if (t > 1.0) {
            return person.getLocation().distance(new Point2d(endVector));
        }
        v = new Vector2d(startVector);
        w = new Vector2d(endVector);
        w.sub(v);
        w.scale(t);
        w.add(v);
        Point2d p1 = new Point2d();
        p1.add(w);
        return person.getLocation().distance(p1);
    }

    public double distance(Point2d point2d) {
        double[] points = new double[2];
        point2d.get(points);
        return distance(new WorldRepresentation.Person(points[0], points[1]));
    }
*/

    public Point2d nearestPoint(Point2d point) {
        // Get lengths of triangle
        Vector2d startToEnd = new Vector2d(endVector);
        startToEnd.sub(new Vector2d(startVector));
        double a = startToEnd.length();

        Vector2d startToPoint = new Vector2d(point);
        startToPoint.sub(new Vector2d(endVector));
        double b = startToPoint.length();

        Vector2d endToPoint = new Vector2d(point);
        endToPoint.sub(new Vector2d(startVector));
        double c = endToPoint.length();


        if (b < c) {
            // Check if point is past start of wall
            if ( Math.acos((Math.pow(a,2) + Math.pow(b,2) - Math.pow(c,2)) / (2.0*a*b)) > Math.PI/2.0 )
                return new Point2d(startVector);
        }
        else {
            // Check if point is past end of wall
            if ( Math.acos((Math.pow(a,2) + Math.pow(c,2) - Math.pow(b,2)) / (2.0*a*c)) > Math.PI/2.0 )
                return new Point2d(endVector);
        }

        // Get nearest point on line
        double cosTheta = startToPoint.dot(startToEnd) / (a*b);
        double aScaled = b * cosTheta;
        startToEnd.scale(aScaled / a);
        startToEnd.add(startVector);

        return new Point2d(startToEnd);
    }

    public Point2d nearestPoint(Person person) {
        return nearestPoint(person.getLocation());
    }

    public boolean touches(Point2d point, double radius) {
        return radius > distance(point);
    }

    public boolean touches(Person person) {
        return (person.getSize() / 2.0) > distance(person);
    }

    public Vector2d getStartVector() {
        return startVector;
    }

    public Vector2d getEndVector() {
        return endVector;
    }

}
