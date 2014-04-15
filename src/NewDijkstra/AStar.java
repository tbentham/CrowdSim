package NewDijkstra;

import Dijkstra.Edge;
import WorldRepresentation.FloorConnection;
import WorldRepresentation.Path;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;

// Stores all the information required to, and then performs, A* Star search
public class AStar {

    // Total nodes on the map
    private int numNodes;
    // Length of one side of map
    private int sideLength;
    // List of NodeRecords converted from Nodes
    private ArrayList<NodeRecord> aNodes;
    // HashMap storing all connections to and from each node
    private HashMap<Integer, ArrayList<aConnection>> connections;
    // Multiplier to up or downscale the density aspect of the heuristic
    private static final int DENSITY_COEFF = 2;

    private FloorConnection floorConnection;

    public AStar(int numNodes, ArrayList<Edge> edges, int sideLength) {
        this.numNodes = numNodes;
        this.sideLength = sideLength;
        createConnections(edges);
    }

    // Returns the path from the startNode to the goal by calling pathFind and constructing the path from the nodeRecord
    public Path getPath(Integer startNode, Integer goalX, Integer goalY,
                        Integer goalFloor, int[][][] density, ArrayList<FloorConnection> floorConnections) throws Exception {
        // Finds the closest floor connection that can be used
        if (floorConnections.size() > 0) {
            this.floorConnection = closestFloorConnection(floorConnections, startNode);
        }
        Integer goalNode = (goalFloor * sideLength * sideLength) + (goalX * sideLength) + goalY;
        NodeRecord nr = pathFind(startNode, goalNode, density);
        return pathFromNodeRecord(startNode, goalX, goalY, goalFloor, nr);
    }

    // Performs A* Star search from startNode to endNode using euclidean distance and density as heuristic
    private NodeRecord pathFind(Integer startNode, Integer goalNode, int[][][] density) throws Exception {

        // aNodes is a list of all nodes for this search
        createNodes();

        // Instantiate each key to be 10000 and the key for the start node as the euclidean distance to the goal
        Double[] keys = instantiateKeys(startNode, goalNode);

        // PriorityQueue is used to allow for efficiently retrieving the element with the smallest value
        PriorityQueue<NodeRecord> priorityQueue = instantiateQueue();

        for (int k = 0; k < numNodes; k++) {

            // Pulls the node with the smallest key value
            NodeRecord currentNodeRecord = priorityQueue.poll();

            // Grabs the associated key value
            double thisKey = currentNodeRecord.value;

            // Grabs the associated node
            Integer currentNode = currentNodeRecord.node;

            // If we have searched to the goal node, search has completed so return
            if (currentNode == goalNode.intValue()) {
                return aNodes.get(currentNode);
            }

            // Calculate the euclidean distance from the goal to the current node
            double euCurr = euclidDistance(sideLength, currentNode, goalNode);

            // Convert the current node value to it's corresponding (x, y, z) coordinates
            int z = currentNode / (sideLength * sideLength);
            int x = Math.round((currentNode % (sideLength * sideLength) / sideLength));
            int y = Math.round(currentNode % sideLength);

            // Calculates the Euclidean distance from the current position to the stairs
            // and then from the stairs to the goal
            if (floorConnection != null && z != goalNode / (sideLength * sideLength)) {
                euCurr = euclidDistance(sideLength, currentNode, (int) floorConnection.location.x * sideLength + (int) floorConnection.location.y);
                euCurr += euclidDistance(sideLength, (int) floorConnection.location.x * sideLength + (int) floorConnection.location.y, goalNode);
            }

            // Use the (x, y, z) coordinates to find the density at the current point
            int currDensity = density[x][y][z];

            if (connections.get(currentNodeRecord.node) == null) {
                priorityQueue.remove(currentNodeRecord);
                continue;
            }

            // Loop through each node connected to the current node
            for (aConnection connection : connections.get(currentNodeRecord.node)) {

                // Grab the associated node record
                NodeRecord toNodeRecord = aNodes.get(connection.getTo());

                // Calculate the euclidean distance from the goal to the connected node
                double euTo = euclidDistance(sideLength, toNodeRecord.node, goalNode);

                // Convert the connected node into it's (x, y, z) coordinates
                int toNodeRecordX = toNodeRecord.node % (sideLength * sideLength) / sideLength;
                int toNodeRecordY = toNodeRecord.node % sideLength;
                int toNodeRecordZ = toNodeRecord.node / (sideLength * sideLength);

                // Calculates the Euclidean distance from the current position to the stairs
                // and then from the stairs to the goal
                if (floorConnection != null && toNodeRecordZ != (goalNode / (sideLength * sideLength))) {
                    euTo = euclidDistance(sideLength, toNodeRecord.node, (int) floorConnection.location.x * sideLength + (int) floorConnection.location.y);
                    euTo += euclidDistance(sideLength, (int) floorConnection.location.x * sideLength + (int) floorConnection.location.y, goalNode);
                }

                // Grab the density at the connected node
                int nextDensity = density[toNodeRecordX][toNodeRecordY][toNodeRecordZ];

                // Calculate the new key for the connected node
                double newKeyValue = thisKey + connection.getCost() + euTo - euCurr +
                        (DENSITY_COEFF * nextDensity) - (DENSITY_COEFF * currDensity);

                // Only update the key if it is a improvement
                if (newKeyValue < keys[toNodeRecord.node]) {

                    // Avoid adding duplicated NodeRecords
                    if (!priorityQueue.contains(toNodeRecord)) {
                        continue;
                    }

                    // Remove connected node to be modified
                    priorityQueue.remove(toNodeRecord);

                    // Update key stored in NodeRecord and keys array for quick access
                    toNodeRecord.value = newKeyValue;
                    keys[toNodeRecord.node] = newKeyValue;
                    // Set the path for the connected node to be the path using the current node
                    toNodeRecord.predecessor = currentNodeRecord.node;
                    // Put the updated node back into the queue
                    priorityQueue.add(toNodeRecord);

                }
            }
        }
        throw new Exception("No Path Found from " + startNode + " to " + goalNode);
    }

