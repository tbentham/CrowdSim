package NewDijkstra;

import javax.vecmath.Point2d;

public class Node {

    public Integer x;
    public Integer y;

    public Node(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Point2d toPoint2d() {
        return new Point2d(x, y);
    }

}
