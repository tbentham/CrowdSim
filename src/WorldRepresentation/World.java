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

    private int sideLength;

    private ArrayList<Wall> walls;
    private int[][] floorPlan;

    private Vertex[][] nodeArray;
    private List<Vertex> nodes;
    private List<Edge> edges;
    private FastDijkstra fastDijkstra;

    private boolean isSetUp;
    private boolean routesComputed;

    private ArrayList<Person> people;

    public World(int sideLength) {

        fastDijkstra = new FastDijkstra();

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

    public void addNewPersonAt(int x, int y) throws RoutesNotComputedException,
            PersonOverlapException, WallOverlapException {
        if (!routesComputed) {
            throw new RoutesNotComputedException("Cannot add people until world has routes computed");
        }
        for (Person p : people) {
            if (p.getLocation().x == x && p.getLocation().y == y) {
                throw new PersonOverlapException("Cannot create second person at " + x + ", " + y);
            }
        }
        Person person = new Person(x, y);
        for (Wall w : walls) {
            if (w.touches(person)) {
                throw new WallOverlapException("Cannot add person at " + x + " , " + y + " because wall exists there");
            }
        }
        person.setGoalList(getPath(x, y).getSubGoals());
        people.add(person);
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

    public void computeDijsktraTowards(int x, int y) throws WorldNotSetUpException {
        if (!isSetUp)
            throw new WorldNotSetUpException("computerDijsktraTowards called before setting up world");

        fastDijkstra = new FastDijkstra();
        fastDijkstra.nodes = new ArrayList<FibonacciHeapNode>();
        fastDijkstra.connections = new HashMap<Integer, ArrayList<Connection>>();

        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                FibonacciHeapNode newNode = new FibonacciHeapNode(new NodeRecord((i * sideLength) + j));
                fastDijkstra.nodes.add(newNode);
            }
        }
        FibonacciHeap fibonacciHeap = fastDijkstra.pathFind((x * sideLength) + y, sideLength * sideLength, this);

        routesComputed = true;
    }


    public Path getPath(int x, int y) throws RoutesNotComputedException {
        if (!routesComputed) {
            throw new RoutesNotComputedException("getPath called before routes were computed");
        }
        FibonacciHeapNode fibonacciHeapNode = fastDijkstra.nodes.get((x * sideLength) + y);
        NodeRecord nr = (NodeRecord) fibonacciHeapNode.getData();
        ArrayList<Node> nodeList = new ArrayList<Node>();
        while (true) {
            if (nr.predecessor == null) {
                // nodeList = new ArrayList<Node>();
                break;
            }
            Integer i = nr.predecessor;
            Integer prevX = ((NodeRecord) fastDijkstra.nodes.get(i).getData()).node / sideLength;
            Integer prevY = ((NodeRecord) fastDijkstra.nodes.get(i).getData()).node % sideLength;
            if (prevX == 0 && prevY == 0) {
                nodeList.add(new Node(0, 0));
                break;
            }
            else {
                nodeList.add(new Node(prevX, prevY));
                nr = (NodeRecord) fastDijkstra.nodes.get(i).getData();
            }
        }
        return new Path(nodeList);
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

}
