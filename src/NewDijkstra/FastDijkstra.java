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


    public FibonacciHeap pathFind(Integer startNode, int numNodes, World w) {

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
           wEdge.add(new Edge(e.getDestination(), e.getSource(), e.getWeight()));
        }


        for (Edge e : wEdge) {
            Integer source = (int) Math.round((e.getSource().getX() * w.getSideLength()) + (e.getSource().getY()));
            Integer destination = (int) Math.round((e.getDestination().getX() * w.getSideLength()) + (e.getDestination().getY()));
            Double weight = e.getWeight();
            Connection newConn = new Connection(weight, nodes.get(source), nodes.get(destination));


            ArrayList<Connection> connections2;
            if (connections.containsKey(source)) {
                connections2 = connections.get(source);
            }
            else {
                connections2 = new ArrayList<Connection>();
            }
            connections2.add(newConn);
            connections.put(source, connections2);
        }


        FibonacciHeap fibonacciHeap = new FibonacciHeap();
        FibonacciHeapNode fibonacciHeapNode = new FibonacciHeapNode(new NodeRecord(startNode));
        fibonacciHeap.insert(fibonacciHeapNode, 0);

        keys = new Double[numNodes];
        for (int i = 1; i < numNodes; i++) {
            fibonacciHeap.insert(nodes.get(i), 100000.0);
            keys[i] = 10000.0;

        }

        for (int k = 1; k < numNodes; k++) {
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
        return fibonacciHeap;
    }

//    public static void badBoyTest() {
//
//        connections = new HashMap<Integer, ArrayList<Connection>>();
//
//        nodes = new ArrayList<FibonacciHeapNode>();
//        nodes.add(new FibonacciHeapNode(0));
//        nodes.add(new FibonacciHeapNode(1));
//        nodes.add(new FibonacciHeapNode(2));
//        nodes.add(new FibonacciHeapNode(3));
//        nodes.add(new FibonacciHeapNode(4));
//        nodes.add(new FibonacciHeapNode(5));
//        nodes.add(new FibonacciHeapNode(6));
//
//
//        ArrayList<Connection> connections1 = new ArrayList<Connection>();
//
//
//        connections1.add(new Connection(1, nodes.get(0), nodes.get(1)));
//        connections1.add(new Connection(2, nodes.get(0), nodes.get(2)));
//        connections1.add(new Connection(4, nodes.get(0), nodes.get(4)));
//        connections.put(0, connections1);
//        connections1 = new ArrayList<Connection>();
//        connections1.add(new Connection(1, nodes.get(1), nodes.get(4)));
//        connections1.add(new Connection(1, nodes.get(1), nodes.get(2)));
//        connections.put(1, connections1);
//        connections1 = new ArrayList<Connection>();
//        connections1.add(new Connection(5, nodes.get(2), nodes.get(5)));
//        connections1.add(new Connection(1, nodes.get(2), nodes.get(3)));
//        connections.put(2, connections1);
//        connections1 = new ArrayList<Connection>();
//        connections1.add(new Connection(2, nodes.get(3), nodes.get(6)));
//        connections.put(3, connections1);
//        connections1 = new ArrayList<Connection>();
//        connections1.add(new Connection(6, nodes.get(6), nodes.get(5)));
//        connections.put(5, connections1);
//
//        FibonacciHeap fibonacciHeap = pathFind(0, 7);
//
//        System.out.println("d");
//
//    }
//
//    public static void main(String[] args) {
//
//        badBoyTest();
//
//    }

}
