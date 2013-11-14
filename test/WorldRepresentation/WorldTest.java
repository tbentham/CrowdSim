package WorldRepresentation;

import org.junit.Before;
import org.junit.Test;

import javax.vecmath.Point2d;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class WorldTest {

    World world;

    @Before
    public void setUp() {
        world = new World(100);
    }

    @Test
    public void testWorldConstructor() {
        assertTrue(world.getSideLength() == 100);
        assertNotNull(world.getWalls());
        assertTrue(world.getWalls().size() == 0);
        assertNotNull(world.getFloorPlan());
        assertTrue(world.getFloorPlan().length == 100);
        assertTrue(world.getNodeArray().length == 100);
        assertNotNull(world.getNodes());
        assertTrue(world.getNodes().size() == 0);
        assertNotNull(world.getEdges());
        assertTrue(world.getEdges().size() == 0);
        assertFalse(world.isSetUp());
        assertFalse(world.areRoutesComputed());
    }

    @Test
    public void addWallAddsWall() throws Exception {
        world.addWall(0, 0, 5, 5);
        assertTrue(world.getWalls().size() == 1);
        assertTrue(world.getWalls().get(0).getStartVector().x == 0);
        assertTrue(world.getWalls().get(0).getStartVector().y == 0);
        assertTrue(world.getWalls().get(0).getEndVector().x == 5);
        assertTrue(world.getWalls().get(0).getEndVector().y == 5);
    }

    @Test
    public void addWallAddsPointWall() throws Exception {
        world.addWall(new Point2d(0, 0), new Point2d(5, 5));
        assertTrue(world.getWalls().size() == 1);
        assertTrue(world.getWalls().get(0).getStartVector().x == 0);
        assertTrue(world.getWalls().get(0).getStartVector().y == 0);
        assertTrue(world.getWalls().get(0).getEndVector().x == 5);
        assertTrue(world.getWalls().get(0).getEndVector().y == 5);
    }

    @Test
    public void testSetUp() throws Exception {

    }

    @Test
    public void testPrintFloorPlan() throws Exception {

    }

    @Test
    public void testPrintDijsktras() throws Exception {

    }

    @Test
    public void testRound() throws Exception {

    }

    @Test
    public void testComputeDijsktraTowards() throws Exception {

    }

    @Test
    public void testGetPath() throws Exception {

    }

    @Test
    public void testGetSideLength() throws Exception {

    }

    @Test
    public void testGetWalls() throws Exception {

    }
}
