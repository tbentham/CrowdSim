import Dijkstra.Vertex;

import java.util.LinkedList;

public class BasicCanvas {

    public static void main(String[] args) throws Exception {
        double d = System.currentTimeMillis();

        World world = new World(3);
        world.addWall(1, 1, 1, 2);

        world.setUp();
        world.printFloorPlan();
        world.computeDijsktraTowards(0, 2);
        Thread.sleep(5);
        LinkedList<Vertex> path = world.getPath(2, 2).getVertices();
        for (Vertex v : path) {
            System.out.println(v);
        }
        System.out.println();
        System.out.println("Subgoals:");

        LinkedList<Vertex> path2 = world.getPath(2, 2).getSubGoals();
        for (Vertex v : path2) {
            System.out.println(v);
        }

        System.out.println("Executed in: " + (System.currentTimeMillis() - d) + "ms");
    }
}
