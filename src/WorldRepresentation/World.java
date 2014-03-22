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

public class World {

    public int sideLength;

    private ArrayList<ArrayList<Wall>> walls;
    private int[][][] floorPlan;

    private Vertex[][][] nodeArray;
    private ArrayList<Vertex> nodes;
    private ArrayList<Edge> edges;
    public Point3d evac;

    private int[][][] staticDensityMap;

    private boolean isSetUp;
    private boolean routesComputed;
    public ArrayList<FastDijkstra> fdPOIList;
    public ArrayList<FastDijkstra> fdEvacList;

    public ArrayList<Point2d> poi;

    private ArrayList<Person> people;

    public int numFloors;

    public ArrayList<FloorConnection> floorConnections;

    public World(int sideLength, int numFloors) {

        fdPOIList = new ArrayList<FastDijkstra>();
        fdEvacList = new ArrayList<FastDijkstra>();

        this.numFloors = numFloors;
        this.sideLength = sideLength;

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
        Path p1 = getPath(x, y, floor, goalID, evac);
        person.setGoalList(p1.getSubGoals());
        people.add(person);
        if (person.getGoalList().size() == 0) {

            System.out.println();
        }
    }

    public void setUp() {
        populateFloorPlan();
        populateNodeArray();
        createEdges();
        // edges.add(new Edge(nodeArray[50][50][1], nodeArray[50][50][0], 2, 0));
        isSetUp = true;
        routesComputed = false;
    }

    private void populateFloorPlan() {
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                for (int z = 0; z < numFloors; z++) {
                    floorPlan[i][j][z] = 0;
                    for (Wall wall : walls.get(z)) {
                        Point2d point2d = new Point2d(i, j);
                        if (wall.touches(point2d, 1)) {
                            floorPlan[i][j][z] = 1;
                            break;
                        }
                    }
                }
            }
        }
    }

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
                        System.out.print(floorPlan[j][i]);
                    }
                }
                System.out.println();
            }
        }
    }

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
    }

    public void computeDijsktraTowards(ArrayList<Point3d> goals, ArrayList<Point3d> evacuationPoints) throws WorldNotSetUpException {
        if (!isSetUp)
            throw new WorldNotSetUpException("computerDijsktraTowards called before setting up world");

        this.poi = poi;

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


    public int[][][] getStaticDensityMap() throws RoutesNotComputedException {
        if (!routesComputed) {
            throw new RoutesNotComputedException("getPath called before routes were computed");
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

    				/* Add whole path from (i,j) to density map */

                    Path thisPath = getPath(i, j, 0, 0, true);
                    int pathLength = thisPath.getNodes().size();
                    if (fdEvacList.size() > 1) {
                        for (int q = 1; q < fdEvacList.size(); q++) {
                            Path newPath = getPath(i, j, 0, q, true);
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

    public int getStaticDensity(int i, int j, int z) throws RoutesNotComputedException {
        return (getStaticDensityMap())[i][j][z];
    }

    public int getSideLength() {
        return this.sideLength;
    }

    public ArrayList<ArrayList<Wall>> getWalls() {
        return walls;
    }

    public int[][][] getFloorPlan() {
        return floorPlan;
    }

    public Vertex[][][] getNodeArray() {
        return nodeArray;
    }

    public ArrayList<Vertex> getNodes() {
        return nodes;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public boolean isSetUp() {
        return isSetUp;
    }

    public boolean areRoutesComputed() {
        return routesComputed;
    }

    public ArrayList<Person> getPeople() {
        return people;
    }

    public void setEvac(Point3d evac) {
        this.evac = evac;
    }

    public void addFloorConnection(FloorConnection floorConnection) {
        floorConnections.add(floorConnection);
    }

}
