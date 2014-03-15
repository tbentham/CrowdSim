package WorldRepresentation;

import Dijkstra.Edge;
import Dijkstra.Vertex;
import NewDijkstra.AStar;
import NewDijkstra.Node;

import javax.vecmath.Point2d;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;

public class LayoutChunk implements Runnable {

    private ArrayList<Wall> lWalls;
    private ArrayList<Wall> gWalls;
    private double topYBoundary;
    private double bottomYBoundary;
    private double rightXBoundary;
    private double leftXBoundary;
    private ArrayList<Person> people;
    private ArrayList<Person> overlapPeople;
    boolean finished;
    private CyclicBarrier barrier;
    private int steps;
    private int[][] floorPlan;
    private int[][] densityMap;
    private World w;
    private ArrayList<Edge> edges;
    private Vertex[][] nodes;
    LayoutChunk[][] chunks;
    public LinkedBlockingQueue<Person> q;
    public LinkedBlockingQueue<Person> qOverlap;
    public int sideLength;
    private ArrayList<Person> allPeople;
    private AStar aStar;
    
//    private HashMap<Point2d, Queue<Person>> queues;
//    private Queue<Person> newPeople;
    
    public LayoutChunk(double leftXBoundary, double rightXBoundary, double topYBoundary, double bottomYBoundary, ArrayList<Wall> walls, CyclicBarrier barrier, int steps, World w) {
        System.out.println("LayoutChunk Created");
    	people = new ArrayList<Person>();
        overlapPeople = new ArrayList<Person>();
        this.topYBoundary = topYBoundary;
        this.bottomYBoundary = bottomYBoundary;
        this.leftXBoundary = leftXBoundary;
        this.rightXBoundary = rightXBoundary;
        nodes = new Vertex[w.getSideLength()][w.getSideLength()];
        edges = new ArrayList<Edge>();
        lWalls = new ArrayList<Wall>();
        sideLength = w.getSideLength();
        floorPlan = new int[w.getSideLength()][w.getSideLength()];
        gWalls = walls;
        densityMap = new int[w.getSideLength()][w.getSideLength()];
        finished = false;
        this.w = w;
        this.barrier = barrier;
        this.steps = steps;
        q = new LinkedBlockingQueue<Person>();
        qOverlap = new LinkedBlockingQueue<Person>();
        
        populateFloorPlan();
        createEdges();
        aStar = new AStar(sideLength * sideLength, nodes, edges, sideLength);
        if(topYBoundary == 50 && leftXBoundary == 0) {
        	printFloorPlan();
        }
    }

    public void addWall(double x1, double y1, double x2, double y2) {
        lWalls.add(new Wall(x1, y1, x2, y2));
    }

    public ArrayList<Wall> getWalls() {
        return lWalls;
    }

    public void addPerson(Person p) {
        people.add(p);
    }

    public ArrayList<Person> getPeople() {
        return people;
    }

    public boolean isPointInside(double x, double y) {
        return (y <= topYBoundary && y >= bottomYBoundary && x <= rightXBoundary && x >= leftXBoundary);
    }

    public boolean intersectsTop(Wall w) {
        return (w.intersects(new Point2d(leftXBoundary, topYBoundary), new Point2d(rightXBoundary, topYBoundary)));
    }

    public boolean intersectsBottom(Wall w) {
        return (w.intersects(new Point2d(leftXBoundary, bottomYBoundary), new Point2d(rightXBoundary, bottomYBoundary)));
    }

    public boolean intersectsLeft(Wall w) {
        return (w.intersects(new Point2d(leftXBoundary, bottomYBoundary), new Point2d(leftXBoundary, topYBoundary)));
    }

    public boolean intersectsRight(Wall w) {
        return (w.intersects(new Point2d(rightXBoundary, bottomYBoundary), new Point2d(rightXBoundary, topYBoundary)));
    }

    public int numberOfIntersects(Wall w) {
        int num = 0;
        if (intersectsBottom(w)) {
            num++;
        }
        if (intersectsTop(w)) {
            num++;
        }
        if (intersectsRight(w)) {
            num++;
        }
        if (intersectsLeft(w)) {
            num++;
        }
        return num;
    }
    
