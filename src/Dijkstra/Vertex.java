package Dijkstra;

import javax.vecmath.Point2d;

public class Vertex {
    private String id;

    private double x;
    private double y;
    private int z;


    public Vertex(double x, double y, int z) {
        this.x = x;
        this.y = y;
        this.id = x + "_" + y;
        this.z = z;

    }

    public int getZ() {
        return z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getId() {
        return id;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vertex other = (Vertex) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "("+x+", "+y+")";
    }

    public Point2d toPoint2d() {
        return (new Point2d(x, y));
    }

} 