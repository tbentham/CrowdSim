import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.vecmath.Point2d;
import java.io.IOException;

import Dijkstra.Vertex;
import WorldRepresentation.Person;
import WorldRepresentation.World;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.*;

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

        World world = new World(10);

        for (Cobject co : cobjs) {
            world.addWall(co.getFrom().x / 10.0, co.getFrom().y / 10.0, co.getTo().x / 10.0, co.getTo().y / 10.0);
        }

        Point2d goal = new Point2d(2, 2);

        world.setUp();
        world.printFloorPlan();
        world.computeDijsktraTowards((int) Math.round(goal.x), (int) Math.round(goal.y));

        world.printDijsktras();


        System.out.println("Dijsktra's Executed in: " + (System.currentTimeMillis() - d)
                + "ms Towards " + goal.x + ", " + goal.y);


        Person p2 = new Person(30.0, 45.0);
        p2.setGoalList(world.getPath((int) Math.round(p2.getLocation().x), (int) Math.round(p2.getLocation().y)).getSubGoals());

        System.out.println("Printing vertices towards " + p2.getLocation().x + " " + p2.getLocation().y);

        for (Vertex v : world.getPath((int) Math.round(p2.getLocation().x), (int) Math.round(p2.getLocation().y)).getVertices()) {
            System.out.println(v);
        }

        System.out.println("Printing subgoals towards " + p2.getLocation().x + " " + p2.getLocation().y);

        for (Vertex v : world.getPath((int) Math.round(p2.getLocation().x), (int) Math.round(p2.getLocation().y)).getSubGoals()) {
            System.out.println(v);
        }

        ArrayList<Person> people = new ArrayList<Person>();
        people.add(p2);

        System.out.println("Printing persons starting location");

        for (Person p : people) {
            System.out.println(p.getLocation());
        }

        for (int i = 0; i < 10; i++) {
            for (Person p : people)
                p.advance(people, 1);
            if (i % 10 == 9) {
                System.out.println();
                System.out.println("Step " + (i + 1) + " (Simulated time: " + (i + 1) * 1    + "s)");
            }
        }

        System.out.println("Printing persons position for each advance");


        for (int i = 0; i < 30; i++) {
            for (Person p : people)
                System.out.println(p.locations.get(i));
        }

        PrintWriter out = new PrintWriter("www/people.json");
        out.print(peopleToJson(people));
        out.close();


        server.join();
    }

    public static String peopleToJson(ArrayList<Person> people) {

//        for (WorldRepresentation.Person p: people) {
//            System.out.println(p.getLocation());
//        }

        String[] locations = new String[people.size()];
        Person curPerson;
        String finalString = "";
        for (int i = 0; i < people.size(); i++) {
            curPerson = people.get(i);
            locations[i] = "";
            for (int j = 1; j < curPerson.locations.size(); j++) {
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

        System.out.println(finalString);

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