    public void addChunks(LayoutChunk[][] chunks) {
    	this.chunks = chunks;
    }
    
    public void putPerson(Person p) {
    	try {
			this.q.put(p);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    private void addPeople() {
    	while(!q.isEmpty()){
    		this.people.add(q.poll());
    	}
    }
    
    private ArrayList<Person> peopleLeftEdge() {
    	ArrayList<Person> ret = new ArrayList<Person>();
    	for (Person p : people) {
    		if (p.getLocation() != null && p.getLocation().x - leftXBoundary < 10) {
    			ret.add(p);
    		}
    	}
    	return ret;
    }
    
    private ArrayList<Person> peopleTopEdge() {
    	ArrayList<Person> ret = new ArrayList<Person>();
    	for (Person p : people) {
    		if (p.getLocation() != null && topYBoundary - p.getLocation().y < 10) {
    			ret.add(p);
    		}
    	}
    	return ret;
    }
    
    private ArrayList<Person> peopleRightEdge() {
    	ArrayList<Person> ret = new ArrayList<Person>();
    	for (Person p : people) {
    		if (p.getLocation() != null && rightXBoundary - p.getLocation().x < 10) {
    			ret.add(p);
    		}
    	}
    	return ret;
    }
    
    private ArrayList<Person> peopleBottomEdge() {
    	ArrayList<Person> ret = new ArrayList<Person>();
    	for (Person p : people) {
    		if (p.getLocation() != null && p.getLocation().y - bottomYBoundary < 10) {
    			ret.add(p);
    		}
    	}
    	return ret;
    }
    
    private void sendLeftOverlap(){
    	ArrayList<Person> l = peopleLeftEdge();
    	
    	int xIndex = (int) leftXBoundary / 50;
    	int yIndex = (int) bottomYBoundary / 50;
    	
    	if (xIndex == 0) {
    		return;
    	}
    	chunks[xIndex - 1][yIndex].qOverlap.addAll(l);
    }
    
    private void sendRightOverlap(){
    	ArrayList<Person> r = peopleRightEdge();
    	
    	int xIndex = (int) leftXBoundary / 50;
    	int yIndex = (int) bottomYBoundary / 50;
    	
    	if (xIndex == chunks.length - 1) {
    		return;
    	}
    	chunks[xIndex + 1][yIndex].qOverlap.addAll(r);
    }
    
    private void sendTopOverlap(){
    	ArrayList<Person> t = peopleTopEdge();
    	
    	int xIndex = (int) leftXBoundary / 50;
    	int yIndex = (int) bottomYBoundary / 50;
    	
    	if (yIndex == chunks.length - 1) {
    		return;
    	}
    	chunks[xIndex][yIndex + 1].qOverlap.addAll(t);
    }
    
    private void sendBottomOverlap(){
    	ArrayList<Person> b = peopleLeftEdge();
    	
    	int xIndex = (int) leftXBoundary / 50;
    	int yIndex = (int) bottomYBoundary / 50;
    	
    	if (yIndex == 0) {
    		return;
    	}
    	chunks[xIndex][yIndex - 1].qOverlap.addAll(b);
    }
    
    private void addOverlapPeople() {
    	while(!qOverlap.isEmpty()){
    		overlapPeople.add(qOverlap.poll());
    	}
    }
    
    private void sendOverlaps() {
    	sendLeftOverlap();
    	sendRightOverlap();
    	sendBottomOverlap();
    	sendTopOverlap();
    }

    public void run() {
        int astars = 0;
        for (int i = 0; i < this.steps; i++) {
        	
        	// System.out.println("My queue has: " + q.size() + " And I have: " + people.size());
            overlapPeople = new ArrayList<Person>();
        	addPeople();
            // System.out.println("U have " + overlapPeople.size() + " overlap to send");
        	sendOverlaps();
        	
        	try {
        		barrier.await();
        	} catch (InterruptedException e) {
				System.err.println("Overlap fuckage");
        	} catch (BrokenBarrierException e) {
        		System.err.println("Overlap fuckage");
			}
        	
        	addOverlapPeople();
        	
        	// System.out.println("After My queue has: " + q.size() + " And I have: " + people.size());
        	
        	allPeople = new ArrayList<Person>();
        	allPeople.addAll(people);
        	allPeople.addAll(overlapPeople);
        	populateDensityMap();
        	int blockages = 0;

        	ArrayList<Person> toRemove = new ArrayList<Person>();
        	for (Person p : people) {
//        		System.out.println("There have been" + blockages + " blockages");
                try {
                	if(p.getLocation() == null){
                		continue;
                	}
                    p.advance(gWalls, allPeople, 0.1);
                	if (visibleBlockage(p) != null && p.getLocation().distance(p.getNextGoal()) > 3) {
                		//Red on canvas
                		blockages++;
                		p.blockedList.set(p.blockedList.size() - 1, true);

                        //Dont a star so often brah
                        if(p.lastAStar + 5 < i) {
                            for (Node n : p.getGoalList()) {
                                System.out.println(p.toString() + " goal before: " + n.x + ", " + n.y);
                            }
                            aStar(p);
                            astars++;
                            p.lastAStar = i;
                            for (Node n : p.getGoalList()) {
                                System.out.println(p.toString() + " goal after: " + n.x + ", " + n.y);
                            }
                        }


                	}


                	
                    if(p.getLocation() != null && !isPointInside(p.getLocation().x, p.getLocation().y) && p.getLocation().x > 0 && p.getLocation().y > 0){
                        int xIndex = (int) p.getLocation().x / 50;
                    	int yIndex = (int) p.getLocation().y / 50;
                    	if(!(xIndex < 0 || yIndex < 0)){
                    		toRemove.add(p);
                    		chunks[xIndex][yIndex].putPerson(p);
                    	}
                    	else {
                    		System.out.println("Left Canvas");
                    	}
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    
                } 
            }
            System.out.println("A stars for this chunk:" + astars);
            if (i != this.steps - 1) {
        	people.removeAll(toRemove);
            }
            try {
				barrier.await();
			}
            catch (InterruptedException e) {
				System.err.println("Barrier fuckage");
			}
			catch (BrokenBarrierException e) {
				System.err.println("Barrier fuckage");
			}

            // System.out.println(people.size());
        }
    }
    
    private void aStar(Person p) throws Exception {
    	int x = (int) p.getLocation().x;
    	int y = (int) p.getLocation().y;

    	int startNode = x * sideLength + y;
    	int goalNode = p.getGoalList().getLast().x * sideLength + p.getGoalList().getLast().y;
    	p.astarCheck = true;

    	System.out.println("I am calling with start node: " + startNode + " and goal node: " + goalNode);

        if (aStar.connections.get(startNode) == null) {
            System.out.println("Tried to do AStar from " + x + ", " + y + " but couldn't find any connections");
            // System.exit(1);
        }

        Path path = aStar.getPath(startNode, goalNode, densityMap);
       	p.setGoalList(path.getSubGoals());

       	if (p.getGoalList().getLast().x != 0) {
       		System.out.println("fuckyeah");
       	}
    }
    
    private void populateDensityMap() {	 	
    	int sideLength = w.getSideLength();
	    densityMap = new int[sideLength][sideLength];
    	for(Person p : people) {
    		if (p.getLocation() != null) {
	    		Point2d l = new Point2d((int) Math.round(p.getLocation().x), (int) Math.round(p.getLocation().y));
	    		if (l.x < 0) {
	    			l.x = 0;
	    		}
	    		if (l.x >= sideLength) {
	    			l.x = sideLength - 1;
	    		}
	    		if (l.y >= sideLength) {
	    			l.y = sideLength - 1;
	    		}
	    		if (l.y < 0) {
	    			l.y = 0;
	    		}
	    		
	    		densityMap[(int) l.x][(int) l.y]++;
	    		if (l.x > 0 && l.y > 0) {
	    			densityMap[(int) l.x - 1][(int) l.y - 1]++;
	    		}
	    		if (l.y > 0) {
	    			densityMap[(int) l.x][(int) l.y - 1]++;
	    		}
	    		if (l.y > 0 && l.x < sideLength - 1) {
	    			densityMap[(int) l.x + 1][(int) l.y - 1]++;
	    		}
	    		if (l.x > 0) {
	    			densityMap[(int) l.x - 1][(int) l.y]++;
	    		}
	    		if (l.x < sideLength - 1) {
	    			densityMap[(int) l.x + 1][(int) l.y]++;
	    		}
	    		if (l.x > 0 && l.y < sideLength - 1) {
	    			densityMap[(int) l.x - 1][(int) l.y + 1]++;
	    		}
	    		if (l.y < sideLength - 1) {
	    			densityMap[(int) l.x][(int) l.y + 1]++;
	    		}
	    		if (l.y < sideLength - 1 && l.x < sideLength - 1) {
	    			densityMap[(int) l.x + 1][(int) l.y + 1]++;
	    		}
    		}
    	}
//        for (int i = 0; i < densityMap.length; i++) {
//            for ( int j = 0; j < densityMap.length; j++) {
////                densityMap[i][j] = (int) Math.pow(densityMap[i][j], 2);
//                  densityMap[i][j] = densit
//            }
//        }
    }
   
    
    private void populateFloorPlan() {
    	int sideLength = w.getSideLength();
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                floorPlan[i][j] = 0;
                for (Wall wall : gWalls) {
                    Point2d point2d = new Point2d(i, j);
                    if (wall.touches(point2d, 1.0)) {
                    	floorPlan[i][j] = 1;
                        break;
                    }
                }
                if(floorPlan[i][j] == 0) {
                	nodes[i][j] = new Vertex(i, j);
                }
            }
        }
    }
    
    private void createEdges() {
    	int sideLength = w.getSideLength();
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                if (floorPlan[i][j] == 0) {
                    // if not at right-most node
                    if (j < (sideLength - 1)) {
                        // check right
                        if (floorPlan[i][j + 1] == 0) {
                            edges.add(new Edge(nodes[i][j], nodes[i][j + 1], 1.0));
                        }
                        // check bottom right
                        if (i < (sideLength - 1) && floorPlan[i + 1][j + 1] == 0) {
                            edges.add(new Edge(nodes[i][j], nodes[i + 1][j + 1], Math.sqrt(2)));
                        }
                    }
                    // if not at bottom node
                    if (i < sideLength - 1) {
                        // check bottom
                        if (floorPlan[i + 1][j] == 0) {
                            edges.add(new Edge(nodes[i][j], nodes[i + 1][j], 1.0));
                        }
                        // check bottom left
                        if (j > 0 && floorPlan[i + 1][j - 1] == 0) {
                            edges.add(new Edge(nodes[i][j], nodes[i + 1][j - 1], Math.sqrt(2)));
                        }
                    }
                }
            }
        }
    }
    
    public void printFloorPlan() {

    	int sideLength = w.getSideLength();
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
            		System.out.print(floorPlan[j][i]);   
            }
            System.out.println();
        }
    }
    
    public void printDensity() {

    	int sideLength = w.getSideLength();
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
            		System.out.print(densityMap[j][i]);   
            }
            System.out.println();
        }
    }
    
    public Point2d visibleBlockage(Person p){
    	
    	if(p.getLocation() == null){
    		return null;
    	}
    	Point2D l = new Point2D.Double(p.getLocation().x, p.getLocation().y);
    	Point2D nextGoal = new Point2D.Double(p.getNextGoal().x, p.getNextGoal().y);
    	
    	int length = (int) l.distance(nextGoal);
    	for (int i = 1; i < length; i++) {
    		int y = (int) Math.round(((nextGoal.getY() - l.getY()) - (nextGoal.getX() - l.getX()) / length * i) + l.getY());
    		int x = (int) Math.round(l.getX() + 1);
    		if(x < 0 || y < 0 || x >= sideLength || y >= sideLength){
    			return null;
    		}
    		if (densityMap[x][y] > 9) {
    			return new Point2d(x, y);
    		}
    	}
    	
    	return null;
    }
}
