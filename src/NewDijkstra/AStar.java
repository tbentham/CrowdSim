package NewDijkstra;

import Dijkstra.Edge;
import WorldRepresentation.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;

public class AStar {

    // Total nodes on the map
    private int numNodes;
    // Length of one side of map
    private int sideLength;
    // List of NodeRecords converted from Nodes
    private ArrayList<NodeRecord> aNodes;
    // HashMap storing all connections to and from each node
    private HashMap<Integer, ArrayList<aConnection>> connections;
    // Key value A* Star has associated with each node
    private Double[] keys;
    // Multiplier to up or downscale the density aspect of the heuristic
    private static final int DENSITY_COEFF = 2;

    public AStar(int numNodes, ArrayList<Edge> edges, int sideLength) {
        this.numNodes = numNodes;
        this.sideLength = sideLength;
        createConnections(edges);
    }

    public Path getPath(Integer startNode, Integer goalX, Integer goalY, Integer goalFloor, int[][][] density) throws Exception {
        Integer goalNode = (goalFloor * sideLength * sideLength) + (goalX * sideLength) + goalY;
        NodeRecord nr = pathFind(startNode, goalNode, density);
        return pathFromNodeRecord(startNode, goalX, goalY, goalFloor, nr);
    }

    // Performs A* Star search from startNode to endNode using euclidean distance and density as heuristic
    private NodeRecord pathFind(Integer startNode, Integer goalNode, int[][][] density) throws Exception {

        // aNodes is a list of all nodes for this search
        createNodes();

        // Instantiate each key to be 10000 and the key for the start node as the euclidean distance to the goal
        keys = instantiateKeys(startNode, goalNode);

        // PriorityQueue is used to allow for efficiently retrieving the element with the smallest value
        PriorityQueue<NodeRecord> priorityQueue = instantiateQueue();

        int k;
        for (k = 0; k < numNodes; k++) {
            NodeRecord nr = priorityQueue.poll();

            double thisKey = nr.value;
            Integer i = nr.node;

            if (i == goalNode.intValue()) {
                return aNodes.get(i);

            }
            if (connections.get(nr.node) == null) {
                continue;
            }

            for (aConnection connection : connections.get(nr.node)) {

                NodeRecord toNodeRecord = aNodes.get(connection.getTo());

                double euTo = euclidDistance(sideLength, toNodeRecord.node, goalNode);
                double euCurr = euclidDistance(sideLength, i, goalNode);

                int z = i / (sideLength * sideLength);
                int x = Math.round((i % (sideLength * sideLength) / sideLength));
                int y = Math.round(i % sideLength);
                int currDensity = density[x][y][z];
                int nextDensity = density[toNodeRecord.node % (sideLength * sideLength) / sideLength][toNodeRecord.node % sideLength][z];

                //+ (DENSITY_COEFF * nextDensity) - (DENSITY_COEFF * currDensity)
                //Add remove curr density again
                if ((thisKey + connection.getCost() + euTo - euCurr + (DENSITY_COEFF * nextDensity) - (DENSITY_COEFF * currDensity)) < keys[toNodeRecord.node]) {

                    if (!priorityQueue.contains(toNodeRecord)) {
                        continue;
                    }

                    priorityQueue.remove(toNodeRecord);

                    toNodeRecord.value = (thisKey + connection.getCost() + euTo - euCurr + (DENSITY_COEFF * nextDensity) - (DENSITY_COEFF * currDensity));
                    toNodeRecord.predecessor = nr.node;
                    priorityQueue.add(toNodeRecord);
                    keys[toNodeRecord.node] = thisKey + connection.getCost() + euTo - euCurr + (DENSITY_COEFF * nextDensity) - (DENSITY_COEFF * currDensity);

                }
            }
        }
//        System.out.println("I have looped" + k + " times");
        throw new Exception("No Path Found from " + startNode + " to " + goalNode);
    }

