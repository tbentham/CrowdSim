package Dijkstra;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class VertexTest {

    private double x;
    private double y;
    private int z;
    private Vertex vertex;

    @Before
    public void setUp() {
        x = 0.0;
        y = 0.0;
        z = 0;
        vertex = new Vertex(x, y, z);
    }

    @After
    public void tearDown() {
        vertex = null;
    }

    @Test
    public void testGetZ() {
        assertTrue(vertex.getZ() == z);
    }

    @Test
    public void testGetX() {
        assertTrue(vertex.getX() == z);
    }

    @Test
    public void testGetY() {
        assertTrue(vertex.getY() == z);
    }

    @Test
    public void testGetId() {
        assertTrue(vertex.getId().equals(x + "_" + y));
    }

    @Test
    public void testHashCode() {
        assertTrue(vertex.hashCode() == 1014651460);
        Vertex vertex2 = new Vertex(1.0, 1.0, 1);
        assertTrue(vertex2.hashCode() == 1902156102);
    }

    @Test
    public void testEquals() {
        assertTrue(vertex.equals(vertex));
        Vertex vertex2 = new Vertex(1.0, 1.0, 1);
        assertFalse(vertex.equals(vertex2));
        assertFalse(vertex.equals(null));
        assertFalse(vertex.equals("String"));
    }
}
