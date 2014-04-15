package WorldRepresentation;

import Dijkstra.Edge;
import Dijkstra.Vertex;
import Exceptions.PersonOverlapException;
import Exceptions.RoutesNotComputedException;
import Exceptions.WallOverlapException;
import Exceptions.WorldNotSetUpException;
import NewDijkstra.FastDijkstra;
import NewDijkstra.Node;
import NewDijkstra.NodeRecord;
import org.jgrapht.util.FibonacciHeapNode;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.util.ArrayList;

// This class is intended to be a representation of the building layout which is feasible to perform simulation
// across and perform path finding on
public class World {

    // Represents the length of one side of the entire world
    public int sideLength;

    // Stores all the walls as a list
    private ArrayList<ArrayList<Wall>> walls;
    // Stores 0 and 1 values which represent whether or not there is a node on the graph at that corresponding
    // coordinate point
    private int[][][] floorPlan;

    // Stores the node objects which represent each integer point in the world
    private Vertex[][][] nodeArray;
    // Stores a reference to these vertices as a list
    private ArrayList<Vertex> nodes;
    // Stores the edges in the world in a list
    private ArrayList<Edge> edges;
    public Point3d evac;

    // Stores the collected density information through analysis of the calculated Dijkstra paths
    private int[][][] staticDensityMap;

    // Represents whether set up has been run on the world
    private boolean isSetUp;
    // Represents whether the paths have been computed on this world
    private boolean routesComputed;
    // Stores the Dijkstra objects which encode the calculated paths to each evacuation point and point of interest
    // in accessible lists
    public ArrayList<FastDijkstra> fdPOIList;
    public ArrayList<FastDijkstra> fdEvacList;
    // Stores all the people in the simulation in a list
    private ArrayList<Person> people;
    // Stores how many floors this simulation is concerned with
    public int numFloors;
    // Stores all the objects which can be viewed as staircases
    public ArrayList<FloorConnection> floorConnections;

    public World(int sideLength, int numFloors) {

        fdPOIList = new ArrayList<FastDijkstra>();
        fdEvacList = new ArrayList<FastDijkstra>();

        this.numFloors = numFloors;
        this.sideLength = sideLength;

        // Sets up a new list of walls for each floor in the simulation
        walls = new ArrayList<ArrayList<Wall>>();
        for (int i = 0; i < numFloors; i++) {
            walls.add(new ArrayList<Wall>());
        }
        floorPlan = new int[sideLength][sideLength][numFloors];

        nodeArray = new Vertex[sideLength][sideLength][numFloors];
        nodes = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();

        isSetUp = false;
        routesComputed = false;

        people = new ArrayList<Person>();

        floorConnections = new ArrayList<FloorConnection>();
    }

    public void addWall(double x1, double y1, double x2, double y2, int floor) {
        isSetUp = false;
        routesComputed = false;
        walls.get(floor).add(new Wall(x1, y1, x2, y2));
    }

    // Attempts to add a new person at the given location and assigns them an initial path
    // This will throw expections if the method is called at a location where a person already exists
    // or there is a wall
    public void addNewPersonAt(int x, int y, int floor, int goalID, boolean evac) throws RoutesNotComputedException,
            PersonOverlapException, WallOverlapException {
        if (!routesComputed) {
            throw new RoutesNotComputedException("Cannot add people until world has routes computed");
        }
        for (Person p : people) {
            if (p.getLocation().x == x && p.getLocation().y == y && p.floor == floor) {
                throw new PersonOverlapException("Cannot create second person at " + x + ", " + y);
            }
        }
        Person person = new Person(x, y, floor, goalID);
        person.evacBool = evac;
        for (Wall w : walls.get(person.floor)) {
            if (w.touches(person)) {
                throw new WallOverlapException("Cannot add person at " + x + " , " + y + " because wall exists there");
            }
        }
        // Once it has been determined that the person can be created, assign them with a path to their goal
        Path p1 = getPath(x, y, floor, goalID, evac);
        person.setGoalList(p1.getSubGoals());
        people.add(person);
    }

    // Perform each of the set up steps
    public void setUp() {
        populateFloorPlan();
        populateNodeArray();
        createEdges();
        isSetUp = true;
        routesComputed = false;
    }

