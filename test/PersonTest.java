import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PersonTest {
    @Test
    public void ConstructorAddsCoordinates() throws Exception {
        Person person = new Person(1.0, 2.0);
        assertTrue(person.xPos == 1.0);
        assertTrue(person.yPos == 2.0);
    }
}
