import java.util.ArrayList;

public class BasicCanvas {

    public static void main(String[] args) throws Exception {
        double d = System.currentTimeMillis();

        World world = new World(20);

        world.setUp();
        world.printFloorPlan();
        world.computeDijsktraTowards(18, 18);

        Person p1 = new Person(2, 2);
        p1.setGoalList(world.getPath(2, 2).getSubGoals());

        Person p2 = new Person(4, 4);
        p2.setGoalList(world.getPath(4, 4).getSubGoals());

        Person p3 = new Person(18, 18);
        p3.setGoalList(world.getPath(18, 18).getSubGoals());

        ArrayList<Person> people = new ArrayList<Person>();
        people.add(p1);
        people.add(p2);
        people.add(p3);

        for(Person p : people)
            System.out.println(p.getLocation());

        for(int i = 0; i < 20; i++) {
            for(Person p : people)
                p.advance(people);
            System.out.println();
            System.out.println((i+1) + ".");
            for(Person p : people)
                System.out.println(p.getLocation());
        }

//        System.out.println(p1.desiredAcceleration());
//	    for (int i = 0; i < 15; i++) {
//            System.out.println(p1.advance());
//        }

        System.out.println("Executed in: " + (System.currentTimeMillis() - d) + "ms");
    }
}
