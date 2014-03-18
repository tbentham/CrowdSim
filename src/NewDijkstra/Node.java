package NewDijkstra;

import javax.vecmath.Point2d;

public class Node {

    public Integer x;
    public Integer y;
    public Integer floor;

    public Node(Integer x, Integer y, Integer floor) {
        this.x = x;
        this.y = y;
        this.floor = floor;
    }

    public Point2d toPoint2d() {
        return new Point2d(x, y);
    }

    @Override
    public String toString() {
        return (x + ", " + y);
    }

}
