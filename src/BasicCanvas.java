import java.util.ArrayList;

public class BasicCanvas {

    public static void main(String[] args) throws Exception {
        double d = System.currentTimeMillis();

        World world = new World(100);

        world.setUp();
        world.printFloorPlan();
        world.computeDijsktraTowards(9, 9);

        Person p1 = new Person(1, 1);
        p1.setGoalList(world.getPath(1, 1).getSubGoals());

        Person p3 = new Person(9, 9);
        p3.setGoalList(world.getPath(9, 9).getSubGoals());

        Person p2 = new Person(2, 2);
        p2.setGoalList(world.getPath(2, 2).getSubGoals());

        ArrayList<Person> people = new ArrayList<Person>();
        people.add(p1);
        people.add(p2);
        people.add(p3);

        for(Person p : people) {
            System.out.println(p.getLocation());
        }

        for(int i = 0; i < 10; i++) {
            for(Person p : people) {
                p.advance(people);
            }
            for(Person p : people) {
                System.out.println(p.getLocation());
            }
        }

//        System.out.println(p1.desiredAcceleration());
//	    for (int i = 0; i < 15; i++) {
//            System.out.println(p1.advance());
//        }

        System.out.println("Executed in: " + (System.currentTimeMillis() - d) + "ms");
    }
}
