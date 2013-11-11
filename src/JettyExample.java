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

public class JettyExample extends AbstractHandler {
	
	private static String SUBMIT_PAGE = "<html><form action=\"/\" method=\"post\"><input type=\"text\" name=\"objects\"></input><input type=\"submit\"></form></html>";
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
    
    public static void main(String[] args) throws Exception {
        Server server = new Server(8881);
        
        ResourceHandler staticFiles = new ResourceHandler();
        
        //Create a handler for jetty to serve static files
        staticFiles.setDirectoriesListed(true);
        staticFiles.setWelcomeFiles(new String[]{"client.html"});
        staticFiles.setResourceBase("./www");
        
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{ staticFiles, new JettyExample()});
        server.setHandler(handlers);

        server.start();
        server.join();
    }
}
