package WorldRepresentation;

import javax.vecmath.Point2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
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
    boolean finished;
    private CyclicBarrier barrier;
    private int steps;
    LayoutChunk[][] chunks;
    public LinkedBlockingQueue<Person> q;
//    private HashMap<Point2d, Queue<Person>> queues;
//    private Queue<Person> newPeople;
    
    public LayoutChunk(double leftXBoundary, double rightXBoundary, double topYBoundary, double bottomYBoundary, ArrayList<Wall> walls, CyclicBarrier barrier, int steps) {
        people = new ArrayList<Person>();
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
        return (w.intersects(new Point2d(leftXBoundary, topYBoundary), new Point2d(rightXBoundary, topYBoundary), 1));
    }

    public boolean intersectsBottom(Wall w) {
        return (w.intersects(new Point2d(leftXBoundary, bottomYBoundary), new Point2d(rightXBoundary, bottomYBoundary), 1));
    }

    public boolean intersectsLeft(Wall w) {
        return (w.intersects(new Point2d(leftXBoundary, bottomYBoundary), new Point2d(leftXBoundary, topYBoundary), 1));
    }

    public boolean intersectsRight(Wall w) {
        return (w.intersects(new Point2d(rightXBoundary, bottomYBoundary), new Point2d(rightXBoundary, topYBoundary), 1));
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
    

    public void run() {
        for (int i = 0; i < this.steps; i++) {
        	
        	System.out.println("My queue has: " + q.size() + " And I have: " + people.size());
        	addPeople();
        	ArrayList<Person> toRemove = new ArrayList<Person>();
        	for (Person p : people) {
                try {
                    p.advance(gWalls, people, 0.25);
                    if(!isPointInside(p.getLocation().x, p.getLocation().y)){
                    	int xIndex = (int) p.getLocation().x / 50;
                    	int yIndex = (int) p.getLocation().y / 50;
                    	chunks[xIndex][yIndex].putPerson(p);
                    	toRemove.add(p);
                    }
                }
                catch (Exception e) {
                    //
                } 
            }
        	people.removeAll(toRemove);
            try {
				barrier.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				System.err.println("Barrier fuckage");
			}
        }
    }
}
