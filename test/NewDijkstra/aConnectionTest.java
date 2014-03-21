package NewDijkstra;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class aConnectionTest {

    private double cost;
    private Integer from;
    private Integer to;
    private int floor;
    private aConnection aConnection;

    @Before
    public void setUp() {
        cost = 1.0;
        from = 0;
        to = 1;
        floor = 0;
        aConnection = new aConnection(cost, from, to, floor);
    }

    @After
    public void tearDown() {
        aConnection = null;
    }

    @Test
    public void testGetCost() {
        assertTrue(aConnection.getCost() == cost);
    }

    @Test
    public void testGetFrom() {
        assertTrue(aConnection.getFrom().equals(from));
    }

    @Test
    public void testGetTo() {
        assertTrue(aConnection.getTo().equals(to));
    }

    @Test
    public void testGetFloor() {
        assertTrue(aConnection.getFloor() == floor);
    }
}
