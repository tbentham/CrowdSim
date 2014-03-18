package Dijkstra;

public class Edge {
    private final String id;
    private final Vertex source;
    private final Vertex destination;
    private final double weight;
    private int floor;

    public Edge(Vertex source, Vertex destination, double weight, int floor) {
        this.id = source.getId() + "_" + destination.getId();
        this.source = source;
        this.destination = destination;
        this.weight = weight;
        this.floor = floor;
    }

    public String getId() {
        return id;
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

    @Override
    public String toString() {
        return source + " " + destination;
    }

    public int getFloor() {
        return floor;
    }


}