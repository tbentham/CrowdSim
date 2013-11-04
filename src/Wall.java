import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class Wall implements BuildingObject {

    private Vector2d startVector;
    private Vector2d endVector;
    
    public Wall(double x1, double y1, double x2, double y2) {
        startVector = new Vector2d(x1, y1);
        endVector = new Vector2d(x2, y2);
    }

    public double distance(Person person) {
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
        return distance(new Person(points[0], points[1]));
    }

    public boolean touches(Point2d point2d, double radius) {
        return radius > distance(point2d);
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
