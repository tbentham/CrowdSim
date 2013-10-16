import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CoordTest {
    @Test
    public void getZReturnsCorrectValue() {
        Coord c1 = new Coord(1.0, 2.0, 3);
        assertEquals(c1.getZ(), 3);
    }
}
