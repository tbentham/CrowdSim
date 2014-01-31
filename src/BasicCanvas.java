import Exceptions.PersonOverlapException;
import Exceptions.WallOverlapException;
import WorldRepresentation.LayoutChunk;
import WorldRepresentation.Person;
import WorldRepresentation.Wall;
import WorldRepresentation.World;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.vecmath.Point2d;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class BasicCanvas {

    public static void main(String[] args) throws Exception {

        Server server = new Server(8881);

        ResourceHandler staticFiles = new ResourceHandler();

        //Create a handler for jetty to serve static files
        staticFiles.setDirectoriesListed(true);
        staticFiles.setWelcomeFiles(new String[]{"client.html"});
        staticFiles.setResourceBase("./www");
        JettyExample jettyHandle = new JettyExample();

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{staticFiles, jettyHandle});
        server.setHandler(handlers);

        server.start();
        Cobject[] cobjs = null; //CALL THE COPS

        while (cobjs == null) {
            cobjs = jettyHandle.getLatestObjs();
            Thread.sleep(100);
        }

        double d = System.currentTimeMillis();

        World world = new World(100);
        Point2d goal = new Point2d(0, 0);

        for (Cobject co : cobjs) {
        	if (co.getType() == 0){
        		world.addWall(co.getFrom().x / 10.0, co.getFrom().y / 10.0, co.getTo().x / 10.0, co.getTo().y / 10.0);
        	}
        	else if (co.getType() == 2) {
        		//Do something with goals here.
        		System.out.println("I have a goal at " + co.getFrom().toString());
        		goal = new Point2d(co.getFrom().x/ 10 , co.getFrom().y / 10.0);
        	}
        }

        world.setUp();
        world.printFloorPlan();
        world.computeDijsktraTowards((int) goal.x, (int) goal.y);

        // world.printDijsktras();

        System.out.println("Dijsktra's Executed in: " + (System.currentTimeMillis() - d)
                + "ms Towards " + goal.x + ", " + goal.y);

        for(int i = 2; i < 500; i++) {
            try {
                world.addNewPersonAt((int)(Math.random()*100),(int)(Math.random()*100));
            } catch (PersonOverlapException e) {
            	
            } catch (WallOverlapException e) {
                // e.printStackTrace();
            }
        }

        ArrayList<LayoutChunk> chunks = new ArrayList<LayoutChunk>();
        LayoutChunk topLeft = new LayoutChunk(0, 50, 100, 50, world.getWalls());
        LayoutChunk topRight = new LayoutChunk(50, 100, 100, 50, world.getWalls());
        LayoutChunk bottomLeft = new LayoutChunk(0, 50, 50, 0, world.getWalls());
        LayoutChunk bottomRight = new LayoutChunk(50, 100, 50, 0, world.getWalls());
        chunks.add(topLeft);
        chunks.add(topRight);
        chunks.add(bottomLeft);
        chunks.add(bottomRight);

        for (LayoutChunk lc : chunks) {
            for (Wall w : world.getWalls()) {
                boolean startInside = false;
                boolean endInside = false;
                if (lc.isPointInside(w.getStartVector().x, w.getStartVector().y)) {
                    startInside = true;
                }
                if (lc.isPointInside(w.getEndVector().x, w.getEndVector().y)) {
                    endInside = true;
                }
                if (startInside || endInside || topLeft.numberOfIntersects(w) == 2) {
                    lc.addWall(w.getStartVector().x, w.getStartVector().y,
                            w.getEndVector().x, w.getEndVector().y);
                }
            }
        }

        for (Person p : world.getPeople()) {
            if (topLeft.isPointInside(p.getLocation().x, p.getLocation().y)) {
                topLeft.addPerson(p);
            }
            else if (topRight.isPointInside(p.getLocation().x, p.getLocation().y)) {
                topRight.addPerson(p);
            }
            else if (bottomLeft.isPointInside(p.getLocation().x, p.getLocation().y)) {
                bottomLeft.addPerson(p);
            }
            else if (bottomRight.isPointInside(p.getLocation().x, p.getLocation().y)) {
                bottomRight.addPerson(p);
            }
            else {
                System.out.println(p.getLocation().x);
                System.out.println(p.getLocation().y);
                throw new Exception("This shit is fucked");
            }
        }

        ArrayList<Thread> threads = new ArrayList<Thread>();

        Runnable topLeftTask = (Runnable) topLeft;
        Runnable topRightTask = (Runnable) topRight;
        Runnable bottomLeftTask = (Runnable) bottomLeft;
        Runnable bottomRightTask = (Runnable) bottomRight;

//        Thread worker1 = new Thread(topLeftTask);
//        threads.add(worker1);
//        Thread worker2 = new Thread(topRightTask);
//        threads.add(worker2);
//        Thread worker3 = new Thread(bottomLeftTask);
//        threads.add(worker3);
//        Thread worker4 = new Thread(bottomRightTask);
//        threads.add(worker4);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 250; i++) {
            Thread worker1 = new Thread(topLeftTask);
            threads.add(worker1);
            Thread worker2 = new Thread(topRightTask);
            threads.add(worker2);
            Thread worker3 = new Thread(bottomLeftTask);
            threads.add(worker3);
            Thread worker4 = new Thread(bottomRightTask);
            threads.add(worker4);
            worker1.start();
            worker2.start();
            worker3.start();
            worker4.start();
            int running;
            do {
                running = 0;
                for (Thread thread : threads) {
                    if (thread.isAlive()) {
                        running++;
                    }
                }
                //System.out.println("We have " + running + " running threads.");
            } while (running > 0);
        }



