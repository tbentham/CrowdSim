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

    // Returns the shortest distance between the wall and @point
    public double distance(Point2d point) {
        double a = this.length();

        // If the wall is a single point, return distance from that point instead
        if (a == 0.0) {
            return point.distance(new Point2d(startVector.x, startVector.y));
        }

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

    public Point2d nearestPoint(Point2d point) {
        // Get lengths of triangle
        Vector2d startToEnd = new Vector2d(endVector);
        startToEnd.sub(new Vector2d(startVector));
        double a = startToEnd.length();

        Vector2d startToPoint = new Vector2d(point);
        startToPoint.sub(new Vector2d(startVector));
        double b = startToPoint.length();

        Vector2d endToPoint = new Vector2d(point);
        endToPoint.sub(new Vector2d(endVector));
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

    public double length() {
        Vector2d startToEnd = new Vector2d(endVector);
        startToEnd.sub(new Vector2d(startVector));
        return startToEnd.length();
    }

}
