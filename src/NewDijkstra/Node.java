package NewDijkstra;

import javax.vecmath.Point2d;

public class Node {

    private Integer x;
    private Integer y;
    private Integer floor;

    public Node(Integer x, Integer y, Integer floor) {
        this.x = x;
        this.y = y;
        this.floor = floor;
    }

    public Point2d toPoint2d() {
        return new Point2d(x, y);
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public Integer getZ() {
        return floor;
    }


}
