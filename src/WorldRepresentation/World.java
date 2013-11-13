package WorldRepresentation;

import Dijkstra.DijkstraAlgorithm;
import Dijkstra.Edge;
import Dijkstra.Graph;
import Dijkstra.Vertex;
import Exceptions.RoutesNotComputedException;
import Exceptions.WorldNotSetUpException;

import javax.vecmath.Point2d;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class World {

    private int sideLength;

    private ArrayList<Wall> walls;
    private int[][] floorPlan;

    private Vertex[][] nodeArray;
    private List<Vertex> nodes;
    private List<Edge> edges;

    private DijkstraAlgorithm dijkstra;
    
    private boolean isSetUp;
    private boolean routesComputed;

    public World(int sideLength) {
        this.sideLength = sideLength;
        
        walls = new ArrayList<Wall>();
        floorPlan = new int[sideLength][sideLength];
        
        nodeArray = new Vertex[sideLength][sideLength];
        nodes = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
        
        isSetUp = false;
        routesComputed = false;
    }

    public void addWall(Point2d from, Point2d to) {
        addWall(from.x, from.y, to.x, to.y);
    }

    public void addWall(double x1, double y1, double x2, double y2) {
        isSetUp = false;
        routesComputed = false;
        walls.add(new Wall(x1, y1, x2, y2));
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
            for (int j = 0; j < sideLength; j++)
                System.out.print(floorPlan[i][j]);
            System.out.println();
        }
    }

    public void printDijsktras() throws RoutesNotComputedException, WorldNotSetUpException {
        if (!routesComputed) {
            throw new RoutesNotComputedException("");
        }
        if (!isSetUp) {
            throw new WorldNotSetUpException("");
        }
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                System.out.print(round(dijkstra.distance.get(nodeArray[i][j]), 2) + " ");
            }
            System.out.println();
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
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
        dijkstra = new DijkstraAlgorithm(new Graph(nodes, edges));
        dijkstra.execute(nodeArray[x][y]);
        routesComputed = true;
    }

    public void computeDijsktraTowards(Point2d goal) throws WorldNotSetUpException {
        computeDijsktraTowards((int) Math.round(goal.x), (int) Math.round(goal.y));
    }

    public Path getPath(int x, int y) throws RoutesNotComputedException {
        if (!routesComputed)
            throw new RoutesNotComputedException("getPath called before routes were computed");
        return new Path(dijkstra.getPath(nodeArray[x][y]));
    }

    public Path getPath(Point2d location) throws RoutesNotComputedException {
        return getPath((int) Math.round(location.x), (int) Math.round(location.y));
    }
    
    public int getSideLength() {
    	return this.sideLength;
    }

    public ArrayList<Wall> getWalls() {
        return walls;
    }

}
