package NewDijkstra;

import org.jgrapht.util.FibonacciHeapNode;

public class Connection {

    public double cost;
    public FibonacciHeapNode from;
    public FibonacciHeapNode to;

    public Connection(double cost, FibonacciHeapNode from, FibonacciHeapNode to) {
        this.cost = cost;
        this.from = from;
        this.to = to;
    }


    public double getCost() {
        return cost;
    }

}
