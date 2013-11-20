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

    public boolean intersects(Point2d p1, Point2d p2, double width) {
    	double x1 = startVector.getX();
    	double y1 = startVector.getY();
    	double x2 = endVector.getX();
    	double y2 = endVector.getY();
    	
    	double x3 = p1.getX();
    	double y3 = p1.getY();
    	double x4 = p2.getX();
    	double y4 = p2.getY();
    	
		double addedLengthx;
		double addedLengthy;
		if ( x1 == x2 ) {
			addedLengthx = 0;
			addedLengthy = width/2.0;
    	}
    	else if ( y1 == y2 ) {
    		addedLengthx = width/2.0;
    		addedLengthy = 0;
    	}
    	else {
    		double gradientSq = Math.pow(y2-y1/x2-x1,2);
    		addedLengthx = Math.sqrt(Math.pow(width/2.0,2)/(1.0+gradientSq));
    		addedLengthy = Math.sqrt(Math.pow(width/2.0,2)/(1.0+1.0/gradientSq));
    	}
		
		if ( Math.max(x1,x2) == x1 ) {
			x1 += addedLengthx;
			x2 -= addedLengthx;
		}
		else {
			x1 -= addedLengthx;
			x2 += addedLengthx;
		}
		
		if ( Math.max(y1,y2) == y1 ) {
			y1 += addedLengthy;
			y2 -= addedLengthy;
		}
		else {
			y1 -= addedLengthy;
			y2 += addedLengthy;
		}
    	
    	double divisor = (x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4);
    	if ( divisor == 0)
    		return true;	// Fail-safe - assume intersect
    	
    	double xInt = ((x1*y2 - y1*x2)*(x3 - x4) - (x1 - x2)*(x3*y4 - y3*x4)) / divisor;
    	
    	return (Math.min(x1,x2) <= xInt && xInt <= Math.max(x1,x2) &&
    			Math.min(x3,x4) <= xInt && xInt <= Math.max(x3,x4));
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
