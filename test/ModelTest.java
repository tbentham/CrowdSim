import Dijkstra.Vertex;
import Exceptions.NaNException;
import Exceptions.PersonOverlapException;
import ForceModel.Model;
import WorldRepresentation.Person;
import org.junit.Before;
import org.junit.Test;

import javax.vecmath.Vector2d;
import java.util.LinkedList;

import static org.junit.Assert.assertTrue;

public class ModelTest {

    Model forceModel;

    @Before
    public void init() {
        forceModel = new Model();
    }

    @Test
    public void bTestShouldNotExceptionWithTheseValues() throws NaNException, PersonOverlapException {
        Person p1 = new Person(7.420065505692629, 7.420065505692629);
        Person p2 = new Person(7.132320990663707, 7.132320990663707);
        p1.setActualVelocity(new Vector2d(0.6631487427802485, 0.6631487427802485));
        p2.setActualVelocity(new Vector2d(1.4277746470546464, 1.4277746470546464));
        LinkedList<Vertex> goals = new LinkedList<Vertex>();
        goals.add(new Vertex(18.0, 18.0));
        p2.setGoalList(goals);
        assertTrue(!Double.isNaN(forceModel.b(p1, p2, 0.5)));
    }




}
