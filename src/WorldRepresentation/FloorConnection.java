package WorldRepresentation;

import javax.vecmath.Point2d;

// Represents any way that people can move between floors
public class FloorConnection {

    public Point2d location;
    public int fromFloor;
    public int toFloor;

    public FloorConnection(Double x, Double y, int from, int to) {
        location = new Point2d(x, y);
        fromFloor = from;
        toFloor = to;
    }


}
