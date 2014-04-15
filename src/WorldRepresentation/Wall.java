package WorldRepresentation;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

// Represents walls in the building layout mainly as vectors, this allows for force calculations to be done
// as well as collision and intersection calculations
public class Wall {

    // Record the starting and ending position of the wall
    private Vector2d startVector;
    private Vector2d endVector;

    public Wall(double x1, double y1, double x2, double y2) {
        startVector = new Vector2d(x1, y1);
        endVector = new Vector2d(x2, y2);
    }

    // Returns the shortest distance between the wall and the given point
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
            if (Math.acos((Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2)) / (2.0 * a * b)) > Math.PI / 2.0)
                return b;
        } else {
            // Check if point is past end of wall
            if (Math.acos((Math.pow(a, 2) + Math.pow(c, 2) - Math.pow(b, 2)) / (2.0 * a * c)) > Math.PI / 2.0)
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

    // Returns the closest x, y coordinate point on the wall to the given point
    // This is used to allow the force that a wall exerts on a person to be applied in a specific direction
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

        // Check if triangle is flat
        if (c == a + b)
            return new Point2d(startVector);
        else if (b == a + c)
            return new Point2d(endVector);
        else if (a == b + c)
            return new Point2d(point);

        if (b < c) {
            // Check if point is past start of wall
            double angle = Math.acos((Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2)) / (2.0 * a * b));
            if (angle > Math.PI / 2.0)
                return new Point2d(startVector);
        } else {
            // Check if point is past end of wall
            double angle = Math.acos((Math.pow(a, 2) + Math.pow(c, 2) - Math.pow(b, 2)) / (2.0 * a * c));
            if (angle > Math.PI / 2.0)
                return new Point2d(endVector);
        }

        // Get nearest point on line
        double cosTheta = startToPoint.dot(startToEnd) / (a * b);
        double aScaled = b * cosTheta;
        startToEnd.scale(aScaled / a);
        startToEnd.add(startVector);

        return new Point2d(startToEnd);
    }

    // Returns true if the given points when represented as a wall would intersect this wall
    public boolean intersects(Point2d p1, Point2d p2, double addedLength) {
        double x1 = startVector.x;
        double y1 = startVector.y;
        double x2 = endVector.x;
        double y2 = endVector.y;

        double x3 = p1.x;
        double y3 = p1.y;
        double x4 = p2.x;
        double y4 = p2.y;

        if (addedLength > 0.0) { /* consider extension of wall */
            double addedX;
            double addedY;
            if (x1 == x2) {
                addedX = 0;
                addedY = addedLength;
            } else if (y1 == y2) {
                addedX = addedLength;
                addedY = 0;
            } else {
                double gradientSq = Math.pow(y2 - y1 / x2 - x1, 2);
                addedX = Math.sqrt(addedLength * addedLength / (1.0 + gradientSq));
                addedY = Math.sqrt(addedLength * addedLength / (1.0 + 1.0 / gradientSq));
            }

            if (Math.max(x1, x2) == x1) {
                x1 += addedX;
                x2 -= addedX;
            } else {
                x1 -= addedX;
                x2 += addedX;
            }

            if (Math.max(y1, y2) == y1) {
                y1 += addedY;
                y2 -= addedY;
            } else {
                y1 -= addedY;
                y2 += addedY;
            }
        }

        double divisor = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

        if (divisor == 0) {
            /* Consider cases where line intersect cannot be calculated */
            if (y1 <= y3 && y3 <= y2)
                return y3 == (x3 - x1) * (y2 - y1) / (x2 - x1) + y1;
            else if (y1 <= y4 && y4 <= y2)
                return y4 == (x4 - x1) * (y2 - y1) / (x2 - x1) + y1;
            else if (y3 <= y1 && y1 <= y4)
                return y1 == (x1 - x3) * (y4 - y3) / (x4 - x3) + y3;
            else if (y3 <= y2 && y2 <= y4)
                return y2 == (x2 - x3) * (y4 - y3) / (x4 - x3) + y3;
            else
                return false;
        }

        double xInt = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / divisor;

        return (Math.min(x1, x2) <= xInt && xInt <= Math.max(x1, x2) &&
                Math.min(x3, x4) <= xInt && xInt <= Math.max(x3, x4));
    }

    public boolean intersects(Point2d p1, Point2d p2) {
        return intersects(p1, p2, 0.0);
    }

    public boolean intersects(Wall w, double width) {
        return intersects(new Point2d(w.getStartVector().x, w.getStartVector().y),
                new Point2d(w.getEndVector().x, w.getEndVector().y), width);
    }

    public Point2d nearestPoint(Person person) {
        return nearestPoint(person.getLocation());
    }

    public boolean touches(Point2d point, double radius) {
        return radius > distance(point);
    }

    public boolean touches(Person person) {
        return (person.getSize() * 2) > distance(person);
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
