package NewDijkstra;

import Dijkstra.Edge;
import WorldRepresentation.World;
import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;

import java.util.ArrayList;
import java.util.HashMap;

// Performs Dijkstra's algorithm from a specific goal node to each point on a map
public class FastDijkstra {

    private HashMap<Integer, ArrayList<Connection>> connections;
    private Double[] keys;
    private ArrayList<FibonacciHeapNode> nodes;

    public FastDijkstra() {
        connections = new HashMap<Integer, ArrayList<Connection>>();
        nodes = new ArrayList<FibonacciHeapNode>();
    }

    public void pathFind(Integer startNode, int numNodes, World w) {

        System.out.println("Computing Dijkstra towards " + startNode);
        // Ensure we are starting from empty nodes and connections
        nodes = new ArrayList<FibonacciHeapNode>();
        connections = new HashMap<Integer, ArrayList<Connection>>();

        // Create a FibonacciHeapNode for each NodeRecord on the map and add it to nodes
        // FibonacciHeapNode is used to ensure time is O(|E| + |V|log|V|)
        for (int z = 0; z < numNodes / (w.sideLength * w.sideLength); z++) {
            for (int i = 0; i < w.sideLength; i++) {
                for (int j = 0; j < w.sideLength; j++) {
                    FibonacciHeapNode newNode = new FibonacciHeapNode(new NodeRecord(z * (w.sideLength * w.sideLength) +
                            (i * w.sideLength) + j));
                    nodes.add(newNode);
                }
            }
        }

        // Add all the edges on the map to wEdge
        ArrayList<Edge> wEdge = new ArrayList<Edge>();
        wEdge.addAll(w.getEdges());

        // Add the reverse of every edge to the list, simulating bidirectional edges
        for (Edge e : w.getEdges()) {
            wEdge.add(new Edge(e.getDestination(), e.getSource(), e.getWeight(), e.getFloor()));
        }

        // Convert each Edge into a Connection between integer nodes
        for (Edge e : wEdge) {
            Integer source = (int) Math.round((e.getSource().getZ() * w.sideLength * w.sideLength) +
                    (e.getSource().getX() * w.getSideLength()) + (e.getSource().getY()));
            Integer destination = (int) Math.round((e.getDestination().getZ() * w.sideLength * w.sideLength) +
                    (e.getDestination().getX() * w.getSideLength()) + (e.getDestination().getY()));
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

        // Instantiate each key to be some large value and the key at the start node to be 0
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

        for (int k = 0; k < numNodes; k++) {
            // Grab node with lowest value
            FibonacciHeapNode currentHeapNode = fibonacciHeap.removeMin();
            // Get NodeRecord out of FibonacciHeapNode
            NodeRecord currentNodeRecord = (NodeRecord) currentHeapNode.getData();
            // Get Integer value of NodeRecord
            Integer currentNodeValue = currentNodeRecord.node;
            // Grab key for this Node
            keys[currentNodeValue] = currentHeapNode.getKey();
            // Ignore nodes with no connections
            if (connections.get(((NodeRecord) currentHeapNode.getData()).node) == null)
                continue;

            // Loop over all nodes connected to this node
            for (Connection connection : connections.get(currentNodeRecord.node)) {
                // Calculate new key value
                double newKey = currentHeapNode.getKey() + connection.getCost();
                // Only update key if it is an improvement
                if (newKey < keys[((NodeRecord) connection.getTo().getData()).node]) {
                    // Update key in fibonacci heap
                    fibonacciHeap.decreaseKey(connection.getTo(), (newKey));
                    // Update key in key array for quick access
                    keys[((NodeRecord) connection.getTo().getData()).node] = (newKey);
                    // Set shortest path to use path to current node
                    ((NodeRecord) connection.getTo().getData()).predecessor = ((NodeRecord) (currentHeapNode.getData())).node;
                }
            }
        }
    }

    public ArrayList<FibonacciHeapNode> getNodes() {
        return nodes;
    }

    public HashMap<Integer, ArrayList<Connection>> getConnections() {
        return connections;
    }
}

