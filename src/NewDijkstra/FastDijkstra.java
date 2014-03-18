package NewDijkstra;

import Dijkstra.Edge;
import WorldRepresentation.World;
import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;

import java.util.ArrayList;
import java.util.HashMap;

public class FastDijkstra {

    public HashMap<Integer, ArrayList<Connection>> connections;
    public Double[] keys;
    public ArrayList<FibonacciHeapNode> nodes;
    public ArrayList<NodeRecord> anodes;
    private static final int DENSITY_COEFF = 0;

    public FibonacciHeap pathFind(Integer startNode, int numNodes, World w) {

        System.out.println("Computing DJ towards " + startNode);
        System.out.println("German whip");
        nodes = new ArrayList<FibonacciHeapNode>();
        connections = new HashMap<Integer, ArrayList<Connection>>();

        for (int i = 0; i < Math.sqrt(numNodes); i++) {
            for (int j = 0; j < Math.sqrt(numNodes); j++) {
                FibonacciHeapNode newNode = new FibonacciHeapNode(new NodeRecord((i * (int) Math.sqrt(numNodes)) + j));
                nodes.add(newNode);
            }
        }

        ArrayList<Edge> wEdge = new ArrayList<Edge>();
        wEdge.addAll(w.getEdges());

        for (Edge e : w.getEdges()) {
            wEdge.add(new Edge(e.getDestination(), e.getSource(), e.getWeight(), e.getFloor()));
        }

        for (Edge e : wEdge) {
            Integer source = (int) Math.round((e.getSource().getX() * w.getSideLength()) + (e.getSource().getY()));
            Integer destination = (int) Math.round((e.getDestination().getX() * w.getSideLength()) + (e.getDestination().getY()));
            Double weight = e.getWeight();
            Connection newConn = new Connection(weight, nodes.get(source), nodes.get(destination));


            ArrayList<Connection> connections2;
            if (connections.containsKey(source)) {
                connections2 = connections.get(source);
            } else {
                connections2 = new ArrayList<Connection>();
            }
            connections2.add(newConn);
            connections.put(source, connections2);
        }


        FibonacciHeap fibonacciHeap = new FibonacciHeap();
        FibonacciHeapNode fibonacciHeapNode = new FibonacciHeapNode(new NodeRecord(startNode));
        //fibonacciHeap.insert(fibonacciHeapNode, 0);

        keys = new Double[numNodes];
        for (int i = 0; i < numNodes; i++) {
            if (i == startNode) {
                fibonacciHeap.insert(nodes.get(i), 0);
                keys[i] = 0.0;
            } else {
                fibonacciHeap.insert(nodes.get(i), 100000.0);
                keys[i] = 100000.0;
            }
        }
        if (fibonacciHeap.size() < 10000) {
            System.out.println(fibonacciHeap.size());
        }
        int k;
        for (k = 1; k < numNodes; k++) {
            FibonacciHeapNode currentHeapNode = fibonacciHeap.removeMin();
            NodeRecord nr = (NodeRecord) currentHeapNode.getData();
            Integer i = nr.node;
            keys[i] = currentHeapNode.getKey();
            if (connections.get(((NodeRecord) currentHeapNode.getData()).node) == null)
                continue;
            for (Connection connection : connections.get(nr.node)) {
                if ((currentHeapNode.getKey() + connection.cost) < keys[((NodeRecord) connection.to.getData()).node]) {
                    fibonacciHeap.decreaseKey(connection.to, (Double) (currentHeapNode.getKey() + connection.cost));
                    keys[((NodeRecord) connection.to.getData()).node] = (currentHeapNode.getKey() + connection.cost);
                    ((NodeRecord) connection.to.getData()).predecessor = ((NodeRecord) (currentHeapNode.getData())).node;
                }
            }
        }
        if (keys[0] == 100000.0) {
            System.out.println("k = " + k);
        }
        return fibonacciHeap;
    }


    double euclidDistance(int sideLength, int from, int to) {
        int fromX = from / sideLength;
        int fromY = from % sideLength;
        int toX = to / sideLength;
        int toY = to % sideLength;

        double eu = (fromX - toX) * (fromX - toX) + (fromY - toY) * (fromY - toY);
        return Math.sqrt(eu);
    }
}

