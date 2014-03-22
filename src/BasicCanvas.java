import Dijkstra.Edge;
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
import javax.vecmath.Point3d;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class BasicCanvas {


    public static void main(String[] args) throws Exception {

        boolean interactive = true;
        String objectsFileName = "";
        String configFileName = "";
        int NUM_THREADS = 4;
        if (args.length > 5 && args[0].equals("-o") && args[2].equals("-c") && args[4].equals("-n")) {
            objectsFileName = args[1];
            configFileName = args[3];
            NUM_THREADS = Integer.parseInt(args[5]);
            interactive = false;
        }

        Integer TIME_STEPS = 600;
        Integer EVAC_TIME = 0;

        //This used to be an int, hopefully it wont cause trouble
        Integer PEOPLE = 100;
        Integer ASTAR = 1;
        Integer ASTAR_FREQUENCY = 5;
        Integer numFloors = 2;

        Cobject[] cobjs;
        Server server = new Server();

        if (interactive) {
            server = new Server(8881);

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
            cobjs = null; //CALL THE COPS

            while (cobjs == null) {
                cobjs = jettyHandle.getLatestObjs();
                Thread.sleep(100);
            }

            TIME_STEPS = ((Double) jettyHandle.getConfig().get("totalTime")).intValue();
            EVAC_TIME = ((Double) jettyHandle.getConfig().get("evacTime")).intValue();
            PEOPLE = ((Double) jettyHandle.getConfig().get("numPeople")).intValue();
            ASTAR = ((Double) jettyHandle.getConfig().get("astarToggle")).intValue();
            ASTAR_FREQUENCY = ((Double) jettyHandle.getConfig().get("astarFreq")).intValue();
        } else {
            // Convert object file input into string and store in objects
            final String EoL = System.getProperty("line.separator");
            List<String> lines = Files.readAllLines(Paths.get(objectsFileName),
                    Charset.defaultCharset());

            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line).append(EoL);
            }
            final String objects = sb.toString();
            // Convert config file input into string and store in config
            lines = Files.readAllLines(Paths.get(configFileName),
                    Charset.defaultCharset());

            sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line).append(EoL);
            }
            final String config = sb.toString();

            JsonParser jsonObjectParser = new JsonParser(objects);
            JsonParser jsonConfigParser = new JsonParser(config);

            cobjs = jsonObjectParser.parse();
            HashMap<String, Double> conf = jsonConfigParser.parseConf();

            TIME_STEPS = conf.get("totalTime").intValue();
            EVAC_TIME = conf.get("evacTime").intValue();
            PEOPLE = conf.get("numPeople").intValue();
            ASTAR = conf.get("astarToggle").intValue();
            ASTAR_FREQUENCY = conf.get("astarFreq").intValue();

        }

        double d = System.currentTimeMillis();

        ArrayList<Point3d> poi = new ArrayList<Point3d>();
        ArrayList<Point3d> evacuationPoints = new ArrayList<Point3d>();

        World world = new World(100, numFloors);
        Point3d goal;
        Point3d evac = new Point3d(0, 0, 0);
        boolean evacBool = false;
        ArrayList<FloorConnection> stairs = new ArrayList<>();

        for (Cobject co : cobjs) {
            if (co.getType() == 0) {

                world.addWall(co.getFrom().x / 10.0, co.getFrom().y / 10.0, co.getTo().x / 10.0, co.getTo().y / 10.0, (int) co.getFrom().z);
            } else if (co.getType() == 2) {

                System.out.println("I have a wl at " + co.getFrom().toString());
                goal = new Point3d(co.getFrom().x / 10.0, co.getFrom().y / 10.0, (int) co.getFrom().z);
                poi.add(goal);
            } else if (co.getType() == 3) {

                evac = new Point3d(co.getFrom().x / 10.0, co.getFrom().y / 10.0, (int) co.getFrom().z);
                evacuationPoints.add(evac);
            } else if (co.getType() == 4) {
                stairs.add(new FloorConnection(co.getFrom().x / 10.0, co.getFrom().y / 10.0, (int) co.getFrom().z, (int) co.getFrom().z + 1));
                // evacBool = true;
            }
        }

        if (poi.size() == 0) {
            System.err.println("Add a point of interest you mug");
            System.exit(1);
        }
        if (evacuationPoints.size() == 0) {
            System.err.println("No fuckin' evacuation points mate?");
        }

        world.setUp();
        world.printFloorPlan();
        world.setEvac(evac);
        for (FloorConnection fc : stairs) {
            world.getEdges().add(new Edge(world.getNodeArray()[(int) fc.location.x][(int) fc.location.y][fc.fromFloor],
                    world.getNodeArray()[(int) fc.location.x][(int) fc.location.y][fc.fromFloor + 1], 2, fc.fromFloor));
        }

        for (FloorConnection floorConnection : stairs) {
            world.addFloorConnection(floorConnection);
        }

        world.computeDijsktraTowards(poi, evacuationPoints);

        System.out.println("Dijsktra's Executed in: " + (System.currentTimeMillis() - d)
                + "ms");

        for (int i = 0; i < PEOPLE; i++) {
            try {
                int num = (int) Math.round(Math.random() * (poi.size() - 1));
                // world.addNewPersonAt((int) (Math.random() * 100), (int) (Math.random() * 100), 1, num, evacBool);
                world.addNewPersonAt((int) (Math.random() * 100), (int) (Math.random() * 100), 1, num, evacBool);

            } catch (PersonOverlapException e) {

            } catch (WallOverlapException e) {
            }
        }


        //Each chunk needs a reference to its own queue, every queue should be kept in a publicly accessible hashmap.

        int delta = 100 / NUM_THREADS;
        LayoutChunk[] chunks2d = new LayoutChunk[NUM_THREADS];

        ChunkSync chunkSyn = new ChunkSync();
        CyclicBarrier barrier = new CyclicBarrier(NUM_THREADS, chunkSyn);
        ArrayList<LayoutChunk> chunks = new ArrayList<LayoutChunk>();


        for (int i = 0; i < NUM_THREADS; i++) {
            if (i != NUM_THREADS - 1) {
                LayoutChunk nextChunk = new LayoutChunk(0, 100, i * delta, (i + 1) * delta, world.getWalls(), barrier, TIME_STEPS, world, EVAC_TIME, ASTAR, ASTAR_FREQUENCY, numFloors);
                chunks.add(nextChunk);
            } else {
                LayoutChunk nextChunk = new LayoutChunk(0, 100, i * delta, 100, world.getWalls(), barrier, TIME_STEPS, world, EVAC_TIME, ASTAR, ASTAR_FREQUENCY, numFloors);
                chunks.add(nextChunk);
            }
        }

        chunkSyn.addChunks(chunks);

        for (int i = 0; i < NUM_THREADS; i++) {
            chunks2d[i] = chunks.get(i);
            chunks.get(i).addChunks(chunks2d);
        }

        for (LayoutChunk lc : chunks) {
            for (int i = 0; i < numFloors; i++)
                for (Wall w : world.getWalls().get(i)) {
                    boolean startInside = false;
                    boolean endInside = false;
                    if (lc.isPointInside(w.getStartVector().x, w.getStartVector().y)) {
                        startInside = true;
                    }
                    if (lc.isPointInside(w.getEndVector().x, w.getEndVector().y)) {
                        endInside = true;
                    }
                    if (startInside || endInside || lc.numberOfIntersects(w) == 2) {
                        lc.addWall(w.getStartVector().x, w.getStartVector().y,
                                w.getEndVector().x, w.getEndVector().y, i);
                    }
                }
        }

        boolean foundPlace = false;
        for (Person p : world.getPeople()) {
            foundPlace = false;
            for (LayoutChunk lc : chunks) {
                if (lc.isPointInside(p.getLocation().x, p.getLocation().y)) {
                    lc.addPerson(p);
                    foundPlace = true;
                    break;
                }
            }
            if (!foundPlace) {
                System.out.println(p.getLocation().x);
                System.out.println(p.getLocation().y);
                throw new Exception("This shit is fucked");
            }
        }

        ArrayList<Thread> threads = new ArrayList<Thread>();

        for (LayoutChunk lc : chunks) {
            Runnable thisTask = (Runnable) lc;
            Thread worker = new Thread(thisTask);
            threads.add(worker);
        }

        long startTime = System.currentTimeMillis();

        for (Thread thread : threads) {
            thread.start();
        }

        while (true) {
            int alive = 0;
            for (Thread t : threads) {
                if (t.isAlive()) {
                    alive++;
                }
            }
            if (alive != NUM_THREADS) {
                System.err.println("Thread died");
                break;
            }
            Thread.sleep(10);
        }

        for (Thread t : threads) {
            t.join();
        }
        System.out.println("All threads have finished.");
        int evacTook = chunkSyn.getStopped();
        System.out.println("Evacuation took:" + evacTook / 10.0 + " seconds.");

        double endTime = System.currentTimeMillis();

        System.out.println("The simulation took " + (endTime - startTime));

        ArrayList<Person> people = world.getPeople();

        System.out.println("Printing persons starting location");

        for (Person p : people) {
            System.out.println("Floor: " + p.floors.get(0) + " " + p.locations.get(0));
        }


        System.out.println("Time taken before file I/O: " + (System.currentTimeMillis() - d));

        ArrayList<Person> output = new ArrayList<Person>();

        for (LayoutChunk lc : chunks) {
            output.addAll(lc.getPeople());
        }
        // output.addAll(world.getPeople());

        Point3d[][] locations = new Point3d[output.size()][TIME_STEPS + 1];
        // loop through people
        for (int i = 0; i < output.size(); i++) {
            Person p = output.get(i);
            for (int j = 0; j < p.locations.size(); j++) {
                try {
                    if (p.locations.get(j) == null) {
                        break;
                    }
                    locations[i][j] = new Point3d(p.locations.get(j).x, p.locations.get(j).y, p.floors.get(j));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Boolean[][] stuckStatus = new Boolean[output.size()][output.get(0).locations.size() + 1];
        for (int i = 0; i < output.size(); i++) {
            Person p = output.get(i);
            stuckStatus[i] = p.blockedList.toArray(new Boolean[p.blockedList.size()]);
        }
//
//        /* TOTAL DENSITY MAP */
//        int[][][][] totalDensityMaps = new int[TIME_STEPS][world.sideLength][world.sideLength][numFloors];
//        for ( LayoutChunk c : chunks ) {
//        	int[][][][] densityMaps = c.getAllDensityMaps();
//        	for (int i=0; i < TIME_STEPS; i++) {
//        		for (int j=0; j < world.getSideLength(); j++) {
//        			for (int k=0; k < world.getSideLength(); k++) {
//        				for (int l=0; l < numFloors; l++) {
//                    		if ( densityMaps[i][j][k][l] != 0 )
//                    			totalDensityMaps[i][j][k][l] = densityMaps[i][j][k][l];
//        				}
//                	}
//            	}
//        	}
//        }

        try {
            FileWriter writer = new FileWriter("www/console.txt");
            writer.write("Evacuation took: " + evacTook / 10.0 + " seconds.");
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        toJson(stuckStatus, "www/stuck.json");
        toJson(locations, "www/people.json");
        // toJson(totalDensityMaps, "www/densities.json");
        toJson(world.getStaticDensityMap(), "www/bottlenecks.json");
        System.out.println("I'm done");
        System.out.println("Total time taken: " + (System.currentTimeMillis() - d));

        if (interactive) {
            server.join();
        }

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
            for (String s : conf.keySet()) {
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

    public HashMap getConfig() {
        return conf;
    }

}

