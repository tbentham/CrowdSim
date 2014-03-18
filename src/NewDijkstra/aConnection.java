package NewDijkstra;

public class aConnection {

    public double cost;
    public Integer from;
    public Integer to;
    public int floor;

    public aConnection(double cost, Integer from, Integer to, int floor) {
        this.floor = floor;
        this.cost = cost;
        this.from = from;
        this.to = to;
    }


    public double getCost() {
        return cost;
    }

}
