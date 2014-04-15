package WorldRepresentation;

import Dijkstra.Edge;
import Dijkstra.Vertex;
import Exceptions.RoutesNotComputedException;
import NewDijkstra.AStar;
import NewDijkstra.aConnection;

import javax.vecmath.Point2d;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

// Represents the abstraction of the main World into threadable instances
public class LayoutChunk implements Runnable {

    // Stores the walls across the entire building
    private ArrayList<ArrayList<Wall>> gWalls;
    // Stores a local copy of the walls to avoid concurrency modification errors
    private ArrayList<ArrayList<Wall>> lWalls;
    // Stores the information representing the area for which each thread is responsible
    private double topYBoundary;
    private double bottomYBoundary;
    private double rightXBoundary;
    private double leftXBoundary;
    // Stores the people that this chunk is currently simulating
    private ArrayList<Person> people;
    // Stores the people just over the boundary of this chunk
    private ArrayList<Person> overlapPeople;
    // Boolean which represents if chunk is currently performing simulation
    boolean finished;
    // Reference to the synchronisation barrier
    private CyclicBarrier barrier;
    // Number of timesteps that this simulation will perform
    private int steps;
    // Node array which represents the graph of the building in binary, mainly for printing
    private int[][][] floorPlan;
    // Stores the density at each point on the graph
    private int[][][] densityMap;
    // Stores a reference to all the density information for previous time intervals
    private ArrayList<int[][][]> allDensityMaps;
    // Stores a reference to the original world to callback for paths
    private World w;
    // Stores all the unidirectional edges on the graph
    private ArrayList<Edge> edges;
    // Stores the nodes which represent the graph of the building
    private Vertex[][][] nodes;
    // References to all the other chunks in the simulation
    LayoutChunk[] chunks;
    // Queue which represents the people other chunks have determined are just over this chunks boundary
    public LinkedList<Person> qOverlap;
    // Queue which is a temporary storage for people who will be moved into this chunk
    public ArrayList<Person> q;
    // AStar object used for recomputing paths
    private AStar chunkStar;
    // Time at which the simulation will switch to an evacuation
    private Integer evacTime;
    // Represents the status of A* in this simulation
    private Integer ASTAR;
    // Represents how many timesteps there should be between each A* computation
    private Integer ASTAR_FREQUENCY;
    public int i;
    public int numFloors;

