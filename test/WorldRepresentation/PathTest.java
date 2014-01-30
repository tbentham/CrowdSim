package WorldRepresentation;

import Dijkstra.Vertex;
import NewDijkstra.Node;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PathTest {

    @Test
    public void baseCases() {
        LinkedList<Node> vertices = new LinkedList<>();
        vertices.add(new Node(0, 0));
        vertices.add(new Node(1, 0));

        LinkedList<Vertex> exSubGoals = new LinkedList<>();
        exSubGoals.add(new Vertex(1, 0));


        Path path = new Path(vertices);

        // check if subgoals are correctly generated in the case of 2 items in vertices
        if (path.getSubGoals().size() == exSubGoals.size()) {
            for (int i = 0; i < path.getSubGoals().size(); i++) {
                assertTrue(path.getSubGoals().get(i).equals(exSubGoals.get(i)));
            }
        } else {
            fail("Sub-goal path lists not even same size");
        }

        vertices.clear();
        vertices.add(new Node(0, 0));
        path = new Path(vertices);

        assertTrue(path.getSubGoals().size() == 0); // check if subgoals list is empty (not enough vertices to generate a subgoal)
    }

    @Test
    public void pathConstructorShouldNotReverseGivenVertices() {
        LinkedList<Node> vertices = new LinkedList<>();
        vertices.add(new Node(0, 0));
        vertices.add(new Node(1, 0));
        Path path = new Path(vertices);
        assertTrue(path.getNodes().get(0).toPoint2d().x == 0.0);
        assertTrue(path.getNodes().get(0).toPoint2d().y == 0.0);
        assertTrue(path.getNodes().get(1).toPoint2d().x == 1.0);
        assertTrue(path.getNodes().get(1).toPoint2d().y == 0.0);
    }

}

