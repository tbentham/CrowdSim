package Dijkstra;

// Represents a vertex as in a vertex in a graph
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

    // Required when using as index in a HashMap or when stored in a PriorityQueue
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    // Required when using as index in a HashMap or when stored in a PriorityQueue
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vertex other = (Vertex) obj;
        if (!id.equals(other.id))
            return false;
        return true;
    }

} 