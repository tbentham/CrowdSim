package WorldRepresentation;

import Dijkstra.Vertex;
import org.junit.Before;
import org.junit.Test;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.LinkedList;

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
    public void pathAlgorithmTimeProfilingTest() throws Exception {
        long startTime = System.currentTimeMillis();

        ArrayList<Integer> sizesToTest = new ArrayList<>();
        sizesToTest.add(5);
        sizesToTest.add(10);
        sizesToTest.add(25);
        sizesToTest.add(50);
//        sizesToTest.add(100);
//        sizesToTest.add(250);

        ArrayList<Long> endTimes = new ArrayList<>();
        ArrayList<Integer> numbersOfEdges = new ArrayList<>();

        for (Integer sz : sizesToTest) {
            World w = new World(sz);
            w.setUp();
            w.computeDijsktraTowards(0, 0);
            Path p = w.getPath(3, 3);
            LinkedList<Vertex> vertices = p.getVertices();
            // Attempts to explicitly avoid any compiler lazy optimisations
            for (Vertex v : vertices) {
                v.getX();
                v.getY();
            }
            endTimes.add(System.currentTimeMillis());
            numbersOfEdges.add(w.getEdges().size());
        }

        for (int i = 0; i < sizesToTest.size(); i++) {
            Integer sz = sizesToTest.get(i);
            Long timeDiff = endTimes.get(i) - startTime;
            System.out.format("%dx%d (%d) nodes and %d edges took %dms to compute paths\n",
                    sz, sz, sz * sz, numbersOfEdges.get(i), timeDiff);
        }
    }
}
