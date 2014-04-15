package WorldRepresentation;

import NewDijkstra.aConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

public class LayoutChunkTest {

    private LayoutChunk layoutChunk;
    private World world;

    @Before
    public void setUp() throws Exception {
        world = new World(100, 1);
        world.getPeople().add(mock(Person.class));
        world.setUp();
        layoutChunk = new LayoutChunk(0, 100, 0, 100, mock(CyclicBarrier.class), 100, world, 0, 1, 5, 1);
    }

    @After
    public void tearDown() {
        layoutChunk = null;
    }

    @Test
    public void addAndGetPersonTest() {
        layoutChunk.addPerson(mock(Person.class));
        assertTrue(layoutChunk.getPeople().getClass() == ArrayList.class);
    }

    @Test
    public void addWallTest() {
        layoutChunk.addWall(0, 0, 1, 1, 0);
    }

    @Test
    public void getAllDensityMapsTest() {
        layoutChunk.getAllDensityMaps();
    }

    @Test
    public void isPointInsideReturnsTrueWithValidPoint() {
        assertTrue(layoutChunk.isPointInside(50, 50));
    }

    @Test
    public void isPointInsideReturnsFalseWithInvalidPoint() {
        assertFalse(layoutChunk.isPointInside(101, 101));
    }

    @Test
    public void intersectsTopReturnsTrueWithIntersectingWall() {
        assertTrue(layoutChunk.intersectsTop(new Wall(5, 5, -5, -5)));
    }

    @Test
    public void intersectsTopReturnsFalseWithNonIntersectingWall() {
        assertFalse(layoutChunk.intersectsTop(new Wall(5, 5, 10, 10)));
    }

    @Test
    public void intersectsBottomReturnsTrueWithIntersectingCall() {
        assertTrue(layoutChunk.intersectsBottom(new Wall(95, 95, 105, 105)));
    }

    @Test
    public void intersectsBottomReturnsFalseWithNonIntersectingCall() {
        assertFalse(layoutChunk.intersectsBottom(new Wall(95, 95, 85, 85)));
    }

    @Test
    public void numberOfIntersectsReturnsTwoWithLineAcrossChunk() {
        assertTrue(layoutChunk.numberOfIntersects(new Wall(-10, -10, 110, 110)) == 2);
    }

    @Test
    public void addChunksTest() {
        layoutChunk.addChunks(new LayoutChunk[1]);
    }

    @Test
    public void putPersonTest() {
        layoutChunk.putPerson(mock(Person.class));
    }

    @Test
    public void addPeopleTest() {
        layoutChunk.addPeople();
        assertTrue(layoutChunk.q.isEmpty());
    }

    @Test
    public void peopleTopEdgeTest() {
        ArrayList<Person> topEdgeArray = layoutChunk.peopleTopEdge();
        assertTrue(topEdgeArray.isEmpty());
    }

    @Test
    public void peopleBottomEdgeTest() {
        ArrayList<Person> bottomEdgeArray = layoutChunk.peopleBottomEdge();
        assertTrue(bottomEdgeArray.isEmpty());
    }

    @Test
    public void chunkSizeTest() {
        layoutChunk.chunks = new LayoutChunk[]{layoutChunk};
        assertTrue(layoutChunk.chunkSize() == 100);
    }

    @Test
    public void sendTopOverlapTest() {
        layoutChunk.chunks = new LayoutChunk[]{layoutChunk};
        layoutChunk.sendTopOverlap();
    }

    @Test
    public void sendBottomOverlapTest() {
        layoutChunk.chunks = new LayoutChunk[]{layoutChunk};
        layoutChunk.sendBottomOverlap();
    }

    @Test
    public void addOverLapPeopleTest() {
        layoutChunk.addOverlapPeople();
    }

    @Test
    public void addOverlapTest() {
        layoutChunk.addOverlapPeople();
    }

    @Test
    public void sendOverlapsTest() {
        layoutChunk.chunks = new LayoutChunk[]{layoutChunk};
        layoutChunk.sendOverlaps();
    }

    @Test
    public void addDensityMapTest() {
        layoutChunk.addDensityMap(new int[1][1][1]);
    }

    @Test
    public void getAllPeopleTest() {
        assertTrue(layoutChunk.getAllPeople().isEmpty());
    }

