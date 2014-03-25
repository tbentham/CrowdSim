package WorldRepresentation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

public class LayoutChunkTest {

    private LayoutChunk layoutChunk;

    @Before
    public void setUp() throws Exception {
        World world = new World(100, 1);
        world.getPeople().add(mock(Person.class));
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

}