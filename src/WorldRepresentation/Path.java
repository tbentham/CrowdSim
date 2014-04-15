package WorldRepresentation;

import NewDijkstra.Node;

import java.util.LinkedList;
import java.util.List;

// Represents a path for a person as a list of vertices
public class Path {
    private LinkedList<Node> vertices;
    private LinkedList<Node> subgoals;

    public Path(List<Node> nodeList) {
        vertices = new LinkedList<Node>();
        subgoals = new LinkedList<Node>();
        vertices.addAll(nodeList);
        generateSubGoals();
    }

    public LinkedList<Node> getNodes() {
        return vertices;
    }

    public LinkedList<Node> getSubGoals() {
        return subgoals;
    }

    // Creates a subgoal list which is the subset of vertices at which the path changes direction
    private void generateSubGoals() {
        if (vertices.size() > 2) {
            Node lastVertex = vertices.get(0);
            Node curVertex = vertices.get(1);

            double curDiffX = lastVertex.getX() - curVertex.getX();
            double curDiffY = lastVertex.getY() - curVertex.getY();

            for (int i = 2; i < vertices.size(); i++) {
                // current differences are now last differences
                double lastDiffX = curDiffX; // last difference in x-coord value
                double lastDiffY = curDiffY; // last difference in y-coord value

                lastVertex = curVertex;
                curVertex = vertices.get(i);

                // calculate current differences
                curDiffX = lastVertex.getX() - curVertex.getX();
                curDiffY = lastVertex.getY() - curVertex.getY();

                // if either has changed, add a subgoal
                if (curDiffX != lastDiffX || curDiffY != lastDiffY) {
                    subgoals.add(lastVertex);
                }
            }
        }
        if (vertices.size() >= 1) {
            subgoals.add(vertices.get(vertices.size() - 1));
        } else {
            System.err.println("No vertices in list to generate sub-goals from.");
        }
    }
}
