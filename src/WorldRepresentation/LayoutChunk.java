package WorldRepresentation;

import javax.vecmath.Point2d;
import java.util.ArrayList;

public class LayoutChunk implements Runnable {

    private ArrayList<Wall> walls;
    private double topYBoundary;
    private double bottomYBoundary;
    private double rightXBoundary;
    private double leftXBoundary;
    private ArrayList<Person> people;
    boolean finished;

    public LayoutChunk(double leftXBoundary, double rightXBoundary, double topYBoundary, double bottomYBoundary) {
        people = new ArrayList<>();
        this.topYBoundary = topYBoundary;
        this.bottomYBoundary = bottomYBoundary;
        this.leftXBoundary = leftXBoundary;
        this.rightXBoundary = rightXBoundary;
        walls = new ArrayList<>();
        finished = false;
    }

    public void addWall(double x1, double y1, double x2, double y2) {
        walls.add(new Wall(x1, y1, x2, y2));
    }

    public ArrayList<Wall> getWalls() {
        return walls;
    }

    public void addPerson(Person p) {
        people.add(p);
    }

    public ArrayList<Person> getPeople() {
        return people;
    }

    public boolean isPointInside(double x, double y) {
        return (y <= topYBoundary && y >= bottomYBoundary && x <= rightXBoundary && x >= leftXBoundary);
    }

    public boolean intersectsTop(Wall w) {
        return (w.intersects(new Point2d(leftXBoundary, topYBoundary), new Point2d(rightXBoundary, topYBoundary), 1));
    }

    public boolean intersectsBottom(Wall w) {
        return (w.intersects(new Point2d(leftXBoundary, bottomYBoundary), new Point2d(rightXBoundary, bottomYBoundary), 1));
    }

    public boolean intersectsLeft(Wall w) {
        return (w.intersects(new Point2d(leftXBoundary, bottomYBoundary), new Point2d(leftXBoundary, topYBoundary), 1));
    }

    public boolean intersectsRight(Wall w) {
        return (w.intersects(new Point2d(rightXBoundary, bottomYBoundary), new Point2d(rightXBoundary, topYBoundary), 1));
    }

    public int numberOfIntersects(Wall w) {
        int num = 0;
        if (intersectsBottom(w)) {
            num++;
        }
        if (intersectsTop(w)) {
            num++;
        }
        if (intersectsRight(w)) {
            num++;
        }
        if (intersectsLeft(w)) {
            num++;
        }
        return num;
    }

    public void run() {
        //for (int i = 0; i < 500; i++) {
            for (Person p : people) {
                try {
                    p.advance(walls, people, 0.25);
                }
                catch (Exception e) {
                    //
                }
            }
        //}
    }
}
