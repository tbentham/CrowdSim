package NewDijkstra;

public class aConnection {

    public double cost;
    public Integer from;
    public Integer to;

    public aConnection(double cost, Integer from, Integer to) {
        this.cost = cost;
        this.from = from;
        this.to = to;
    }


    public double getCost() {
        return cost;
    }

}
