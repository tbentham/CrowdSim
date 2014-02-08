package WorldRepresentation;

import javax.vecmath.Point2d;
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
    LayoutChunk[][] chunks;
    public LinkedBlockingQueue<Person> q;
    public LinkedBlockingQueue<Person> qOverlap;
//    private HashMap<Point2d, Queue<Person>> queues;
//    private Queue<Person> newPeople;
    
    public LayoutChunk(double leftXBoundary, double rightXBoundary, double topYBoundary, double bottomYBoundary, ArrayList<Wall> walls, CyclicBarrier barrier, int steps) {
        people = new ArrayList<Person>();
        overlapPeople = new ArrayList<Person>();
        this.topYBoundary = topYBoundary;
        this.bottomYBoundary = bottomYBoundary;
        this.leftXBoundary = leftXBoundary;
        this.rightXBoundary = rightXBoundary;
        lWalls = new ArrayList<Wall>();
        gWalls = walls;
        finished = false;
        this.barrier = barrier;
        this.steps = steps;
        q = new LinkedBlockingQueue<Person>();
        qOverlap = new LinkedBlockingQueue<Person>();
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
    		if (p.getLocation().x - leftXBoundary < 10) {
    			ret.add(p);
    		}
    	}
    	return ret;
    }
    
    private ArrayList<Person> peopleTopEdge() {
    	ArrayList<Person> ret = new ArrayList<Person>();
    	for (Person p : people) {
    		if (topYBoundary - p.getLocation().y < 10) {
    			ret.add(p);
    		}
    	}
    	return ret;
    }
    
    private ArrayList<Person> peopleRightEdge() {
    	ArrayList<Person> ret = new ArrayList<Person>();
    	for (Person p : people) {
    		if (rightXBoundary - p.getLocation().x < 10) {
    			ret.add(p);
    		}
    	}
    	return ret;
    }
    
    private ArrayList<Person> peopleBottomEdge() {
    	ArrayList<Person> ret = new ArrayList<Person>();
    	for (Person p : people) {
    		if (p.getLocation().y - bottomYBoundary < 10) {
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
        for (int i = 0; i < this.steps; i++) {
        	
        	// System.out.println("My queue has: " + q.size() + " And I have: " + people.size());
            overlapPeople = new ArrayList<Person>();
        	addPeople();
            // System.out.println("U have " + overlapPeople.size() + " overlap to send");
        	sendOverlaps();
        	
        	try {
        		barrier.await();
        	} catch (InterruptedException | BrokenBarrierException e) {
				System.err.println("Overlap fuckage");
			}
      
        	addOverlapPeople();
        	
        	// System.out.println("After My queue has: " + q.size() + " And I have: " + people.size());
        	
        	ArrayList<Person> allPeople = new ArrayList<Person>();
        	allPeople.addAll(people);
        	allPeople.addAll(overlapPeople);
        	
        	ArrayList<Person> toRemove = new ArrayList<Person>();
        	for (Person p : people) {
                try {
                    p.advance(gWalls, allPeople, 0.25);
                    if(!isPointInside(p.getLocation().x, p.getLocation().y) && p.getLocation().x > 0 && p.getLocation().y > 0){
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
                    //
                } 
            }
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
}
