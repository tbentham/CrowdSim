package WorldRepresentation;

import Exceptions.RoutesNotComputedException;
import Exceptions.WallOverlapException;
import Exceptions.WorldNotSetUpException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.vecmath.Point3d;
import java.util.ArrayList;

public class WorldTest {

    private World world;

    @Before
    public void setUp() {
        world = new World(100, 1);
        world.setUp();
    }

    @After
    public void tearDown() {
        world = null;
    }

    @Test
    public void addNewPersonAtTest() throws Exception {
        ArrayList<Point3d> goals = new ArrayList<>();
        goals.add(new Point3d(1, 1, 0));
        ArrayList<Point3d> poi = new ArrayList<>();
        poi.add(new Point3d(10, 10, 0));
        world.computeDijsktraTowards(goals, poi);
        world.addNewPersonAt(0, 0, 0, 0, false);
    }

    @Test
    public void printFloorPlanTest() throws Exception {
        world.printFloorPlan();
    }

    @Test
    public void getStaticDensityMapTest() throws Exception {
        ArrayList<Point3d> goals = new ArrayList<>();
        goals.add(new Point3d(1, 1, 0));
        ArrayList<Point3d> poi = new ArrayList<>();
        poi.add(new Point3d(10, 10, 0));
        poi.add(new Point3d(5, 5, 0));
        world.computeDijsktraTowards(goals, poi);
        world.getStaticDensityMap();
    }

    @Test
    public void setEvacTest() {
        world.setEvac(new Point3d(0, 0, 0));
    }

    @Test(expected = WallOverlapException.class)
    public void wallOverlapExceptionTest() throws Exception {
        world.addWall(0, 0, 10, 10, 0);
        world.setUp();
        world.computeDijsktraTowards(new ArrayList<Point3d>(), new ArrayList<Point3d>());
        world.addNewPersonAt(5, 5, 0, 0, false);
    }

    @Test(expected = RoutesNotComputedException.class)
    public void routesNotComputedExceptionTest() throws Exception {
        world.addNewPersonAt(5, 5, 0, 0, false);
    }

    @Test(expected = WorldNotSetUpException.class)
    public void worldNotSetUpExceptionTest() throws Exception {
        world = new World(100, 1);
        world.printFloorPlan();
    }

}
