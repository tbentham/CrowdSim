import Dijkstra.Vertex;
import WorldRepresentation.Path;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.LinkedList;

public class PathTest {

    @Test
    public void baseCases() {
        LinkedList<Vertex> vertices = new LinkedList<Vertex>();
        // added in wrong order as they get reversed when initialising WorldRepresentation.Path
        vertices.add(new Vertex(1, 0));
        vertices.add(new Vertex(0, 0));

        LinkedList<Vertex> exSubGoals = new LinkedList<Vertex>();
        exSubGoals.add(new Vertex(1, 0));


        Path path = new Path(vertices);

        // check if subgoals are correctly generated in the case of 2 items in vertices
        if (path.getSubGoals().size() == exSubGoals.size()) {
            for (int i=0; i<path.getSubGoals().size(); i++) {
                assertTrue(path.getSubGoals().get(i).equals(exSubGoals.get(i)));
            }
        } else {
            fail("Sub-goal path lists not even same size");
        }

        vertices.clear();
        vertices.add(new Vertex(0, 0));
        path = new Path(vertices);

        assertTrue(path.getSubGoals().size() == 0); // check if subgoals list is empty (not enough vertices to generate a subgoal)
    }
}
