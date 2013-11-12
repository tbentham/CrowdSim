import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

import Dijkstra.Vertex;
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

        World world = new World(120);

        for (Cobject co : cobjs) {
            world.addWall(co.getFrom().x / 10.0, co.getFrom().y / 10.0, co.getTo().x / 10.0, co.getTo().y / 10.0);
        }

        world.setUp();
        world.printFloorPlan();
        world.computeDijsktraTowards(20, 20);

        Person p1 = new Person(50, 50);
        for (Vertex v : world.getPath(50, 50).getVertices()) {
            System.out.println(v);
        }

        System.out.println("--");

        for (Vertex v : world.getPath(5, 5).getSubGoals()) {
            System.out.println(v);
        }

        p1.setGoalList(world.getPath(5, 5).getSubGoals());

        Person p3 = new Person(35, 45);
        p3.setGoalList(world.getPath(35, 45).getSubGoals());

        Person p2 = new Person(30, 45);
        p2.setGoalList(world.getPath(30, 45).getSubGoals());

        ArrayList<Person> people = new ArrayList<Person>();
//		people.add(p1);
        people.add(p2);
//		people.add(p3);

        System.out.println("--");

        for (Person p : people) {
            System.out.println(p.getLocation());
        }

        System.out.println("--");

        for (int i = 0; i < 30; i++) {
            for (Person p : people)
                p.advance(people, 0.1);
            if (i % 10 == 9) {
                System.out.println();
                System.out.println("Step " + (i + 1) + " (Simulated time: " + (i + 1) * 0.1 + "s)");
                for (Person p : people)
                    System.out.println(p.getLocation());
            }
        }

        System.out.println("--");

//        for (int i = 0; i < 10; i++) {
//            for (Person p : people) {
//                System.out.println(p.locations.get(i));
//            }
//        }

        PrintWriter out = new PrintWriter("www/people.json");
        out.print(peopleToJson(people));
        out.close();

        //        System.out.println(p1.desiredAcceleration());
        //	    for (int i = 0; i < 15; i++) {
        //			B====D
        //            System.out.println(p1.advance());
        //        }

        System.out.println("Executed in: " + (System.currentTimeMillis() - d) + "ms");
        server.join();
    }

    public static String peopleToJson(ArrayList<Person> people) {

//        for (Person p: people) {
//            System.out.println(p.getLocation());
//        }

        String[] locations = new String[people.size()];
        Person curPerson;
        String finalString = "";
        for (int i = 0; i < people.size(); i++) {
            curPerson = people.get(i);
            locations[i] = "";
            for (int j = 1; j < curPerson.locations.size(); j++) {
                locations[i] += "{\"x\":" + (int) (curPerson.locations.get(j).x) * 10 + ", \"y\":" + (int) (curPerson.locations.get(j).y * 10) + "}";
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

