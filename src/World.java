import Dijkstra.DijkstraAlgorithm;
import Dijkstra.Edge;
import Dijkstra.Graph;
import Dijkstra.Vertex;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class World {

    private List<Wall> walls;
    private int[][] floorPlan;
    private int sideLength;
    private List<Vertex> nodes;
    private List<Edge> edges;
    private Vertex[][] nodeArray;
    private DijkstraAlgorithm dijkstra;

    public World(int sideLength) {
        this.sideLength = sideLength;
        walls = new ArrayList<Wall>();
        floorPlan = new int[sideLength][sideLength];
        nodes = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
        nodeArray = new Vertex[sideLength][sideLength];
    }

    public void addWall(double x1, double y1, double x2, double y2) {
        walls.add(new Wall(x1, y1, x2, y2));

    public void populateFloorPlan() {
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

    public void printFloorPlan() {
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                System.out.print(floorPlan[i][j]);
                if (j == (sideLength - 1)) {
                    System.out.println("");
                }
            }
        }
    }

    public void populateVertexArray() {
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                nodeArray[i][j] = null;
                if (floorPlan[i][j] == 0) {
                    Vertex location = new Vertex(i + "_" + j);
                    nodes.add(location);
                    nodeArray[i][j] = location;
                }
            }
        }
    }

    public void createEdges() {
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                if (floorPlan[i][j] == 0) {
                    // check right
                    // if not at far right edge
                    if (j < (sideLength - 1)) {
                        if (floorPlan[i][j + 1] == 0) {
                            edges.add(new Edge(nodeArray[i][j], nodeArray[i][j + 1], 1));
                        }
                        // check bottom right
                        if (i < (sideLength - 1)) {
                            if (floorPlan[i + 1][j + 1] == 0) {
                                edges.add(new Edge(nodeArray[i][j], nodeArray[i + 1][j + 1], Math.sqrt(2)));
                            }
                        }
                    }
                    // check bottom
                    if (i < sideLength - 1) {
                        if (floorPlan[i + 1][j] == 0) {
                            edges.add(new Edge(nodeArray[i][j], nodeArray[i + 1][j], 1));
                        }
                        // check bottom left
                        if (j != 0 && floorPlan[i + 1][j - 1] == 0) {
                            edges.add(new Edge(nodeArray[i][j], nodeArray[i + 1][j - 1], Math.sqrt(2)));
                        }
                    }
                }
            }
        }
    }

    public void setGoal(int x, int y) {
        Graph graph = new Graph(nodes, edges);
        dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(nodeArray[x][y]);
    }

    public LinkedList<Vertex> getPath(int x, int y) {
        return dijkstra.getPath(nodeArray[x][y]);
    }

}
