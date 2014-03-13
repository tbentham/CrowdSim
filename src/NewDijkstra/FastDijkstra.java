package NewDijkstra;

import Dijkstra.Edge;
import Dijkstra.Vertex;
import WorldRepresentation.World;

import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;

import java.util.ArrayList;
import java.util.HashMap;

public class FastDijkstra {

    public HashMap<Integer, ArrayList<Connection>> connections;
    public Double[] keys;
    public ArrayList<FibonacciHeapNode> nodes;
    private static final int DENSITY_COEFF = 0; 


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
        //fibonacciHeap.insert(fibonacciHeapNode, 0);

        keys = new Double[numNodes];
        for (int i = 0; i < numNodes; i++) {
            if (i == startNode) {
                fibonacciHeap.insert(nodes.get(i), 0);
                keys[i] = 0.0;
            }
            else {
                fibonacciHeap.insert(nodes.get(i), 100000.0);
                keys[i] = 10000.0;
            }
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
    
    public FibonacciHeapNode astar(Integer startNode, Integer goalNode, int numNodes, Vertex[][] chunkNodes, ArrayList<Edge> edges, int[][] density, int sideLength) {

        nodes = new ArrayList<FibonacciHeapNode>();
        connections = new HashMap<Integer, ArrayList<Connection>>();

        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                FibonacciHeapNode newNode = new FibonacciHeapNode(new NodeRecord(i * sideLength + j));
                nodes.add(newNode);
            }
        }

        ArrayList<Edge> wEdge = new ArrayList<Edge>();
        wEdge.addAll(edges);

        for (Edge e : edges) {
           wEdge.add(new Edge(e.getDestination(), e.getSource(), e.getWeight()));
        }


        for (Edge e : wEdge) {
            Integer source = (int) Math.round((e.getSource().getX() * sideLength) + (e.getSource().getY()));
            Integer destination = (int) Math.round((e.getDestination().getX() * sideLength) + (e.getDestination().getY()));
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
//        FibonacciHeapNode fibonacciHeapNode = new FibonacciHeapNode(new NodeRecord(startNode));
        //fibonacciHeap.insert(fibonacciHeapNode, 0);

        keys = new Double[numNodes];
        for (int i = 0; i < numNodes; i++) {
            if (i == startNode) {
                fibonacciHeap.insert(nodes.get(i), 0);
                keys[i] = 0.0;
            }
            else {
            	int x = (int) (i / sideLength);
            	int y = (int) (i % sideLength);
            	int goalX = (int) (goalNode / sideLength);
            	int goalY = (int) (goalNode % sideLength);
            	
            	int densityVal = density[x][y];
            	double euclid = Math.sqrt((y - goalY)*(y - goalY) + (x - goalX)*(x - goalX));
                fibonacciHeap.insert(nodes.get(i), euclid + 1000);
                keys[i] = euclid + 1000;
            }
        }

        
        int goalX = startNode / sideLength;
        int goalY = startNode % sideLength;
        
        int personX = goalNode / sideLength;
        int personY = goalNode % sideLength;
        
        int[][] considered = new int[10][10];
        
        int k;
        for (k = 0; k < numNodes; k++) {
        	
        	
            if ( fibonacciHeap.isEmpty()) {
            	System.out.println("K is" + k);
            } else{
            	System.out.println(fibonacciHeap.size() + " k is " + k);
            }
            
            FibonacciHeapNode currentHeapNode = fibonacciHeap.removeMin();

            NodeRecord nr = (NodeRecord) currentHeapNode.getData();
            Integer i = nr.node;
            System.out.println("I am on node:" + i);
            //            keys[i] = currentHeapNode.getKey();
            if (i == goalNode) {
            	System.out.println("Laterz");
            	return nodes.get(i);
           		
         	}
            if (connections.get(((NodeRecord) currentHeapNode.getData()).node) == null){ 
            	System.out.println("I am continuing");
                continue;
            }
        	
            for (Connection connection : connections.get(nr.node)) {

            	double euTo = euclidDistance(sideLength, ((NodeRecord) connection.to.getData()).node, goalNode);
            	double euCurr = euclidDistance(sideLength, i, goalNode);

            	int x = (int) (i / sideLength);
            	int y = (int) (i % sideLength);
            	
            	int currDensity = density[x][y];
            	int nextDensity = density[((NodeRecord) connection.to.getData()).node / sideLength][((NodeRecord) connection.to.getData()).node % sideLength];
            	
            	//+ (DENSITY_COEFF*nextDensity) - (currDensity*DENSITY_COEFF)
                if ((currentHeapNode.getKey() + connection.cost + euTo - euCurr ) < keys[((NodeRecord) connection.to.getData()).node]) {
                	
                    fibonacciHeap.decreaseKey(connection.to, (Double) (currentHeapNode.getKey() + connection.cost + euTo - euCurr));
                    keys[((NodeRecord) connection.to.getData()).node] = (currentHeapNode.getKey() + connection.cost + euTo - euCurr);
                    ((NodeRecord) connection.to.getData()).predecessor = ((NodeRecord) (currentHeapNode.getData())).node;
                }
            }
            System.out.println("im out");

        }
        System.out.println("I have looped" + k + " times");
      return null;
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
