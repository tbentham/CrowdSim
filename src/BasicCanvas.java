import Dijkstra.Vertex;

import java.util.Collections;

public class BasicCanvas {

    public static void main(String[] args) throws Exception {
        double d = System.currentTimeMillis();

        World world = new World(5);

        world.setUp();
        world.printFloorPlan();
        world.computeDijsktraTowards(4, 4);
        Person p1 = new Person(1, 1);
        p1.goalList = world.getPath(1, 1).getVertices();
        Collections.reverse(p1.goalList);
        for (Vertex v : p1.goalList) {
            System.out.println(v);
        }

        System.out.println(p1.desiredMotion());

        System.out.println("Executed in: " + (System.currentTimeMillis() - d) + "ms");
    }
}