    @Test
    public void validXYLocationWithNoWallsTest() {
        int[] validLocation = layoutChunk.validXYLocation(new Person(5, 5, 0, 0));
        assertTrue(validLocation[0] == 5);
        assertTrue(validLocation[1] == 5);
    }

    @Test
    public void validXYLocationWithWallTest() {
        World world = new World(100, 1);
        world.addWall(0, 0, 1, 1, 0);
        world.getPeople().add(mock(Person.class));
        layoutChunk = new LayoutChunk(0, 100, 0, 100, mock(CyclicBarrier.class), 100, world, 0, 1, 5, 1);
        int[] validLocation = layoutChunk.validXYLocation(new Person(1, 1, 0, 0));
        ArrayList<aConnection> aConn = layoutChunk.getChunkStar().getConnections().get(
                0 + validLocation[0] * 100 + validLocation[1]);
        assertNotNull(aConn);
    }

    @Test
    public void updatePersonWithEvacPathTest() throws Exception {
        World world = new World(100, 1);
        world.getPeople().add(mock(Person.class));
        world.setUp();
        ArrayList<Point3d> goals = new ArrayList<Point3d>();
        goals.add(new Point3d(0, 0, 0));
        goals.add(new Point3d(1, 1, 0));
        ArrayList<Point3d> poi = new ArrayList<Point3d>();
        poi.add(new Point3d(10, 10, 0));
        poi.add(new Point3d(0, 0, 0));
        world.computeDijsktraTowards(goals, poi);
        layoutChunk = new LayoutChunk(0, 100, 0, 100, mock(CyclicBarrier.class),
                100, world, 0, 1, 5, 1);
        layoutChunk.updatePersonWithEvacPath(new Person(5, 5, 0, 0));
    }

    @Test
    public void isStuckTest() {
        Person p = new Person(0, 0, 0, 0);
        layoutChunk.setAStar(0);
        assertFalse(layoutChunk.isStuck(p, 1));
        layoutChunk.setAStar(1);
        assertFalse(layoutChunk.isStuck(p, 1));
        layoutChunk.setEvacTime(100);
        assertFalse(layoutChunk.isStuck(p, 1));
    }

    @Test
    public void waitBarrierTest() {
        layoutChunk.waitBarrier();
    }

    @Test
    public void threadIDTest() {
        layoutChunk.chunks = new LayoutChunk[]{layoutChunk};
        assertTrue(layoutChunk.threadID() == 1);
    }

    @Test
    public void putPersonInCorrespondingChunksListTest() {
        layoutChunk.chunks = new LayoutChunk[]{layoutChunk, layoutChunk};
        layoutChunk.putPersonInCorrespondingChunksList(new Person(0, 0, 0, 0),
                new ArrayList<Person>());
    }

    @Test
    public void aStarTest() throws Exception {
        ArrayList<Point3d> goals = new ArrayList<Point3d>();
        goals.add(new Point3d(0, 0, 0));
        goals.add(new Point3d(1, 1, 0));
        ArrayList<Point3d> poi = new ArrayList<Point3d>();
        poi.add(new Point3d(10, 10, 0));
        poi.add(new Point3d(0, 0, 0));
        world.computeDijsktraTowards(goals, poi);
        Person p = new Person(5, 5, 0, 0);
        p.setGoalList(world.getPath(5, 5, 0, 0, false).getSubGoals());
        layoutChunk.aStar(p);
    }

    @Test
    public void populateDensityMapTest() {
        layoutChunk.addPerson(new Person(0, 0, 0, 0));
        layoutChunk.populateDensityMap();
    }

    @Test
    public void runTest() throws Exception {
        ArrayList<Point3d> goals = new ArrayList<Point3d>();
        goals.add(new Point3d(0, 0, 0));
        goals.add(new Point3d(1, 1, 0));
        ArrayList<Point3d> poi = new ArrayList<Point3d>();
        poi.add(new Point3d(10, 10, 0));
        poi.add(new Point3d(0, 0, 0));
        world.computeDijsktraTowards(goals, poi);
        layoutChunk.chunks = new LayoutChunk[]{layoutChunk, layoutChunk};
        layoutChunk.setSteps(1);
        layoutChunk.setEvacTime(10);
        layoutChunk.addPerson(new Person(5, 5, 0, 0));
        layoutChunk.run();
    }

}