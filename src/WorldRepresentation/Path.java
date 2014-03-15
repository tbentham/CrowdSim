package WorldRepresentation;

import NewDijkstra.Node;

import java.util.LinkedList;
import java.util.List;

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

    private void generateSubGoals() {
        if (vertices.size() > 2) {
            Node lastVertex = vertices.get(0);
            Node curVertex = vertices.get(1);

            double lastDiffX;  // last difference in x-coord value
            double lastDiffY;  // last difference in y-coord value

            double curDiffX = lastVertex.x - curVertex.x;
            double curDiffY = lastVertex.y - curVertex.y;

            for (int i = 2; i < vertices.size(); i++) {
                // current differences are now last differences
                lastDiffX = curDiffX;
                lastDiffY = curDiffY;

                lastVertex = curVertex;
                curVertex = vertices.get(i);

                // calculate current differences
                curDiffX = lastVertex.x - curVertex.x;
                curDiffY = lastVertex.y - curVertex.y;

                //System.out.print("Iteration: "+i+". Considering: "+curVertex.toString()+"\t\tLast Difference: ("+lastDiffX+", "+lastDiffY+").\tCurrent Difference: ("+curDiffX+", "+curDiffY+").\t");

                // if either has changed, add a subgoal
                if (curDiffX != lastDiffX || curDiffY != lastDiffY) {
                    subgoals.add(lastVertex);
                    //System.out.print("Added: "+lastVertex.toString());
                }

                //System.out.println();
            }
        }
        if (vertices.size() > 1) {
            subgoals.add(vertices.get(vertices.size() - 1));
        } else {
            System.err.println("No vertices in list to generate sub-goals from.");
        }
    }

    public void printPath() {
        for (int i = 0; i < vertices.size(); i++) {
           System.out.println(vertices.get(i));
        }
    }
}
