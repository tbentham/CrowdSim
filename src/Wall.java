import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class Wall implements BuildingObject {

    public Vector2d startVector;
    public double startX;
    public double startY;
    public double endX;
    public double endY;
    public Vector2d endVector;

    public Wall(double x1, double y1, double x2, double y2) {
        startVector = new Vector2d(x1, y1);
        endVector = new Vector2d(x2, y2);
        startX = x1;
        startY = y1;
        endX = x2;
        endY = y2;
    }

    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeLine(startX, startY, endX, startX);
    }

    private double distance(Person person) {
        Vector2d v = new Vector2d(endX, endY);
        v.sub(new Vector2d(startX, startY));
        double l2 = v.lengthSquared();
        if (l2 == 0.0) {
            return person.getLocation().distance(new Point2d(startX, startY));
        }
        Vector2d p = new Vector2d(person.getLocation());
        v = new Vector2d(startX, startY);
        p.sub(v);
        Vector2d w = new Vector2d(endX, endY);
        w.sub(v);
        double t = p.dot(w) / l2;
        if (t < 0.0) {
            return person.getLocation().distance(new Point2d(startX, startY));
        } else if (t > 1.0) {
            return person.getLocation().distance(new Point2d(endX, endY));
        }
        v = new Vector2d(startX, startY);
        w = new Vector2d(endX, endY);
        w.sub(v);
        w.scale(t);
        w.add(v);
        Point2d p1 = new Point2d();
        p1.add(w);
        return person.getLocation().distance(p1);
    }

    private double distance(Point2d point2d) {
        double[] points = new double[2];
        point2d.get(points);
        return distance(new Person(points[0], points[1]));
    }

    public boolean touches(Point2d point2d, double size) {
        return size > distance(point2d);
    }

    public boolean touches(Person person) {
        return person.getSize() > distance(person);
    }

}
