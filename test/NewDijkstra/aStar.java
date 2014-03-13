package NewDijkstra;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;

import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;
import org.junit.Before;
import org.junit.Test;

import WorldRepresentation.Path;
import WorldRepresentation.Person;
import WorldRepresentation.World;

public class aStar {

	Integer goalNode;
	Integer startNode;
	int sideLength;
	World w;
	int[][] densityMap;
	FastDijkstra fd;
	
	@Before
	public void setUp() throws Exception {
		
		fd = new FastDijkstra();
    	
    	goalNode = 0;
    	startNode = 7050;
    	sideLength = 100;
    	w = new World(100);
    	w.addWall(3, 2, 30, 80);
    	w.setUp();
    	densityMap = new int[sideLength][sideLength]; 
    	for(int i = 0; i < 9; i++ ) {
    		densityMap[i][3] = 10;
    		densityMap[i][4] = 8;
    	}
	}

	@Test
	public void aStarTest() {
		try{
			w.printFloorPlan();
			FibonacciHeapNode fn = fd.astar(startNode, 0, sideLength*sideLength, w.getNodeArray(), (ArrayList) w.getEdges(), densityMap, sideLength);
			
			FibonacciHeap fh = fd.pathFind(startNode, sideLength*sideLength, w);
			FibonacciHeapNode fibonacciHeapNode = fd.nodes.get((0 * sideLength) + 0);
			 NodeRecord nr = (NodeRecord) fibonacciHeapNode.getData();
		        ArrayList<Node> nodeList = new ArrayList<Node>();
		        while (true) {
		            if (nr.predecessor == null) {
		                // nodeList = new ArrayList<Node>();
		                break;
		            }
		            Integer i = nr.predecessor;
		            Integer prevX = ((NodeRecord) fd.nodes.get(i).getData()).node / sideLength;
	  	            Integer prevY = ((NodeRecord) fd.nodes.get(i).getData()).node % sideLength;
		            if (prevX == 0 && prevY == 0) {
		                nodeList.add(new Node(0, 0));
		                break;
		            }
		            else {
		                nodeList.add(new Node(prevX, prevY));
		                nr = (NodeRecord) fd.nodes.get(i).getData();
		            }
		        }
		        for (Node n : nodeList) {
		        	System.out.println(n.x + ", " + n.y);
		         }
		        
			if(fn == null) {
				fail("Path not found");
			}
			else {
				NodeRecord n = (NodeRecord) fn.getData();
				while(n.predecessor != null) {
					
					System.out.println(n.predecessor);
					n = (NodeRecord) fd.nodes.get(n.predecessor).getData();
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			fail("Exception");
		}
		
	}

}