    // Populate the array with 0 and 1s if there is a node on the map
    private void populateFloorPlan() {
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                for (int z = 0; z < numFloors; z++) {
                    floorPlan[i][j][z] = 0;
                    for (Wall wall : walls.get(z)) {
                        Point2d point2d = new Point2d(i, j);
                        if (wall.touches(point2d, 0.75)) {
                            floorPlan[i][j][z] = 1;
                            break;
                        }
                    }
                }
            }
        }
    }

    // Simply print a representation of the world, mainly for debugging
    public void printFloorPlan() throws WorldNotSetUpException {
        if (!isSetUp)
            throw new WorldNotSetUpException("printFloorPlan called before setting up world");
        for (int z = 0; z < numFloors; z++) {
            System.out.println("Printing floor: " + z);
            for (int i = 0; i < sideLength; i++) {
                for (int j = 0; j < sideLength; j++) {
                    if (floorPlan[j][i][z] == 0) {
                        System.out.print('\267');
                    } else {
                        System.out.print(floorPlan[j][i][z]);
                    }
                }
                System.out.println();
            }
        }
    }

    // Create a node object at each coordinate point
    private void populateNodeArray() {
        for (int z = 0; z < numFloors; z++) {
            for (int i = 0; i < sideLength; i++) {
                for (int j = 0; j < sideLength; j++) {
                    nodeArray[i][j][z] = null;
                    if (floorPlan[i][j][z] == 0) {
                        nodeArray[i][j][z] = new Vertex(i, j, z);
                        nodes.add(nodeArray[i][j][z]);
                    }
                }
            }
        }
    }

    // Walks through the nodes in the world, adding edges between them if it is valid to do so.
    // Starts in the top left of the building for each floor and adds edges right and down until
    // all the nodes have been visited
    private void createEdges() {
        for (int z = 0; z < numFloors; z++) {
            for (int i = 0; i < sideLength; i++) {
                for (int j = 0; j < sideLength; j++) {
                    if (floorPlan[i][j][z] == 0) {
                        // if not at right-most node
                        if (j < (sideLength - 1)) {
                            // check right
                            if (floorPlan[i][j + 1][z] == 0) {
                                edges.add(new Edge(nodeArray[i][j][z], nodeArray[i][j + 1][z], 1.0, z));
                            }
                            // check bottom right
                            if (i < (sideLength - 1) && floorPlan[i + 1][j + 1][z] == 0) {
                                edges.add(new Edge(nodeArray[i][j][z], nodeArray[i + 1][j + 1][z], Math.sqrt(2), z));
                            }
                        }
                        // if not at bottom node
                        if (i < sideLength - 1) {
                            // check bottom
                            if (floorPlan[i + 1][j][z] == 0) {
                                edges.add(new Edge(nodeArray[i][j][z], nodeArray[i + 1][j][z], 1.0, z));
                            }
                            // check bottom left
                            if (j > 0 && floorPlan[i + 1][j - 1][z] == 0) {
                                edges.add(new Edge(nodeArray[i][j][z], nodeArray[i + 1][j - 1][z], Math.sqrt(2), z));
                            }
                        }
                    }
                }
            }
        }
        setUpFloorConnections();
    }

    // Walks over each of the goals on the building layout and performs Dijkstras towards each one, storing the
    // resulting paths in an accessible list
    public void computeDijsktraTowards(ArrayList<Point3d> goals, ArrayList<Point3d> evacuationPoints) throws WorldNotSetUpException {
        if (!isSetUp)
            throw new WorldNotSetUpException("computerDijsktraTowards called before setting up world");

        for (int f = 0; f < goals.size(); f++) {

            FastDijkstra fastDijkstra = new FastDijkstra();
            for (int z = 0; z < numFloors; z++) {
                for (int i = 0; i < sideLength; i++) {
                    for (int j = 0; j < sideLength; j++) {
                        FibonacciHeapNode newNode = new FibonacciHeapNode(new NodeRecord((z * sideLength * sideLength) + (i * sideLength) + j));
                        fastDijkstra.getNodes().add(newNode);
                    }
                }
            }

            fastDijkstra.pathFind((int) (goals.get(f).z * sideLength * sideLength) + (int) goals.get(f).x * sideLength + (int) goals.get(f).y, sideLength * sideLength * numFloors, this);
            fdPOIList.add(fastDijkstra);
        }
        for (int g = 0; g < evacuationPoints.size(); g++) {

            FastDijkstra fastDijkstra = new FastDijkstra();

            for (int z = 0; z < numFloors; z++) {
                for (int i = 0; i < sideLength; i++) {
                    for (int j = 0; j < sideLength; j++) {
                        FibonacciHeapNode newNode = new FibonacciHeapNode(new NodeRecord((z * sideLength * sideLength) + (i * sideLength) + j));
                        fastDijkstra.getNodes().add(newNode);
                    }
                }
            }

            fastDijkstra.pathFind((int) (evacuationPoints.get(g).z * sideLength * sideLength) + (int) evacuationPoints.get(g).x * sideLength + (int) evacuationPoints.get(g).y, sideLength * sideLength * numFloors, this);
            fdEvacList.add(fastDijkstra);
        }
        routesComputed = true;
    }

    // Queries the Dijkstras objects and returns the path from the given coordinate to the given goal
    public Path getPath(int x, int y, int z, int goalID, boolean evac) throws RoutesNotComputedException {
        if (!routesComputed) {
            throw new RoutesNotComputedException("getPath called before routes were computed");
        }
        ArrayList<FastDijkstra> goalList = fdPOIList;
        if (evac) {
            goalList = fdEvacList;
        }

        FibonacciHeapNode fibonacciHeapNode = goalList.get(goalID).getNodes().get((z * (sideLength * sideLength)) + (x * sideLength) + y);

        NodeRecord nr = (NodeRecord) fibonacciHeapNode.getData();
        ArrayList<Node> nodeList = new ArrayList<Node>();
        while (true) {
            if (nr.predecessor == null) {
                // nodeList = new ArrayList<Node>();
                break;
            }
            Integer i = nr.predecessor;
            Integer prevZ = ((NodeRecord) goalList.get(goalID).getNodes().get(i).getData()).node / (sideLength * sideLength);
            Integer prevX = (((NodeRecord) goalList.get(goalID).getNodes().get(i).getData()).node % (sideLength * sideLength)) / sideLength;
            Integer prevY = ((NodeRecord) goalList.get(goalID).getNodes().get(i).getData()).node % sideLength;
            if (prevX == 0 && prevY == 0 && prevZ == 0) {
                nodeList.add(new Node(0, 0, 0));
                break;
            } else {
                nodeList.add(new Node(prevX, prevY, prevZ));
                nr = (NodeRecord) goalList.get(goalID).getNodes().get(i).getData();
            }
        }
        return new Path(nodeList);
    }

    // Computes the density map by querying each Dijkstra object and adding all the information together
    public int[][][] getStaticDensityMap() throws RoutesNotComputedException {
        if (!routesComputed) {
            throw new RoutesNotComputedException("getStaticDensityMap called before routes were computed");
        }
        if (fdEvacList.size() == 0) {
            return new int[sideLength][sideLength][numFloors];
        }

        if (staticDensityMap == null) { /* Create density map */
            staticDensityMap = new int[sideLength][sideLength][numFloors];
        }
        for (int z = 0; z < numFloors; z++) {
            for (int i = 0; i < sideLength; i++) {
                for (int j = 0; j < sideLength; j++) {
                    if (floorPlan[i][j][z] != 0)
                        continue;  // if node (i,j) is a wall, skip it

                    // Add whole path from (i,j) to density map
                    Path thisPath = getPath(i, j, z, 0, true);
                    int pathLength = thisPath.getNodes().size();
                    if (fdEvacList.size() > 1) {
                        for (int q = 1; q < fdEvacList.size(); q++) {
                            Path newPath = getPath(i, j, z, q, true);
                            if (newPath.getNodes().size() < pathLength) {
                                thisPath = newPath;
                                pathLength = newPath.getNodes().size();
                            }
                        }
                    }

                    for (Node n : thisPath.getNodes()) {
                        Point2d p = new Point2d(n.toPoint2d());
                        staticDensityMap[(int) p.x][(int) p.y][z]++;
                    }
                }
            }
        }

        return staticDensityMap;
    }

    public int getSideLength() {
        return this.sideLength;
    }

    public ArrayList<ArrayList<Wall>> getWalls() {
        return walls;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public ArrayList<Person> getPeople() {
        return people;
    }

    public void setEvac(Point3d evac) {
        this.evac = evac;
    }

    public void addFloorConnections(ArrayList<FloorConnection> floorConnections) {
        this.floorConnections.addAll(floorConnections);
    }

    // Creates edges for each of the floorConnection objects
    public void setUpFloorConnections() {
        for (FloorConnection fc : floorConnections) {
            edges.add(new Edge(nodeArray[(int) fc.location.x][(int) fc.location.y][fc.fromFloor],
                    nodeArray[(int) fc.location.x][(int) fc.location.y][fc.fromFloor + 1], 2, fc.fromFloor));
        }
    }

}
