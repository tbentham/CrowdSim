package ForceModel;

import Exceptions.NaNException;
import Exceptions.PersonOverlapException;
import WorldRepresentation.Person;
import WorldRepresentation.Wall;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.vecmath.Vector2d;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class ModelTest {

    private Model forceModel;
    private ArrayList<Person> testPeople;
    private ArrayList<Wall> testWalls;
    private Person p;
    private Person p2;

    @Before
    public void setUp() {
        forceModel = new Model();
        testPeople = new ArrayList<Person>();
        testPeople.add(new Person(0.01, 0, 0, 0));
        testPeople.add(new Person(0.01, 0.01, 0, 0));
        testPeople.add(new Person(0.1, 0.1, 0, 0));
        testPeople.add(new Person(1, 1, 0, 0));
        testPeople.add(new Person(3, 4, 0, 0));
        testPeople.add(new Person(5, 5, 0, 0));
        testPeople.add(new Person(100, 100, 0, 0));
        testPeople.add(new Person(1000, 1000, 0, 0));
        for (Person p : testPeople) {
            p.setSize(0.5);
            p.setDesiredSpeed(1.08);
        }
        p = new Person(0, 0, 0, 0);
        p.setSize(0.5);
        p.setDesiredSpeed(1.08);
        p2 = new Person(0, 0, 0, 0);
        p2.setSize(0.5);
        p2.setDesiredSpeed(1.08);
        testWalls = new ArrayList<Wall>();
        testWalls.add(new Wall(1, 0, 1, 5));
        testWalls.add(new Wall(2, 0, 2, 5));
        testWalls.add(new Wall(3, 0, 3, 10));
        testWalls.add(new Wall(4, 0, 4, 10));
        testWalls.add(new Wall(10, 0, 10, 10));
    }

    @After
    public void tearDown() {
        forceModel = null;
        testPeople = null;
        p = null;
        p2 = null;
    }

    @Test
    public void socialForcesDecreaseWithDistance() throws Exception {
        ArrayList<Double> forceMagnitudes = new ArrayList<Double>();
        for (Person testPerson : testPeople) {
            forceMagnitudes.add(forceModel.socialForce(p, testPerson).length());
        }
        for (int i = 0; i < testPeople.size() - 1; i++) {
            assertTrue(forceMagnitudes.get(i) >= forceMagnitudes.get(i + 1));
        }
    }

    @Test
    public void obstacleAvoidanceForcesDecreaseWithDistance() throws Exception {
        ArrayList<Double> forceMagnitudes = new ArrayList<Double>();
        for (Wall testWall : testWalls) {
            forceMagnitudes.add(forceModel.obstacleAvoidance(p, testWall).length());
        }
        for (int i = 0; i < testWalls.size() - 1; i++) {
            assertTrue(forceMagnitudes.get(i) > forceMagnitudes.get(i + 1));
        }
    }

    @Test(expected = NaNException.class)
    public void personOneInNaNLocationThrowsNaNException() throws Exception {
        p.setLocation(Double.NaN, Double.NaN);
        forceModel.socialForce(p, p2);
    }

    @Test(expected = NaNException.class)
    public void personTwoInNaNLocationThrowsNaNException() throws Exception {
        p.setLocation(Double.NaN, Double.NaN);
        forceModel.socialForce(p2, p);
    }

    @Test(expected = PersonOverlapException.class)
    public void socialForceThrowsExceptionIfPeopleOverlap() throws Exception {
        forceModel.socialForce(p, p2);
    }

    @Test
    public void socialForcesAreEqualAndOpposite() throws Exception {
        ArrayList<Vector2d> forceVectors = new ArrayList<Vector2d>();
        for (Person testPerson : testPeople) {
            forceVectors.add(forceModel.socialForce(p, testPerson));
        }

        ArrayList<Vector2d> reverseForceVectors = new ArrayList<Vector2d>();
        for (Person testPerson : testPeople) {
            reverseForceVectors.add(forceModel.socialForce(testPerson, p));
        }

        for (int i = 0; i < forceVectors.size(); i++) {
            assertTrue(forceVectors.get(i).x == -reverseForceVectors.get(i).x);
            assertTrue(forceVectors.get(i).y == -reverseForceVectors.get(i).y);

        }
    }

    @Test(expected = NaNException.class)
    public void obstacleAvoidanceShouldThrowExceptionIfGivenNaNLocation() throws Exception {
        p.setLocation(Double.NaN, Double.NaN);
        Wall w = new Wall(0, 0, 1, 1);
        forceModel.obstacleAvoidance(p, w);
    }

}
