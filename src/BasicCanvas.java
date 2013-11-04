import Dijkstra.Vertex;

public class BasicCanvas {

    public static void main(String[] args) throws Exception {
        double d = System.currentTimeMillis();

        World world = new World(10);

        world.setUp();
        world.printFloorPlan();
        world.computeDijsktraTowards(9, 9);

        Person p1 = new Person(1, 1);
        p1.setGoalList(world.getPath(1, 1).getSubGoals());
        for (Vertex v : p1.getGoalList()) {
            System.out.println(v);
        }

        System.out.println(p1.desiredMotion());
	    for (int i = 0; i < 15; i++) {
            System.out.println(p1.advance());
        }

        System.out.println("Executed in: " + (System.currentTimeMillis() - d) + "ms");
    }
}
