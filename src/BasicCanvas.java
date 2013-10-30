import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BasicCanvas extends Application {

    private static int[][] floorPlan;
    private static List<BuildingObject> objects;
    private static List<Wall> walls;
    private static People people;

    public static void main(String[] args) throws Exception {
        double d = System.currentTimeMillis();

        walls = new ArrayList<Wall>();
        walls.add(new Wall(20, 20, 120, 20));
        walls.add(new Wall(120, 120, 20, 120));
        walls.add(new Wall(120, 20, 120, 120));
        walls.add(new Wall(20, 120, 20, 20));
        walls.add(new Wall(20, 20, 120, 120));

        int length = 200;

        floorPlan = new int[length][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
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

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                System.out.print(floorPlan[i][j]);
                if (j == (length - 1)) {
                    System.out.println("");
                }
            }
        }


        List<Vertex> nodes;
        List<Edge> edges;

        nodes = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
        Vertex[][] nodeArray = new Vertex[length][length];

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                nodeArray[i][j] = null;
                if (floorPlan[i][j] == 0) {
                    Vertex location = new Vertex(i + "_" + j);
                    nodes.add(location);
                    nodeArray[i][j] = location;
                }
            }
        }

        int edgeCount = 0;

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (floorPlan[i][j] == 0) {
                    // check right
                    // if not at far right edge
                    if (j < (length - 1)) {
                        if (floorPlan[i][j + 1] == 0) {
                            edgeCount++;
                            edges.add(new Edge(nodeArray[i][j], nodeArray[i][j + 1], 1));
                        }
                        // check bottom right
                        if (i < (length - 1)) {
                            if (floorPlan[i + 1][j + 1] == 0) {
                                edgeCount++;
                                edges.add(new Edge(nodeArray[i][j], nodeArray[i + 1][j + 1], Math.sqrt(2)));
                            }
                        }
                    }
                    // check bottom
                    if (i < length - 1) {
                        if (floorPlan[i + 1][j] == 0) {
                            edgeCount++;
                            edges.add(new Edge(nodeArray[i][j], nodeArray[i + 1][j], 1));
                        }
                        // check bottom left
                        if (j != 0 && floorPlan[i + 1][j - 1] == 0) {
                            edgeCount++;
                            edges.add(new Edge(nodeArray[i][j], nodeArray[i + 1][j - 1], Math.sqrt(2)));
                        }
                    }
                }
            }
        }

        System.out.println(edgeCount);

        Graph graph = new Graph(nodes, edges);
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(nodes.get(2010));
        LinkedList<Vertex> path = dijkstra.getPath(nodes.get(10050));
        for (Vertex vertex : path) {
            System.out.println(vertex);
        }
//        people = new People();
//        Person p1 = new Person(50, 40);
//        p1.setGoal(90, 90);
//        people.add(objects, p1);
//        launch();

        System.out.println("Executed in: " + (System.currentTimeMillis() - d) + "ms");
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Drawing Operations Test");
        Group root = new Group();
        Canvas canvas = new Canvas(500, 500);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawShapes(gc);
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public void drawShapes(GraphicsContext gc) {
        for (BuildingObject buildingObject : objects) {
            buildingObject.draw(gc);
        }
        gc.setFill(Color.BLUE);
        for (Person person : people) {
            person.draw(gc);
        }
    }
}