    // Simply calculates the euclidean distance between two points
    private double euclidDistance(int sideLength, int from, int to) {
        int fromX = (from % (sideLength * sideLength)) / sideLength;
        int fromY = from % sideLength;
        int toX = (to % (sideLength * sideLength)) / sideLength;
        int toY = to % sideLength;

        double eu = (fromX - toX) * (fromX - toX) + (fromY - toY) * (fromY - toY);
        return Math.sqrt(eu);
    }

    // Instantiate the aNodes list to be a list of all nodes on the map
    private void createNodes() {
        this.aNodes = new ArrayList<NodeRecord>();
        for (int z = 0; z < numNodes / (sideLength * sideLength); z++) {
            for (int i = 0; i < sideLength; i++) {
                for (int j = 0; j < sideLength; j++) {
                    aNodes.add(new NodeRecord((z * sideLength * sideLength) + (i * sideLength) + j));
                }
            }
        }
    }

    // Create list of connections between integer nodes from list of edges between coordinate points
    private void createConnections(ArrayList<Edge> edges) {
        this.connections = new HashMap<Integer, ArrayList<aConnection>>();
        ArrayList<Edge> wEdge = new ArrayList<Edge>();
        wEdge.addAll(edges);

        // Add the reverse of every edge to the list, simulating bidirectional edges
        for (Edge e : edges) {
            wEdge.add(new Edge(e.getDestination(), e.getSource(), e.getWeight(), e.getFloor()));
        }

        for (Edge e : wEdge) {
            Integer source = (int) Math.round((e.getSource().getZ() * sideLength * sideLength)
                    + (e.getSource().getX() * sideLength) + (e.getSource().getY()));
            Integer destination = (int) Math.round((e.getDestination().getZ() * sideLength * sideLength)
                    + (e.getDestination().getX() * sideLength) + (e.getDestination().getY()));
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
            if (i == null) {
                break;
            }
            if (i.equals(startNode)) {
                break;
            }
            Integer prevFloor = i / (sideLength * sideLength);
            Integer prevX = (aNodes.get(i).node % (sideLength * sideLength)) / sideLength;
            Integer prevY = aNodes.get(i).node % sideLength;
            nodeList.add(new Node(prevX, prevY, prevFloor));
            nr = aNodes.get(i);
        }
        Collections.reverse(nodeList);
        //Generate subgoals.
        return new Path(nodeList);
    }

    // Instantiate each key to be 10000 and the key for the start node as the euclidean distance to the goal
    private Double[] instantiateKeys(Integer startNode, Integer goalNode) {
        Double[] keyArray = new Double[numNodes];
        for (int i = 0; i < numNodes; i++) {
            if (i == startNode) {
                int x = (i % (sideLength * sideLength)) / sideLength;
                int y = (i % sideLength);
                int goalX = (goalNode % (sideLength * sideLength) / sideLength);
                int goalY = (goalNode % sideLength);
                double euclid = Math.sqrt((y - goalY) * (y - goalY) + (x - goalX) * (x - goalX));
                if (floorConnection != null) {
                    double euclidToStairs = floorConnection.location.distance(new Point2d(x, y));
                    double euclidFromStairs = floorConnection.location.distance(new Point2d(goalX, goalY));
                    euclid = euclidFromStairs + euclidToStairs;
                }
                aNodes.get(i).value = euclid;
                keyArray[i] = euclid;
            } else {
                aNodes.get(i).value = 10000.0;
                keyArray[i] = 10000.0;
            }
        }
        return keyArray;
    }

    // Returns the closest staircase to the given node
    private FloorConnection closestFloorConnection(ArrayList<FloorConnection> floorConnections, Integer startNode) {
        Integer startX = (startNode % (sideLength * sideLength)) / sideLength;
        Integer startY = startNode % sideLength;
        Double min = floorConnections.get(0).location.distance(new Point2d(startX, startY));
        Integer id = 0;
        for (int i = 1; i < floorConnections.size(); i++) {
            Double dist = floorConnections.get(i).location.distance(new Point2d(startX, startY));
            if (dist < min) {
                min = dist;
                id = i;
            }
        }
        return floorConnections.get(id);
    }

    // Put each NodeRecord into priorityQueue
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
