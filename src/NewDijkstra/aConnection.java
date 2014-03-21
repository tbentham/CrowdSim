package NewDijkstra;

// Represents edges between Nodes where Nodes are represented as Integers
public class aConnection {

    private double cost;
    private Integer from;
    private Integer to;
    private int floor;

    public aConnection(double cost, Integer from, Integer to, int floor) {
        this.floor = floor;
        this.cost = cost;
        this.from = from;
        this.to = to;
    }

    public double getCost() {
        return cost;
    }

    public Integer getFrom() {
        return from;
    }

    public Integer getTo() {
        return to;
    }

    public int getFloor() {
        return floor;
    }

}
