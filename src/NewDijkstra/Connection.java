package NewDijkstra;

import org.jgrapht.util.FibonacciHeapNode;

// Used to represent connections in FastDijkstra, since connections need to be referenced
// by FibonacciHeapNodes
public class Connection {

    private double cost;
    private FibonacciHeapNode from;
    private FibonacciHeapNode to;

    public Connection(double cost, FibonacciHeapNode from, FibonacciHeapNode to) {
        this.cost = cost;
        this.from = from;
        this.to = to;
    }

    public double getCost() {
        return cost;
    }

    public FibonacciHeapNode getFrom() {
        return from;
    }

    public FibonacciHeapNode getTo() {
        return to;
    }

}
