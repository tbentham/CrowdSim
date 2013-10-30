import Dijkstra.Vertex;

import java.util.LinkedList;

public class BasicCanvas {

    public static void main(String[] args) throws Exception {
        double d = System.currentTimeMillis();

        World world = new World(50);
        world.addWall(1, 1, 40, 1);
        world.addWall(40, 1, 40, 40);
        world.addWall(40, 40, 1, 40);
        world.addWall(1, 40, 1, 1);

        world.populateFloorPlan();
        world.populateVertexArray();
        world.createEdges();
        world.printFloorPlan();
        world.setGoal(3, 10);
        LinkedList<Vertex> path = world.getPath(10, 15);
        for (Vertex v : path) {
            System.out.println(v);
        }
        LinkedList<Vertex> path2 = world.getPath(20, 20);
        for (Vertex v : path2) {
            System.out.println(v);
        }

        System.out.println("Executed in: " + (System.currentTimeMillis() - d) + "ms");
    }
}
