import Exceptions.PersonOverlapException;
import Exceptions.WallOverlapException;
import WorldRepresentation.*;
import com.google.gson.Gson;
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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CyclicBarrier;

public class BasicCanvas {

    static int TIME_STEPS = 600;
    static int PEOPLE = 100;

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

        ArrayList<Point2d> poi = new ArrayList<Point2d>();
        ArrayList<Point2d> evacuationPoints = new ArrayList<>();

        World world = new World(100);
        Point2d goal;
        Point2d evac = new Point2d(0, 0);
        boolean evacBool = false;

        for (Cobject co : cobjs) {
            if (co.getType() == 0) {

                world.addWall(co.getFrom().x / 10.0, co.getFrom().y / 10.0, co.getTo().x / 10.0, co.getTo().y / 10.0);
            } else if (co.getType() == 2) {

                System.out.println("I have a wl at " + co.getFrom().toString());
                goal = new Point2d(co.getFrom().x / 10.0, co.getFrom().y / 10.0);
                poi.add(goal);
            } else if (co.getType() == 3) {

                evac = new Point2d(co.getFrom().x / 10.0, co.getFrom().y / 10.0);
                evacuationPoints.add(evac);
            } else if (co.getType() == 4) {

                evacBool = true;
            }
        }

        world.setUp();
        world.printFloorPlan();
        world.setEvac(evac);

        world.computeDijsktraTowards(poi, evacuationPoints);

        System.out.println("Dijsktra's Executed in: " + (System.currentTimeMillis() - d)
                + "ms");

        for (int i = 0; i < PEOPLE; i++) {
            try {
                int num = (int) Math.round(Math.random() * (poi.size() - 1));
                world.addNewPersonAt((int) (Math.random() * 100), (int) (Math.random() * 100), num, evacBool);
            } catch (PersonOverlapException e) {

            } catch (WallOverlapException e) {
            }
        }


        //Each chunk needs a reference to its own queue, every queue should be kept in a publicly accessible hashmap.
        LayoutChunk[][] chunks2d = new LayoutChunk[2][2];

        CyclicBarrier barrier = new CyclicBarrier(4, new ChunkSync());
        ArrayList<LayoutChunk> chunks = new ArrayList<LayoutChunk>();
        LayoutChunk topLeft = new LayoutChunk(0, 50, 100, 50, world.getWalls(), barrier, TIME_STEPS, world);
        LayoutChunk topRight = new LayoutChunk(50, 100, 100, 50, world.getWalls(), barrier, TIME_STEPS, world);
        LayoutChunk bottomLeft = new LayoutChunk(0, 50, 50, 0, world.getWalls(), barrier, TIME_STEPS, world);
        LayoutChunk bottomRight = new LayoutChunk(50, 100, 50, 0, world.getWalls(), barrier, TIME_STEPS, world);


        chunks.add(bottomLeft);
        chunks.add(topLeft);
        chunks.add(bottomRight);
        chunks.add(topRight);

        chunks2d[0][0] = bottomLeft;
        bottomLeft.addChunks(chunks2d);
        chunks2d[0][1] = topLeft;
        topLeft.addChunks(chunks2d);
        chunks2d[1][0] = bottomRight;
        bottomRight.addChunks(chunks2d);
        chunks2d[1][1] = topRight;
        topRight.addChunks(chunks2d);

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
            } else if (topRight.isPointInside(p.getLocation().x, p.getLocation().y)) {
                topRight.addPerson(p);
            } else if (bottomLeft.isPointInside(p.getLocation().x, p.getLocation().y)) {
                bottomLeft.addPerson(p);
            } else if (bottomRight.isPointInside(p.getLocation().x, p.getLocation().y)) {
                bottomRight.addPerson(p);
            } else {
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

        long startTime = System.currentTimeMillis();

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

        for (Thread t : threads) {
            t.join();
        }
        System.out.println("All threads have finished.");

        // double startTime = System.currentTimeMillis();

//        for (int i = 0; i < TIME_STEPS; i++) {
//            for (Person p : world.getPeople()) {
//                p.advance(world, world.getPeople(), 0.25);
//            }
//        }

        double endTime = System.currentTimeMillis();

        System.out.println("The simulation took " + (endTime - startTime));

        ArrayList<Person> people = world.getPeople();

        System.out.println("Printing persons starting location");

        for (Person p : people) {
            System.out.println(p.getLocation());
        }

        System.out.println("Printing persons starting location");

        for (Person p : people) {
            System.out.println(p.locations.get(0));
        }


        System.out.println("Time taken before file I/O: " + (System.currentTimeMillis() - d));

        ArrayList<Person> output = new ArrayList<Person>();

        output.addAll(topLeft.getPeople());
        output.addAll(topRight.getPeople());
        output.addAll(bottomLeft.getPeople());
        output.addAll(bottomRight.getPeople());
        // output.addAll(world.getPeople());

        Point2d[][] locations = new Point2d[output.size()][TIME_STEPS + 1];
        // loop through people
        for (int i = 0; i < output.size(); i++) {
            Person p = output.get(i);
            for (int j = 0; j < p.locations.size(); j++) {
                try {
                    if (p.locations.get(j) == null) {
                        break;
                    }
                    locations[i][j] = p.locations.get(j);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Boolean[][] stuckStatus = new Boolean[output.size()][output.get(1).locations.size() + 1];
        for (int i = 0; i < output.size(); i++) {
            Person p = output.get(i);
            stuckStatus[i] = p.blockedList.toArray(new Boolean[p.blockedList.size()]);
        }


        toJson(stuckStatus, "www/stuck.json");
        toJson(locations, "www/people.json");
        toJson(world.getDensityMap(), "www/bottlenecks.json");
        System.out.println("I'm done");
        System.out.println("Total time taken: " + (System.currentTimeMillis() - d));

        server.join();

    }

    public static void toJson(Object people, String outputFile) {

        Gson gson = new Gson();
        String json = gson.toJson(people);

        try {
            FileWriter writer = new FileWriter(outputFile);
            writer.write(json);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


class JettyExample extends AbstractHandler {

    private JsonParser jparseO;
    private JsonParser jparseC;

    private Cobject[] objs;
    private HashMap<String, Integer> conf;

    private boolean newObjs = false;

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        if (baseRequest.getMethod().equals("POST") && baseRequest.getParameter("objects") != null) {
            System.out.println("Recieved " + baseRequest.getParameter("objects"));

            jparseO = new JsonParser(baseRequest.getParameter("objects"));
            jparseC = new JsonParser(baseRequest.getParameter("config"));

            try {
                objs = jparseO.parse();
                conf = jparseC.parseConf();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


            //Dump objects to screen for debugging
            for (Cobject c : objs) {
                System.out.println(c.toString());
            }
            for (String s: conf.keySet()) {
                System.out.println(s + ": " + conf.get(s));

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

