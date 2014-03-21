package NewDijkstra;

import Dijkstra.Edge;
import Dijkstra.Vertex;
import WorldRepresentation.Path;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;

public class AStar {

    public int numNodes;
    public Vertex[][][] chunkNodes;
    public ArrayList<Edge> edges;
    public int sideLength;
    public ArrayList<NodeRecord> aNodes;
    public HashMap<Integer, ArrayList<aConnection>> connections;
    public Double[] keys;
    private static final int DENSITY_COEFF = 2;

    public AStar(int numNodes, Vertex[][][] chunkNodes, ArrayList<Edge> edges, int sideLength) {
        this.numNodes = numNodes;
        this.chunkNodes = chunkNodes;
        this.edges = edges;
        this.sideLength = sideLength;
        createConnections(edges);
    }

    public Path getPath(Integer startNode, Integer goalX, Integer goalY, Integer goalFloor, int[][][] density) throws Exception {
        Integer goalNode = (goalFloor * sideLength * sideLength) + (goalX * sideLength) + goalY;
        NodeRecord nr = pathFind(startNode, goalNode, density);
        // Create path from node
        ArrayList<Node> nodeList = new ArrayList<Node>();
        nodeList.add(new Node(goalX, goalY, goalFloor));
        while (true) {
            Integer i = nr.predecessor;
            if (i == startNode || i == null) {
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

    private NodeRecord pathFind(Integer startNode, Integer goalNode, int[][][] density) throws Exception {

        // aNodes is a list of all nodes for this search
        createNodes();

        // TreeBidiMap<Double, NodeRecord> treeBidiMap = new TreeBidiMap<Double, NodeRecord>();
        PriorityQueue<NodeRecord> priorityQueue = new PriorityQueue<NodeRecord>();

        keys = new Double[numNodes];
        for (int i = 0; i < numNodes; i++) {
            if (i == startNode) {
                int x = (int) (i / sideLength);
                int y = (int) (i % sideLength);
                int goalX = (int) (goalNode / sideLength);
                int goalY = (int) (goalNode % sideLength);
                double euclid = Math.sqrt((y - goalY) * (y - goalY) + (x - goalX) * (x - goalX));
                aNodes.get(i).value = euclid;
                priorityQueue.add(aNodes.get(i));
                // treeBidiMap.put(euclid, aNodes.get(i));
                keys[i] = euclid;
            } else {
                //treeBidiMap.put(10000.0, aNodes.get(i));
                aNodes.get(i).value = 10000.0;
                priorityQueue.add(aNodes.get(i));
                keys[i] = 10000.0;
            }
        }

        int k;
        for (k = 0; k < numNodes; k++) {
            NodeRecord currentHeapNode = priorityQueue.poll();

            double thisKey = currentHeapNode.value;


            NodeRecord nr = currentHeapNode;
            Integer i = nr.node;

            if (i == goalNode.intValue()) {
                return aNodes.get(i);

            }
            if (connections.get(nr.node) == null) {
                continue;
            }

            for (aConnection connection : connections.get(nr.node)) {

                NodeRecord toNodeRecord = aNodes.get(connection.to);

                double euTo = euclidDistance(sideLength, toNodeRecord.node, goalNode);
                double euCurr = euclidDistance(sideLength, i, goalNode);

                int z = i / (sideLength * sideLength);
                int x = (int) Math.round((i % (sideLength * sideLength) / sideLength));
                int y = (int) Math.round(i % sideLength);
                int currDensity = density[x][y][z];
                int nextDensity = density[toNodeRecord.node % (sideLength * sideLength) / sideLength][toNodeRecord.node % sideLength][z];

                //+ (DENSITY_COEFF * nextDensity) - (DENSITY_COEFF * currDensity)
                //Add remove curr density again
                if ((thisKey + connection.cost + euTo - euCurr + (DENSITY_COEFF * nextDensity) - (DENSITY_COEFF * currDensity)) < keys[toNodeRecord.node]) {

                    if (!priorityQueue.contains(toNodeRecord)) {
                        continue;
                    }

                    priorityQueue.remove(toNodeRecord);

                    toNodeRecord.value = (thisKey + connection.cost + euTo - euCurr + (DENSITY_COEFF * nextDensity) - (DENSITY_COEFF * currDensity));
                    toNodeRecord.predecessor = nr.node;
                    priorityQueue.add(toNodeRecord);
                    keys[toNodeRecord.node] = thisKey + connection.cost + euTo - euCurr + (DENSITY_COEFF * nextDensity) - (DENSITY_COEFF * currDensity);

                }
            }
        }
//        System.out.println("I have looped" + k + " times");
        throw new Exception("No Path Found from " + startNode + " to " + goalNode);
    }

    void dumpDensityToFile(int[][] density) throws Exception {
        PrintWriter writer = new PrintWriter("density.txt", "UTF-8");
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                writer.print(density[i][j]);
                writer.print(" ");
            }
            writer.println("");
        }
        writer.close();

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
}
