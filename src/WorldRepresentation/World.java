package WorldRepresentation;

import Dijkstra.Edge;
import Dijkstra.Vertex;
import Exceptions.PersonOverlapException;
import Exceptions.RoutesNotComputedException;
import Exceptions.WallOverlapException;
import Exceptions.WorldNotSetUpException;
import NewDijkstra.Connection;
import NewDijkstra.FastDijkstra;
import NewDijkstra.Node;
import NewDijkstra.NodeRecord;
import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class World {

    public int sideLength;

    private ArrayList<Wall> walls;
    private int[][] floorPlan;

    private Vertex[][] nodeArray;
    private List<Vertex> nodes;
    private List<Edge> edges;
    public Point2d evac;

    private int[][] densityMap;

    private boolean isSetUp;
    private boolean routesComputed;
    public ArrayList<FastDijkstra> fdPOIList;
    public ArrayList<FastDijkstra> fdEvacList;

    public ArrayList<Point2d> poi;

    private ArrayList<Person> people;

    public World(int sideLength) {

        fdPOIList = new ArrayList<FastDijkstra>();
        fdEvacList = new ArrayList<FastDijkstra>();

        this.sideLength = sideLength;

        walls = new ArrayList<Wall>();
        floorPlan = new int[sideLength][sideLength];

        nodeArray = new Vertex[sideLength][sideLength];
        nodes = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();

        isSetUp = false;
        routesComputed = false;

        people = new ArrayList<Person>();
    }

    public void addWall(double x1, double y1, double x2, double y2) {
        isSetUp = false;
        routesComputed = false;
        walls.add(new Wall(x1, y1, x2, y2));
    }

    public void addNewPersonAt(int x, int y, int goalID, boolean evac) throws RoutesNotComputedException,
            PersonOverlapException, WallOverlapException {
        if (!routesComputed) {
            throw new RoutesNotComputedException("Cannot add people until world has routes computed");
        }
        for (Person p : people) {
            if (p.getLocation().x == x && p.getLocation().y == y) {
                throw new PersonOverlapException("Cannot create second person at " + x + ", " + y);
            }
        }
        Person person = new Person(x, y, goalID);
        person.evacBool = evac;
        for (Wall w : walls) {
            if (w.touches(person)) {
                throw new WallOverlapException("Cannot add person at " + x + " , " + y + " because wall exists there");
            }
        }
        Path p1 = getPath(x, y, goalID, evac);
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
        isSetUp = true;
        routesComputed = false;
    }

    private void populateFloorPlan() {
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                floorPlan[i][j] = 0;
                for (Wall wall : walls) {
                    Point2d point2d = new Point2d(i, j);
                    if (wall.touches(point2d, 1)) {
                        floorPlan[i][j] = 1;
                        break;
                    }
                }
            }
        }
    }

    public void printFloorPlan() throws WorldNotSetUpException {
        if (!isSetUp)
            throw new WorldNotSetUpException("printFloorPlan called before setting up world");
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                if (floorPlan[j][i] == 0) {
                    System.out.print('\267');
                }
                else {
                    System.out.print(floorPlan[j][i]);
                }
            }
            System.out.println();
        }
    }

    private void populateNodeArray() {
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                nodeArray[i][j] = null;
                if (floorPlan[i][j] == 0) {
                    nodeArray[i][j] = new Vertex(i, j);
                    nodes.add(nodeArray[i][j]);
                }
            }
        }
    }

    private void createEdges() {
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                if (floorPlan[i][j] == 0) {
                    // if not at right-most node
                    if (j < (sideLength - 1)) {
                        // check right
                        if (floorPlan[i][j + 1] == 0) {
                            edges.add(new Edge(nodeArray[i][j], nodeArray[i][j + 1], 1.0));
                        }
                        // check bottom right
                        if (i < (sideLength - 1) && floorPlan[i + 1][j + 1] == 0) {
                            edges.add(new Edge(nodeArray[i][j], nodeArray[i + 1][j + 1], Math.sqrt(2)));
                        }
                    }
                    // if not at bottom node
                    if (i < sideLength - 1) {
                        // check bottom
                        if (floorPlan[i + 1][j] == 0) {
                            edges.add(new Edge(nodeArray[i][j], nodeArray[i + 1][j], 1.0));
                        }
                        // check bottom left
                        if (j > 0 && floorPlan[i + 1][j - 1] == 0) {
                            edges.add(new Edge(nodeArray[i][j], nodeArray[i + 1][j - 1], Math.sqrt(2)));
                        }
                    }
                }
            }
        }
    }

    public void computeDijsktraTowards(ArrayList<Point2d> goals, ArrayList<Point2d> evacuationPoints) throws WorldNotSetUpException {
        if (!isSetUp)
            throw new WorldNotSetUpException("computerDijsktraTowards called before setting up world");

        this.poi = poi;

        for ( int f = 0; f < goals.size(); f++) {

            FastDijkstra fastDijkstra = new FastDijkstra();
            fastDijkstra.nodes = new ArrayList<FibonacciHeapNode>();
            fastDijkstra.connections = new HashMap<Integer, ArrayList<Connection>>();

            for (int i = 0; i < sideLength; i++) {
                for (int j = 0; j < sideLength; j++) {
                    FibonacciHeapNode newNode = new FibonacciHeapNode(new NodeRecord((i * sideLength) + j));
                    fastDijkstra.nodes.add(newNode);
                }
            }

            FibonacciHeap fibonacciHeap = fastDijkstra.pathFind((int) goals.get(f).x * sideLength + (int) goals.get(f).y, sideLength * sideLength, this);
            fdPOIList.add(fastDijkstra);
        }
        for ( int g = 0; g < evacuationPoints.size(); g++) {

            FastDijkstra fastDijkstra = new FastDijkstra();
            fastDijkstra.nodes = new ArrayList<FibonacciHeapNode>();
            fastDijkstra.connections = new HashMap<Integer, ArrayList<Connection>>();

            for (int i = 0; i < sideLength; i++) {
                for (int j = 0; j < sideLength; j++) {
                    FibonacciHeapNode newNode = new FibonacciHeapNode(new NodeRecord((i * sideLength) + j));
                    fastDijkstra.nodes.add(newNode);
                }
            }

            FibonacciHeap fibonacciHeap = fastDijkstra.pathFind((int) evacuationPoints.get(g).x * sideLength + (int) evacuationPoints.get(g).y, sideLength * sideLength, this);
            fdEvacList.add(fastDijkstra);
        }
        routesComputed = true;
    }


    public Path getPath(int x, int y, int goalID, boolean evac) throws RoutesNotComputedException {
        if (!routesComputed) {
            throw new RoutesNotComputedException("getPath called before routes were computed");
        }
        ArrayList<FastDijkstra> goalList = fdPOIList;
        if (evac) {
            goalList = fdEvacList;
        }

        FibonacciHeapNode fibonacciHeapNode = goalList.get(goalID).nodes.get((x * sideLength) + y);

        NodeRecord nr = (NodeRecord) fibonacciHeapNode.getData();
        ArrayList<Node> nodeList = new ArrayList<Node>();
        while (true) {
            if (nr.predecessor == null) {
                // nodeList = new ArrayList<Node>();
                break;
            }
            Integer i = nr.predecessor;
            Integer prevX = ((NodeRecord) goalList.get(goalID).nodes.get(i).getData()).node / sideLength;
            Integer prevY = ((NodeRecord) goalList.get(goalID).nodes.get(i).getData()).node % sideLength;
            if (prevX == 0 && prevY == 0) {
                nodeList.add(new Node(0, 0));
                break;
            }
            else {
                nodeList.add(new Node(prevX, prevY));
                nr = (NodeRecord) goalList.get(goalID).nodes.get(i).getData();
            }
        }
        return new Path(nodeList);
    }


    public int[][] getDensityMap() throws RoutesNotComputedException {
        if (!routesComputed) {
            throw new RoutesNotComputedException("getPath called before routes were computed");
        }
        if (fdEvacList.size() == 0) {
            return new int[sideLength][sideLength];
        }

        if ( densityMap == null ) { /* Create density map */
            densityMap = new int[sideLength][sideLength];
            for (int i = 0; i < sideLength; i++) {
                for (int j = 0; j < sideLength; j++) {
                    densityMap[i][j] = 0;  // initialise density values
                }
            }
            for (int i = 0; i < sideLength; i++) {
                for (int j = 0; j < sideLength; j++) {
                    if (floorPlan[i][j] != 0 )
                        continue;  // if node (i,j) is a wall, skip it

    				/* Add whole path from (i,j) to density map */

                    // changed for compiling FIX ME
                    Path thisPath = getPath(i, j, 0, true);
                    int pathLength = thisPath.getNodes().size();
                    if (fdEvacList.size() > 1) {
                        for (int q = 1; q < fdEvacList.size(); q++) {
                           Path newPath = getPath(i, j, q, true);
                           if (newPath.getNodes().size() < pathLength) {
                               thisPath = newPath;
                               pathLength = newPath.getNodes().size();
                           }
                        }
                    }

                    for ( Node n : thisPath.getNodes() ) {
                        Point2d p = new Point2d(n.toPoint2d());
                        densityMap[(int) p.x][(int) p.y]++;
                    }
                }
            }
        }

        return densityMap;
    }

    public int getDensity(int i, int j) throws RoutesNotComputedException {
        return (getDensityMap())[i][j];
    }

    public int getSideLength() {
        return this.sideLength;
    }

    public ArrayList<Wall> getWalls() {
        return walls;
    }

    public int[][] getFloorPlan() {
        return floorPlan;
    }

    public Vertex[][] getNodeArray() {
        return nodeArray;
    }

    public List<Vertex> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
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

    public void setEvac(Point2d evac) {
        this.evac = evac;
    }

}
