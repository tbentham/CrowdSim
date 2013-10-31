import Dijkstra.Vertex;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

public class PathTest {

    @Test
    public void baseCases() {
        LinkedList<Vertex> vertices = new LinkedList<Vertex>();
        vertices.add(new Vertex(0, 0));
        vertices.add(new Vertex(1, 0));
        LinkedList<Vertex> exSubGoals = new LinkedList<Vertex>();
        exSubGoals.add(new Vertex(1, 0));


        Path path = new Path(vertices);

        assertTrue(path.getSubGoals().equals(exSubGoals));  // check if subgoals are correctly generated in the case of 2 items in vertices

        vertices.clear();
        vertices.add(new Vertex(0, 0));
        path = new Path(vertices);

        assertTrue(path.getSubGoals().size() == 0); // check if subgoals list is empty (not enough vertices to generate a subgoal)
    }
}
