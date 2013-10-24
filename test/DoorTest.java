import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class DoorTest {

    @Test
    public void canConstructDoorFromTwoCoords() {
        Door d1 = new Door(0, 0, 10, 10);
        assertNotNull(d1);
    }

}