    double euclidDistance(int sideLength, int from, int to) {
        int fromX = from / sideLength;
        int fromY = from % sideLength;
        int toX = to / sideLength;
        int toY = to % sideLength;

        double eu = (fromX - toX) * (fromX - toX) + (fromY - toY) * (fromY - toY);
        return Math.sqrt(eu);
    }

    private void createNodes() {
        this.aNodes = new ArrayList<NodeRecord>();
        for (int z = 0; z < numNodes / (sideLength * sideLength); z++) {
            for (int i = 0; i < sideLength; i++) {
                for (int j = 0; j < sideLength; j++) {
                    aNodes.add(new NodeRecord((z * sideLength * sideLength + +(i * sideLength) + j)));
                }
            }
        }
    }

    private void createConnections(ArrayList<Edge> edges) {
        this.connections = new HashMap<Integer, ArrayList<aConnection>>();
        ArrayList<Edge> wEdge = new ArrayList<Edge>();
        wEdge.addAll(edges);


        for (Edge e : edges) {
            wEdge.add(new Edge(e.getDestination(), e.getSource(), e.getWeight(), e.getFloor()));
        }

        for (Edge e : wEdge) {
            Integer source = (int) Math.round((e.getSource().getZ() * sideLength * sideLength) + (e.getSource().getX() * sideLength) + (e.getSource().getY()));
            Integer destination = (int) Math.round((e.getDestination().getZ() * sideLength * sideLength) + (e.getDestination().getX() * sideLength) + (e.getDestination().getY()));
            Double weight = e.getWeight();
            aConnection newConn = new aConnection(weight, source, destination, e.getFloor());


            ArrayList<aConnection> connections2;
            if (connections.containsKey(source)) {
                connections2 = connections.get(source);
            } else {
                connections2 = new ArrayList<aConnection>();
            }
            connections2.add(newConn);
            connections.put(source, connections2);
        }
    }

    // Traverse back from the goal node to the start node, build a path, reverse and return
    private Path pathFromNodeRecord(Integer startNode, Integer goalX, Integer goalY, Integer goalFloor, NodeRecord nr) {
        ArrayList<Node> nodeList = new ArrayList<Node>();
        nodeList.add(new Node(goalX, goalY, goalFloor));
        while (true) {
            Integer i = nr.predecessor;
            if (i.equals(startNode)) {
                break;
            }
            Integer prevFloor = i / (sideLength * sideLength);
            Integer prevX = aNodes.get(i).node / sideLength;
            Integer prevY = aNodes.get(i).node % sideLength;
            nodeList.add(new Node(prevX, prevY, prevFloor));
            nr = aNodes.get(i);
        }
        Collections.reverse(nodeList);
        //Generate subgoals.
        return new Path(nodeList);
    }

    private Double[] instantiateKeys(Integer startNode, Integer goalNode) {
        Double[] keyArray = new Double[numNodes];
        for (int i = 0; i < numNodes; i++) {
            if (i == startNode) {
                int x = (i / sideLength);
                int y = (i % sideLength);
                int goalX = (goalNode / sideLength);
                int goalY = (goalNode % sideLength);
                double euclid = Math.sqrt((y - goalY) * (y - goalY) + (x - goalX) * (x - goalX));
                aNodes.get(i).value = euclid;
                keyArray[i] = euclid;
            } else {
                aNodes.get(i).value = 10000.0;
                keyArray[i] = 10000.0;
            }
        }
        return keyArray;
    }

    private PriorityQueue<NodeRecord> instantiateQueue() {
        PriorityQueue<NodeRecord> priorityQueue = new PriorityQueue<NodeRecord>();
        for (int i = 0; i < numNodes; i++) {
            priorityQueue.add(aNodes.get(i));
        }
        return priorityQueue;
    }

    public HashMap<Integer, ArrayList<aConnection>> getConnections() {
        return connections;
    }

}
