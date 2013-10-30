import Dijkstra.Vertex;

import java.util.LinkedList;
import java.util.List;

public class Path {
    private LinkedList<Vertex> vertices;
    private LinkedList<Vertex> subGoals;

    public Path(List<Vertex> vertexList) {
        vertices = new LinkedList<Vertex>();
        subGoals = new LinkedList<Vertex>();
        vertices.addAll(vertexList);
        generateSubGoals();
    }

    public LinkedList<Vertex> getVertices() {
        return vertices;
    }

    public LinkedList<Vertex> getSubGoals() {
        return subGoals;
    }

    private void generateSubGoals() {
        if (vertices.size() > 2) {
            Vertex lastVertex = vertices.get(0);
            Vertex curVertex = vertices.get(1);

            int lastDiffX;  // last difference in x-coord value
            int lastDiffY;  // last difference in y-coord value

            int curDiffX = lastVertex.getX() - curVertex.getX();
            int curDiffY = lastVertex.getY() - curVertex.getY();

            for (int i = 2; i < vertices.size(); i++) {
                // current differences are now last differences
                lastDiffX = curDiffX;
                lastDiffY = curDiffY;

                lastVertex = curVertex;
                curVertex = vertices.get(i);

                // calculate current differences
                curDiffX = lastVertex.getX() - curVertex.getX();
                curDiffY = lastVertex.getY() - curVertex.getY();

                //System.out.print("Iteration: "+i+". Considering: "+curVertex.toString()+"\t\tLast Difference: ("+lastDiffX+", "+lastDiffY+").\tCurrent Difference: ("+curDiffX+", "+curDiffY+").\t");

                // if either has changed, add a subgoal
                if (curDiffX != lastDiffX || curDiffY != lastDiffY) {
                    subGoals.add(lastVertex);
                    //System.out.print("Added: "+lastVertex.toString());
                }

                //System.out.println();
            }
        }if (vertices.size() > 1) {
            subGoals.add(vertices.get(vertices.size() - 1));
        } else {
            System.err.println("Not enough vertices in list to generate sub-goals from.");
        }
    }
}
