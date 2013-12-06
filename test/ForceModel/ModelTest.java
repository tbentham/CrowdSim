package ForceModel;

import Dijkstra.Vertex;
import Exceptions.NaNException;
import Exceptions.PersonOverlapException;
import ForceModel.Model;
import WorldRepresentation.Person;
import WorldRepresentation.Wall;
import org.junit.Before;
import org.junit.Test;

import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.Assert.assertTrue;

public class ModelTest {

    Model forceModel;
    ArrayList<Person> testPeople;

    @Before
    public void setUp() {
        forceModel = new Model();
        testPeople = new ArrayList<>();
        testPeople.add(new Person(0.01, 0));
        testPeople.add(new Person(0.01, 0.01));
        testPeople.add(new Person(0.1, 0.1));
        testPeople.add(new Person(1, 1));
        testPeople.add(new Person(3, 4));
        testPeople.add(new Person(5, 5));
        testPeople.add(new Person(100, 100));
        testPeople.add(new Person(1000, 1000));
    }

    @Test
    public void bTestShouldNotExceptionWithTheseValues() throws NaNException, PersonOverlapException {
        Person p1 = new Person(7.420065505692629, 7.420065505692629);
        Person p2 = new Person(7.132320990663707, 7.132320990663707);
        p1.setActualVelocity(new Vector2d(0.6631487427802485, 0.6631487427802485));
        p2.setActualVelocity(new Vector2d(1.4277746470546464, 1.4277746470546464));
        LinkedList<Vertex> goals = new LinkedList<>();
        goals.add(new Vertex(18.0, 18.0));
        p2.setGoalList(goals);
        assertTrue(!Double.isNaN(forceModel.b(p1, p2, 0.5)));
    }

    @Test
    public void socialForcesDecreaseWithDistance() throws Exception {
        Person p = new Person(0, 0);
        ArrayList<Double> forceMagnitudes = new ArrayList<>();
        for (Person testPerson : testPeople) {
            forceMagnitudes.add(forceModel.socialForce(p, testPerson, 0.1).length());
        }
        for (int i = 0; i < testPeople.size() - 1; i++) {
            assertTrue(forceMagnitudes.get(i) >= forceMagnitudes.get(i + 1));
        }
    }

    @Test(expected = NaNException.class)
    public void personOneInNaNLocationThrowsNaNException() throws Exception {
        Person p = new Person(0, 0);
        p.setLocation(Double.NaN, Double.NaN);
        Person p2 = new Person(0, 0);
        forceModel.socialForce(p, p2, 0.1);
    }

    @Test(expected = NaNException.class)
    public void personTwoInNaNLocationThrowsNaNException() throws Exception {
        Person p = new Person(0, 0);
        p.setLocation(Double.NaN, Double.NaN);
        Person p2 = new Person(0, 0);
        forceModel.socialForce(p2, p, 0.1);
    }

    @Test(expected = PersonOverlapException.class)
    public void socialForceThrowsExceptionIfPeopleOverlap() throws Exception {
        Person p = new Person(0, 0);
        Person p2 = new Person(0, 0);
        forceModel.socialForce(p, p2, 0.1);
    }

    @Test
    public void socialForcesAreEqualAndOpposite() throws Exception {
        Person p = new Person(0, 0);
        ArrayList<Vector2d> forceVectors = new ArrayList<>();
        for (Person testPerson : testPeople) {
            forceVectors.add(forceModel.socialForce(p, testPerson, 0.1));
        }

        ArrayList<Vector2d> reverseForceVectors = new ArrayList<>();
        for (Person testPerson : testPeople) {
            reverseForceVectors.add(forceModel.socialForce(testPerson, p, 0.1));
        }

        for (int i = 0; i < forceVectors.size(); i++) {
            assertTrue(forceVectors.get(i).x == -reverseForceVectors.get(i).x);
            assertTrue(forceVectors.get(i).y == -reverseForceVectors.get(i).y);

        }
    }

    @Test
    public void socialForceShouldConsiderTimeStep() throws Exception {
        Person p = new Person(5, 5);
        Person p2 = new Person(10, 10);
        Vector2d socialForceUsingOneTimeStep = forceModel.socialForce(p, p2, 1);
        Vector2d socialForceUsingFiveTimeStep = forceModel.socialForce(p, p2, 5);
        assertTrue(socialForceUsingOneTimeStep.length() != socialForceUsingFiveTimeStep.length());
    }

    @Test(expected = NaNException.class)
    public void obstacleAvoidanceShouldThrowExceptionIfGivenNaNLocation() throws Exception {
        Person p = new Person(0, 0);
        p.setLocation(Double.NaN, Double.NaN);
        Wall w = new Wall(0, 0, 1, 1);
        forceModel.obstacleAvoidance(p, w);
    }

    @Test
    public void w() throws Exception {
        Person p = new Person(0, 0);
        Wall w = new Wall(2, 2, 5, 5);
        System.out.println(forceModel.obstacleAvoidance(p, w));
    }

}