//    int running;
//        do {
//            running = 0;
//            for (Thread thread : threads) {
//                if (thread.isAlive()) {
//                    running++;
//                }
//            }
//            System.out.println("We have " + running + " running threads.");
//            Thread.sleep(500);
//        } while (running > 0);

        double endTime = System.currentTimeMillis();

        System.out.println("The simulation took " + (endTime - startTime))  ;

//        for (int i = 0; i < 100; i++) {
//            for (Person p : topLeft.getPeople()) {
//                p.advance(topLeft.getWalls(), topLeft.getPeople());
//            }
//            for (Person p : topRight.getPeople()) {
//                p.advance(topRight.getWalls(), topRight.getPeople());
//            }
//            for (Person p : bottomRight.getPeople()) {
//                p.advance(bottomRight.getWalls(), bottomRight.getPeople());
//            }
//            for (Person p : bottomLeft.getPeople()) {
//                p.advance(bottomLeft.getWalls(), bottomLeft.getPeople());
//            }
//        }

        ArrayList<Person> people = world.getPeople();

        System.out.println("Printing persons starting location");

        for (Person p : people) {
            System.out.println(p.getLocation());
        }

//        for (int i = 0; i < 100; i++) {
//            for (Person p : people)
//                p.advance(world, people);
//            if (i % 10 == 9) {
//                System.out.println();
//                System.out.println("Step " + (i + 1) + " (Simulated time: " + (i + 1) * 0.5 + "s)");
//            }
//        }

        System.out.println("Printing persons starting location");

        for (Person p : people) {
            System.out.println(p.locations.get(0));
        }


        System.out.println("Time taken before file I/O: " + (System.currentTimeMillis() - d));

        PrintWriter out = new PrintWriter("www/people.json");
        //out.print(peopleToJson(people));

        ArrayList<Person> output = new ArrayList<Person>();

        output.addAll(topLeft.getPeople());
        output.addAll(topRight.getPeople());
        output.addAll(bottomLeft.getPeople());
        output.addAll(bottomRight.getPeople());
        out.print(peopleToJson(output));
        System.out.println("I'm done");
        System.out.println("Total time taken: " + (System.currentTimeMillis() - d));
        out.close();



        server.join();

    }

    public static String peopleToJson(ArrayList<Person> people) {

        String[] locations = new String[people.size()];
        Person curPerson;
        String finalString = "";
        for (int i = 0; i < people.size(); i++) {
            curPerson = people.get(i);
            locations[i] = "";
            for (int j = 0; j < curPerson.locations.size(); j++) {
                locations[i] += "{\"x\":" + (curPerson.locations.get(j).x * 10.0) + ", \"y\":" + (curPerson.locations.get(j).y * 10.0) + "}";
                if (j < curPerson.locations.size() - 1)
                    locations[i] += ", ";
            }
        }

        finalString += "[";


        for (int i = 0; i < locations.length; i++) {
            finalString += "[";
            finalString += locations[i];
            finalString += "]";
            if (i < locations.length - 1) {
                finalString += ", ";
            }
        }


        finalString += "]";

        // System.out.println(finalString);

        return finalString;
    }

}


class JettyExample extends AbstractHandler {

    private JsonParser jparse;
    private Cobject[] objs;
    private boolean newObjs = false;

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        if (baseRequest.getMethod().equals("POST") && baseRequest.getParameter("objects") != null) {
            System.out.println("Recieved " + baseRequest.getParameter("objects"));

            jparse = new JsonParser(baseRequest.getParameter("objects"));
            try {
                objs = jparse.parse();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


            //Dump objects to screen for debugging
            for (Cobject c : objs) {
                System.out.println(c.toString());
            }

            newObjs = true;

            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);

        }

    }

    public Cobject[] getLatestObjs() {

        if (newObjs) {
            newObjs = false;
            return objs;
        } else return null; // probably ought to be some exception in the name of java programming.
    }

}

