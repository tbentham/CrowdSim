package Dijkstra;

// Represents edges as in edges of a graph
public class Edge {
    private final Vertex source;
    private final Vertex destination;
    private final double weight;
    private int floor;

    public Edge(Vertex source, Vertex destination, double weight, int floor) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
        this.floor = floor;
    }

    public Vertex getDestination() {
        return destination;
    }

    public Vertex getSource() {
        return source;
    }

    public double getWeight() {
        return weight;
    }

    public int getFloor() {
        return floor;
    }


}