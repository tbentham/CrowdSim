import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PersonTest {
    @Test
    public void ConstructorAddsCoordinates() throws Exception {
        Person person = new Person(1.0, 2.0);
        assertTrue(person.getPosX() == 1.0);
        assertTrue(person.getPosY() == 2.0);
    }
}
