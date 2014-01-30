package NewDijkstra;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph {

    HashMap<Node, ArrayList<Connection>> connections;

    public Graph() {
        connections = new HashMap<Node, ArrayList<Connection>>();
    }

    public ArrayList<Connection> getConnections(Node fromNode) {
        return connections.get(fromNode);
    }



}
