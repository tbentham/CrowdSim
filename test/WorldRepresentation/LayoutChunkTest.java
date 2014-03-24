package WorldRepresentation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class LayoutChunkTest {

    private LayoutChunk layoutChunk;

    @Before
    public void setUp() {
        World world = new World(100, 1);
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

}