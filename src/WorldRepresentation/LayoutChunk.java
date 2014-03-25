package WorldRepresentation;

import Dijkstra.Edge;
import Dijkstra.Vertex;
import Exceptions.RoutesNotComputedException;
import NewDijkstra.AStar;
import NewDijkstra.aConnection;

import javax.vecmath.Point2d;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class LayoutChunk implements Runnable {

    private ArrayList<ArrayList<Wall>> lWalls;
    private ArrayList<ArrayList<Wall>> gWalls;
    private double topYBoundary;
    private double bottomYBoundary;
    private double rightXBoundary;
    private double leftXBoundary;
    private ArrayList<Person> people;
    private ArrayList<Person> overlapPeople;
    boolean finished;
    private CyclicBarrier barrier;
    private int steps;
    private int[][][] floorPlan;
    private int[][][] densityMap;
    private ArrayList<int[][][]> allDensityMaps;
    private World w;
    private ArrayList<Edge> edges;
    private Vertex[][][] nodes;
    LayoutChunk[] chunks;
    public ArrayBlockingQueue<Person> qOverlap;
    public ArrayList<Person> q;
    private ArrayList<Person> allPeople;
    private AStar chunkStar;
    private Integer evacTime;
    private Integer ASTAR;
    private Integer ASTAR_FREQUENCY;
    public int i;
    public int numFloors;

    public LayoutChunk(double leftXBoundary, double rightXBoundary, double topYBoundary, double bottomYBoundary,
                       CyclicBarrier barrier, int steps, World w, Integer evacTime,
                       Integer astarToggle, Integer astarFreq, int numFloors) {
        this.numFloors = numFloors;
        this.ASTAR = astarToggle;
        this.ASTAR_FREQUENCY = astarFreq;
        this.evacTime = evacTime;
        people = new ArrayList<Person>();
        overlapPeople = new ArrayList<Person>();
        this.topYBoundary = topYBoundary;
        this.bottomYBoundary = bottomYBoundary;
        this.leftXBoundary = leftXBoundary;
        this.rightXBoundary = rightXBoundary;
        nodes = new Vertex[w.getSideLength()][w.getSideLength()][numFloors];
        edges = new ArrayList<Edge>();
        lWalls = new ArrayList<ArrayList<Wall>>();
        for (int z = 0; z < numFloors; z++) {
            lWalls.add(new ArrayList<Wall>());
        }
        floorPlan = new int[w.getSideLength()][w.getSideLength()][numFloors];
        gWalls = w.getWalls();
        densityMap = new int[w.getSideLength()][w.getSideLength()][numFloors];
        allDensityMaps = new ArrayList<int[][][]>();
        finished = false;
        this.w = w;
        this.barrier = barrier;
        this.steps = steps;
        qOverlap = new ArrayBlockingQueue<Person>(w.getPeople().size());
        q = new ArrayList<Person>();

        populateFloorPlan();
        createEdges();
        chunkStar = new AStar(w.getSideLength() * w.getSideLength() * numFloors, edges, w.getSideLength());
    }

    public void addWall(double x1, double y1, double x2, double y2, int floor) {
        lWalls.get(floor).add(new Wall(x1, y1, x2, y2));
    }

    public void addPerson(Person p) {
        people.add(p);
    }

    public ArrayList<Person> getPeople() {
        return people;
    }

    public ArrayList<int[][][]> getAllDensityMaps() {
        return allDensityMaps;
    }

    public boolean isPointInside(double x, double y) {
        return (y >= topYBoundary && y <= bottomYBoundary && x <= rightXBoundary && x >= leftXBoundary);
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

    public void addChunks(LayoutChunk[] chunks) {
        this.chunks = chunks;
    }

    public void putPerson(Person p) {
        this.q.add(p);
    }

    private void addPeople() {
        this.people.addAll(q);
        q.clear();
    }

    private ArrayList<Person> peopleTopEdge() {
        ArrayList<Person> ret = new ArrayList<Person>();
        for (Person p : people) {
            if (p.getLocation() != null && topYBoundary - p.getLocation().y < 2) {
                ret.add(p);
            }
        }
        return ret;
    }

    private ArrayList<Person> peopleBottomEdge() {
        ArrayList<Person> ret = new ArrayList<Person>();
        for (Person p : people) {
            if (p.getLocation() != null && p.getLocation().y - bottomYBoundary < 2) {
                ret.add(p);
            }
        }
        return ret;
    }

    private int chunkSize() {
        return (int) chunks[0].bottomYBoundary;
    }

    private void sendTopOverlap() {
        ArrayList<Person> t = peopleTopEdge();

        int yIndex = (int) topYBoundary / chunkSize();

        if (yIndex == 0) {
            return;
        }
        chunks[yIndex - 1].qOverlap.addAll(t);
    }

    private void sendBottomOverlap() {
        ArrayList<Person> b = peopleBottomEdge();

        int yIndex = (int) topYBoundary / chunkSize();

        if (yIndex == chunks.length - 1) {
            return;
        }
        chunks[yIndex + 1].qOverlap.addAll(b);
    }

    private void addOverlapPeople() {
        overlapPeople.addAll(qOverlap);
        qOverlap.clear();
    }

    private void sendOverlaps() {
        sendBottomOverlap();
        sendTopOverlap();
    }

    private void addDensityMap(int[][][] densityMap) {
        allDensityMaps.add(densityMap);
    }

    private ArrayList<Person> getAllPeople() {
        allPeople = new ArrayList<Person>();
        allPeople.addAll(people);
        allPeople.addAll(overlapPeople);
        return allPeople;
    }

    private int[] validXYLocation(Person p) {
        int x = (int) Math.round(p.getLocation().x);
        int y = (int) Math.round(p.getLocation().y);

        if (x < 0)
            x = 0;
        if (y < 0)
            y = 0;

        ArrayList<aConnection> aconn = chunkStar.getConnections().get(p.floor * (w.sideLength * w.sideLength) + x * w.getSideLength() + y);
        int rand1 = x;
        int rand2 = y;

        while (aconn == null) {
            rand1 = (int) Math.round((Math.random() * 2) - 1) + x;
            rand2 = (int) Math.round((Math.random() * 2) - 1) + y;

            aconn = chunkStar.getConnections().get(p.floor * (w.sideLength * w.sideLength) + rand1 * w.getSideLength() + rand2);
        }

        return new int[]{rand1, rand2};
    }

    private void updatePersonWithEvacPath(Person p) throws RoutesNotComputedException {
        int[] xy = validXYLocation(p);
        int x = xy[0];
        int y = xy[1];

        Path thisPath = w.getPath(x, y, p.floor, 0, true);
        int pathLength = thisPath.getNodes().size();

        if (w.fdEvacList.size() > 1) {
            for (int q = 1; q < w.fdEvacList.size(); q++) {
                Path newPath = w.getPath(x, y, p.floor, q, true);
                if (newPath.getNodes().size() < pathLength) {
                    thisPath = newPath;
                    pathLength = newPath.getNodes().size();
                }
            }
        }

        p.setGoalList(thisPath.getSubGoals());
        p.evacBool = true;
    }

    private boolean isStuck(Person p, Integer i) {
        if (ASTAR != 1)
            return false;
        if (Math.abs(evacTime - i) < 3)
            return false;
        // Stuck due to seeing blockage
        if (visibleBlockage(p) != null && p.getLocation().distance(p.getNextGoal()) > 3)
            return true;
        // Stuck due to taking too long to reach goal
        if (p.expectedTimeStepAtNextGoal + 1 < p.locations.size())
            return true;
        return stuckOnWall(p, i);
    }

    private void waitBarrier() {
        try {
            barrier.await();
        } catch (InterruptedException e) {
            System.err.println("Barrier interrupted");
            System.exit(1);
        } catch (BrokenBarrierException e) {
            System.err.println("Broken barrier");
            System.exit(1);
        }
    }

    private int threadID() {
        return (int) bottomYBoundary / chunkSize();
    }

    private void putPersonInCorrespondingChunksList(Person p, ArrayList<Person> toRemove) {
        if (p.getLocation() != null && !isPointInside(p.getLocation().x, p.getLocation().y) && p.getLocation().x > 0 && p.getLocation().y > 0) {
            int xIndex = (int) p.getLocation().x / chunkSize();
            int yIndex = (int) p.getLocation().y / chunkSize();
            if (xIndex > 1) {
                xIndex = 1;
            }
            if (yIndex > 1) {
                yIndex = 1;
            }
            if (!(xIndex < 0 || yIndex < 0)) {
                toRemove.add(p);
                chunks[yIndex].putPerson(p);
            } else {
                System.out.println("Left Canvas");
            }
        }
    }

    public void run() {
        for (i = 0; i < this.steps; i++) {
            System.out.println(i);

            addPeople();
            sendOverlaps();

            waitBarrier();

            finished = true;

            addOverlapPeople();

            ArrayList<Person> allPeople = getAllPeople();

            populateDensityMap();

            addDensityMap(densityMap);

            ArrayList<Person> toRemove = new ArrayList<Person>();
            for (Person p : people) {
                try {
                    if (i == evacTime) {
                        updatePersonWithEvacPath(p);
                    }

                    if (p.getLocation() == null) {
                        continue;
                    }

                    if (stuckOnWall(p, i)) {
                        System.out.println("I am stuck on wall at: " + p.location.x + "," + p.location.y);
                    }

                    if (isStuck(p, i)) {

                        p.blockedList.set(p.blockedList.size() - 1, true);

                        if (p.lastAStar + ASTAR_FREQUENCY < i) {
                            aStar(p);
                            p.lastAStar = i;
                        }
                    }

                    putPersonInCorrespondingChunksList(p, toRemove);

                    p.advance(gWalls, allPeople, 0.1, w);
                    finished = false;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (i != this.steps - 1) {
                people.removeAll(toRemove);
                System.out.println("People size: " + people.size());
            }
            long start = System.currentTimeMillis();
            System.out.println("I am thread " + threadID() + " and I am waiting at the bottom");
            waitBarrier();
            long end = System.currentTimeMillis();
            System.out.println("I am thread " + threadID() + " and I waited at the bottom for " + (end - start) + "ms");

            // System.out.println(people.size());
        }
    }

    private void aStar(Person p) throws Exception {
        int sideLength = w.getSideLength();

        int x = (int) Math.round(p.getLocation().x);
        int y = (int) Math.round(p.getLocation().y);

        //Incase off map

        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }

        ArrayList<aConnection> aconn = chunkStar.getConnections().get(x * sideLength + y);
        int rand1 = x;
        int rand2 = y;

        // Akon fixes people disappearing.
        while (aconn == null) {
            rand1 = (int) Math.round((Math.random() * 2) - 1) + x;
            rand2 = (int) Math.round((Math.random() * 2) - 1) + y;

            aconn = chunkStar.getConnections().get(rand1 * sideLength + rand2);
        }

        x = rand1;
        y = rand2;
        int startNode = (p.floor * sideLength * sideLength) + x * sideLength + y;
        int goalNode = p.getGoalList().getLast().getX() * sideLength + p.getGoalList().getLast().getY();
        int goalZ = goalNode / (sideLength * sideLength);
        int goalX = (goalNode % (sideLength * sideLength)) / sideLength;
        int goalY = goalNode % sideLength;
        p.astarCheck = true;

        if (chunkStar.getConnections().get(startNode) == null) {
            System.out.println("Tried to do AStar from " + x + ", " + y + " but couldn't find any connections");
        }
        Path path = chunkStar.getPath(startNode, goalX, goalY, goalZ, densityMap, w.floorConnections);

        p.setGoalList(path.getSubGoals());
        p.distanceToNextGoal = p.location.distance(p.getGoalList().get(p.getGoalIndex()).toPoint2d());

        p.expectedTimeStepAtNextGoal = (p.distanceToNextGoal / (p.getDesiredSpeed() * 0.1)) + 5 + (p.locations.size());

    }

    private void populateDensityMap() {
        int sideLength = w.getSideLength();
        densityMap = new int[sideLength][sideLength][numFloors];

        for (Person p : people) {
            if (p.getLocation() != null) {
                Point2d l = new Point2d(Math.round(p.getLocation().x), Math.round(p.getLocation().y));

                if (l.x < 0)
                    l.x = 0;
                if (l.x >= sideLength)
                    l.x = sideLength - 1;

                if (l.y >= sideLength)
                    l.y = sideLength - 1;
                if (l.y < 0)
                    l.y = 0;

                densityMap[(int) l.x][(int) l.y][p.floor]++;
                if (l.x > 0) {
                    densityMap[(int) l.x - 1][(int) l.y][p.floor]++;
                    if (l.y > 0)
                        densityMap[(int) l.x - 1][(int) l.y - 1][p.floor]++;
                    if (l.y < sideLength - 1)
                        densityMap[(int) l.x - 1][(int) l.y + 1][p.floor]++;
                }
                if (l.x < sideLength - 1) {
                    densityMap[(int) l.x + 1][(int) l.y][p.floor]++;
                    if (l.y > 0)
                        densityMap[(int) l.x + 1][(int) l.y - 1][p.floor]++;
                    if (l.y < sideLength - 1)
                        densityMap[(int) l.x + 1][(int) l.y + 1][p.floor]++;
                }
                if (l.y > 0)
                    densityMap[(int) l.x][(int) l.y - 1][p.floor]++;
                if (l.y < sideLength - 1)
                    densityMap[(int) l.x][(int) l.y + 1][p.floor]++;
            }
        }
    }


    private void populateFloorPlan() {
        int sideLength = w.getSideLength();
        for (int z = 0; z < numFloors; z++) {
            for (int i = 0; i < sideLength; i++) {
                for (int j = 0; j < sideLength; j++) {
                    floorPlan[i][j][z] = 0;
                    for (Wall wall : gWalls.get(z)) {
                        Point2d point2d = new Point2d(i, j);
                        if (wall.touches(point2d, 1.0)) {
                            floorPlan[i][j][z] = 1;
                            break;
                        }
                    }
                    if (floorPlan[i][j][z] == 0) {
                        nodes[i][j][z] = new Vertex(i, j, z);
                    }
                }
            }
        }
    }

    private void createEdges() {
        int sideLength = w.getSideLength();
        for (int z = 0; z < numFloors; z++) {
            for (int i = 0; i < sideLength; i++) {
                for (int j = 0; j < sideLength; j++) {
                    if (floorPlan[i][j][z] == 0) {
                        // if not at right-most node
                        if (j < (sideLength - 1)) {
                            // check right
                            if (floorPlan[i][j + 1][z] == 0) {
                                edges.add(new Edge(nodes[i][j][z], nodes[i][j + 1][z], 1.0, z));
                            }
                            // check bottom right
                            if (i < (sideLength - 1) && floorPlan[i + 1][j + 1][z] == 0) {
                                edges.add(new Edge(nodes[i][j][z], nodes[i + 1][j + 1][z], Math.sqrt(2), z));
                            }
                        }
                        // if not at bottom node
                        if (i < sideLength - 1) {
                            // check bottom
                            if (floorPlan[i + 1][j][z] == 0) {
                                edges.add(new Edge(nodes[i][j][z], nodes[i + 1][j][z], 1.0, z));
                            }
                            // check bottom left
                            if (j > 0 && floorPlan[i + 1][j - 1][z] == 0) {
                                edges.add(new Edge(nodes[i][j][z], nodes[i + 1][j - 1][z], Math.sqrt(2), z));
                            }
                        }
                    }
                }
            }
        }
        for (FloorConnection fc : w.floorConnections) {
            edges.add(new Edge(nodes[(int) fc.location.x][(int) fc.location.y][fc.fromFloor],
                    nodes[(int) fc.location.x][(int) fc.location.y][fc.fromFloor + 1], 2, fc.fromFloor));
        }
    }

    public Point2d visibleBlockage(Person p) {

        if (p.getLocation() == null) {
            return null;
        }
        Point2D l = new Point2D.Double(p.getLocation().x, p.getLocation().y);
        Point2D nextGoal = new Point2D.Double(p.getNextGoal().x, p.getNextGoal().y);

        int length = (int) l.distance(nextGoal);
        for (int i = 1; i < length; i++) {
            int y = (int) Math.round(((nextGoal.getY() - l.getY()) - (nextGoal.getX() - l.getX()) / length * i) + l.getY());
            int x = (int) Math.round(l.getX() + 1);
            if (x < 0 || y < 0 || x >= w.getSideLength() || y >= w.getSideLength()) {
                return null;
            }
            if (densityMap[x][y][p.floor] > 9) {
                return new Point2d(x, y);
            }
        }

        return null;
    }

    boolean stuckOnWall(Person p, int time) {

        boolean allwalls = false;
        for (Wall w : lWalls.get(p.floor)) {
            if (w.distance(p) < 0.7) {
                p.stuckOnWallSince++;
                allwalls = true;
                break;
            }
        }
        if (!allwalls) {
            p.stuckOnWallSince = 0;
        }
        return p.stuckOnWallSince > time;
    }
}