    // Constructor which for the most part takes aspects of the original world and stores the useful information
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
        qOverlap = new LinkedList<Person>();
        q = new ArrayList<Person>();
        populateFloorPlan();
        createEdges();
        chunkStar = new AStar(w.getSideLength() * w.getSideLength() * numFloors, edges, w.getSideLength());
    }

    // Adds a wall to this chunk
    public void addWall(double x1, double y1, double x2, double y2, int floor) {
        lWalls.get(floor).add(new Wall(x1, y1, x2, y2));
    }

    // Adds a person to this chunk
    public void addPerson(Person p) {
        people.add(p);
    }

    public ArrayList<Person> getPeople() {
        return people;
    }

    public ArrayList<int[][][]> getAllDensityMaps() {
        return allDensityMaps;
    }

    // Returns true if the point given is within the boundaries of this chunk
    public boolean isPointInside(double x, double y) {
        return (y >= topYBoundary && y <= bottomYBoundary &&
                x <= rightXBoundary && x >= leftXBoundary);
    }

    // Returns true if the given wall passes through the top of this chunk
    public boolean intersectsTop(Wall w) {
        return (w.intersects(new Point2d(leftXBoundary, topYBoundary),
                new Point2d(rightXBoundary, topYBoundary)));
    }

    // Returns true if the given wall passes through the bottom of the chunk
    public boolean intersectsBottom(Wall w) {
        return (w.intersects(new Point2d(leftXBoundary, bottomYBoundary),
                new Point2d(rightXBoundary, bottomYBoundary)));
    }

    // Counts the number of times this wall intersects the boundaries of this chunk
    public int numberOfIntersects(Wall w) {
        int num = 0;
        if (intersectsBottom(w)) {
            num++;
        }
        if (intersectsTop(w)) {
            num++;
        }
        return num;
    }

    // Function to store a reference to the other chunks
    public void addChunks(LayoutChunk[] chunks) {
        this.chunks = chunks;
    }

    // Function is called by other chunks when a person has passed into this chunk
    public void putPerson(Person p) {
        this.q.add(p);
    }

    // Shifts all the people other chunks have determined are in this chunk into the set of people for this chunk
    public void addPeople() {
        this.people.addAll(q);
        q.clear();
    }

    // Returns all the people who are close to the top edge of the chunk
    public ArrayList<Person> peopleTopEdge() {
        ArrayList<Person> ret = new ArrayList<Person>();
        for (Person p : people) {
            if (p.getLocation() != null && Math.abs(topYBoundary - p.getLocation().y) < 2) {
                ret.add(p);
            }
        }
        return ret;
    }

    // Returns all the people who are close to the bottom edge of the chunk
    public ArrayList<Person> peopleBottomEdge() {
        ArrayList<Person> ret = new ArrayList<Person>();
        for (Person p : people) {
            if (p.getLocation() != null && Math.abs(p.getLocation().y - bottomYBoundary) < 2) {
                ret.add(p);
            }
        }
        return ret;
    }

    // Returns the height of the chunk in this simulation
    public int chunkSize() {
        return (int) chunks[0].bottomYBoundary;
    }

    // Sends all the people who are determined to be close to the top boundary to the overlap of the
    // neighbouring chunk
    public void sendTopOverlap() {
        ArrayList<Person> t = peopleTopEdge();

        int yIndex = (int) topYBoundary / chunkSize();

        if (yIndex == 0) {
            return;
        }
        chunks[yIndex - 1].qOverlap.addAll(t);
    }

    // Sends all the people who are determined to be close to the bottom boundary to the overlap of the
    // neighbouring chunk
    public void sendBottomOverlap() {
        ArrayList<Person> b = peopleBottomEdge();

        int yIndex = (int) topYBoundary / chunkSize();

        if (yIndex == chunks.length - 1) {
            return;
        }
        chunks[yIndex + 1].qOverlap.addAll(b);
    }

    // Adds the people that other chunks have identified near a boundary into the actual boundary list
    public void addOverlapPeople() {
        overlapPeople.clear();
        overlapPeople.addAll(qOverlap);
        qOverlap.clear();
    }

    // Communicate the overlap people to other chunks
    public void sendOverlaps() {
        sendBottomOverlap();
        sendTopOverlap();
    }

    // Adds the current density map to the list of density maps over time
    public void addDensityMap(int[][][] densityMap) {
        allDensityMaps.add(densityMap);
    }

    // Returns all the people that this chunk is aware of
    public ArrayList<Person> getAllPeople() {
        ArrayList<Person> allPeople = new ArrayList<Person>();
        allPeople.addAll(people);
        allPeople.addAll(overlapPeople);
        return allPeople;
    }

    // Returns an integer x, y coordinate that is as close as possible to the person given but is not inside a wall
    public int[] validXYLocation(Person p) {
        int x = (int) Math.round(p.getLocation().x);
        int y = (int) Math.round(p.getLocation().y);

        if (x < 0)
            x = 0;
        if (y < 0)
            y = 0;

        ArrayList<aConnection> aConns = chunkStar.getConnections().get(
                p.floor * (w.sideLength * w.sideLength) + x * w.getSideLength() + y);
        int rand1 = x;
        int rand2 = y;

        int start = 2;
        int count = 1;
        // Loops through the nodes around the specified point in a spiral until a valid node is found.
        while (aConns == null) {
            if (count % 10 == 0) {
                count = 1;
                start++;
            }
            rand1 = (int) Math.round((Math.random() * start) - 1) + x;
            rand2 = (int) Math.round((Math.random() * start) - 1) + y;

            aConns = chunkStar.getConnections().get(
                    p.floor * (w.sideLength * w.sideLength) + rand1 * w.getSideLength() + rand2);
            count++;
        }

        return new int[]{rand1, rand2};
    }

    // Assign the given person a path towards the closest evacuation point
    public void updatePersonWithEvacPath(Person p) throws RoutesNotComputedException {
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
        p.distanceToNextGoal = p.location.distance(p.getGoalList().get(p.getGoalIndex()).toPoint2d());
        p.expectedTimeStepAtNextGoal = (p.distanceToNextGoal / (p.getDesiredSpeed() * 0.1)) + 5 + (p.locations.size());
    }

    // Return true if a person is stuck
    public boolean isStuck(Person p, Integer i) {
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

    // Wait within this method until all other chunks have reached it
    public void waitBarrier() {
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

    // Returns the unique ID associated with this chunk
    public int threadID() {
        return (int) bottomYBoundary / chunkSize();
    }

    // Communicate people to the chunks that should be responsible for their simulation
    public void putPersonInCorrespondingChunksList(Person p, ArrayList<Person> toRemove) {
        if (p.getLocation() != null && !isPointInside(p.getLocation().x, p.getLocation().y) &&
                p.getLocation().x > 0 && p.getLocation().y > 0) {
            int xIndex = (int) p.getLocation().x / chunkSize();
            int yIndex = (int) p.getLocation().y / chunkSize();
            if (xIndex > 1) {
                xIndex = 1;
            }
            if (!(xIndex < 0 || yIndex < 0)) {
                toRemove.add(p);
                chunks[yIndex].putPerson(p);
            } else {
                System.out.println("Left Canvas");
            }
        }
    }

    // This is the main computation of the simulation
    public void run() {
        for (i = 0; i < this.steps; i++) {
            addPeople();
            sendOverlaps();
            // Wait here until all the overlap queues have been filled
            waitBarrier();
            // Mark that a timestep simulation has started
            finished = true;
            // Add the people identified near a boundary to the boundary list
            addOverlapPeople();
            ArrayList<Person> allPeople = getAllPeople();
            // Store the density map at every 5 time intervals
            if (i % 5 == 0) {
                populateDensityMap();
                addDensityMap(densityMap);
            }
            // Initialise storage for references to people who should be removed from this chunk
            ArrayList<Person> toRemove = new ArrayList<Person>();
            for (Person p : people) {
                try {
                    // Don't advance people if they have evacuated
                    if (p.getLocation() == null) {
                        continue;
                    }
                    // Debug printing
                    if (stuckOnWall(p, i)) {
                        System.out.println("I am stuck on wall at: " + p.location.x + "," + p.location.y);
                    }
                    // If the simulation is switching to an evacuation at this point, assign a new path to this person
                    if (i == evacTime) {
                        updatePersonWithEvacPath(p);
                    } else if (isStuck(p, i)) {
                        // Stores whether or not the person is blocked at this point in time
                        p.blockedList.set(p.blockedList.size() - 1, true);
                        // Only perform A* if it has not been done recently
                        if (p.lastAStar + ASTAR_FREQUENCY < i) {
                            aStar(p);
                            p.lastAStar = i;
                        }
                    }

                    // If the person has moved over the boundary, put that person in the boundary list for that chunk
                    putPersonInCorrespondingChunksList(p, toRemove);
                    // Finally, advance the person using the force model
                    p.advance(gWalls, allPeople, 0.1, w);
                    // Flag that computation at this time interval has finished
                    finished = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Remove all the people who have moved out of this chunk from the list of people being simulated
            if (i != this.steps - 1) {
                people.removeAll(toRemove);
            }
            // Wait here until all the other chunks have finished for this timestep
            waitBarrier();
        }
    }

    // Perform A* towards their goal for the given person
    public void aStar(Person p) throws Exception {
        int[] xy = validXYLocation(p);
        int x = xy[0];
        int y = xy[1];
        int sideLength = w.getSideLength();
        int startNode = (p.floor * sideLength * sideLength) + x * sideLength + y;
        int goalNode = (p.getGoalList().getLast().getZ()) + p.getGoalList().getLast().getX() * sideLength + p.getGoalList().getLast().getY();
        int goalZ = goalNode / (sideLength * sideLength);
        int goalX = (goalNode % (sideLength * sideLength)) / sideLength;
        int goalY = goalNode % sideLength;
        // p.astarCheck = true;

        if (chunkStar.getConnections().get(startNode) == null) {
            System.out.println("Tried to do AStar from " + x + ", " + y + " but couldn't find any connections");
        }
        Path path = chunkStar.getPath(startNode, goalX, goalY, goalZ, densityMap, w.floorConnections);

        p.setGoalList(path.getSubGoals());
        p.distanceToNextGoal = p.location.distance(p.getGoalList().get(p.getGoalIndex()).toPoint2d());

        p.expectedTimeStepAtNextGoal = (p.distanceToNextGoal / (p.getDesiredSpeed() * 0.1)) + 5 + (p.locations.size());

    }

    // Record the density of the world at each point on the layout
    public void populateDensityMap() {
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

    // Fill the floorplan with flags that determine if there is a node at this point
    // Fill the node array with references to vertices for that coordinate
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

    // Create all the edges in the world
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
        // Add edges which represent staircases
        for (FloorConnection fc : w.floorConnections) {
            edges.add(new Edge(nodes[(int) fc.location.x][(int) fc.location.y][fc.fromFloor],
                    nodes[(int) fc.location.x][(int) fc.location.y][fc.fromFloor + 1], 2, fc.fromFloor));
        }
    }

    // Returns true if there is a blockage visible in front of the given person
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

    // Returns true if the person has been too close to a wall for a long time
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

    public AStar getChunkStar() {
        return chunkStar;
    }

    public void setAStar(int num) {
        ASTAR = num;
    }

    public void setEvacTime(int time) {
        evacTime = time;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
