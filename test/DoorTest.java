import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class DoorTest {

    @Test
    public void canConstructDoorFromTwoCoords() {
        Door d1 = new Door(mock(Coord.class), mock(Coord.class));
        assertNotNull(d1);
    }

}
