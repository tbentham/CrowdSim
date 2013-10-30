import Dijkstra.Vertex;

import java.util.LinkedList;
import java.util.List;

public class Path {
    private LinkedList<Vertex> vertices = new LinkedList<Vertex>();
    private LinkedList<Vertex> subgoals = new LinkedList<Vertex>();

    public Path(List<Vertex> vertexList) {
        vertices.addAll(vertexList);
        generateSubGoals();
    }

    public LinkedList<Vertex> getPath() {
        return vertices;
    }

    private void generateSubGoals() {
        Vertex lastVertex = null;
        Vertex curVertex = null;
        for (Vertex v: vertices) {

        }
    }
}
