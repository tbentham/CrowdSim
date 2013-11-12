import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

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
		 handlers.setHandlers(new Handler[]{ staticFiles, jettyHandle});
		 server.setHandler(handlers);
		
		 server.start();
		 Cobject[] cobjs = null; //CALL THE COPS
		 
//		 while(cobjs == null){
//			 cobjs = jettyHandle.getLatestObjs();
//			 Thread.sleep(1);
//		 }
//		 
		double d = System.currentTimeMillis();
		
		World world = new World(10);
		
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
		}
		
		for(int i = 0; i < 10; i++) {
		    for(Person p : people) {
		        System.out.println(p.locations.get(i));
		    }
		}
		
		for(Person p: people){
			System.out.println(p.locations.size());
		}
		
		//        System.out.println(p1.desiredAcceleration());
		//	    for (int i = 0; i < 15; i++) {
		//			B====D
		//            System.out.println(p1.advance());
		//        }
		
		System.out.println("Executed in: " + (System.currentTimeMillis() - d) + "ms");
		server.join();
    }
}


class JettyExample extends AbstractHandler {
	
	private JsonParser jparse;
	private Cobject[] objs;
	private boolean newObjs = false;
	
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
         
    	if(baseRequest.getMethod() == "POST" && baseRequest.getParameter("objects") != null){
        	System.out.println("Recieved " + baseRequest.getParameter("objects"));
        	
        	jparse = new JsonParser(baseRequest.getParameter("objects"));
        	try{
        		objs = jparse.parse();
        	}catch(Exception e){
        		System.out.println(e.getMessage());
        	}
        	

        	//Dump objects to screen for debugging
        	for(Cobject c : objs){
        		System.out.println(c.toString());
        	}
        	
        	newObjs = true;
        	
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
        	
        }
        
    }
 
    public Cobject[] getLatestObjs(){
    	
    	if(newObjs){
    		newObjs = false;
    		return objs;
    	}
    	else return null; // probably ought to be some exception in the name of java programming.
    }

}

