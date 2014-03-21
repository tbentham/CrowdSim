package Dijkstra;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class EdgeTest {

    private Vertex source;
    private Vertex destination;
    private double weight;
    private int floor;
    private Edge edge;

    @Before
    public void setUp() {
        source = new Vertex(0, 0, 0);
        destination = new Vertex(1, 1, 0);
        weight = 1.0;
        floor = 0;
        edge = new Edge(source, destination, weight, floor);
    }

    @After
    public void tearDown() {
        edge = null;
    }

    @Test
    public void testGetDestination() {
        assertTrue(edge.getDestination() == destination);
    }

    @Test
    public void testGetSource() {
        assertTrue(edge.getSource() == source);
    }

    @Test
    public void testGetWeight() {
        assertTrue(edge.getWeight() == weight);
    }

    @Test
    public void testGetFloor() {
        assertTrue(edge.getFloor() == floor);
    }
}
