import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WallTest {
    @Test
    public void touchesReturnsTrueForVerticalLineWhenTouching() {
        Wall wall = new Wall(new Coord(0, 0), new Coord(0, 100));
        Person person = new Person(0, 50);
        assertTrue(wall.touches(person));
    }

    @Test
    public void touchesReturnsFalseForVerticalLineWhenNotTouching() {
        Wall wall = new Wall(new Coord(0, 0), new Coord(0, 100));
        Person person = new Person(10, 50);
        assertFalse(wall.touches(person));
    }

    @Test
    public void touchesReturnsTrueForHorizontalLineWhenTouching() {
        Wall wall = new Wall(new Coord(0, 0), new Coord(100, 0));
        Person person = new Person(50, 0);
        assertTrue(wall.touches(person));
    }

    @Test
    public void touchesReturnsFalseForHorizontalLineWhenNotTouching() {
        Wall wall = new Wall(new Coord(0, 0), new Coord(100, 0));
        Person person = new Person(10, 10);
        assertFalse(wall.touches(person));
    }

    @Test
    public void touchesReturnsTrueForSlantedLineWhenTouching() {
        Wall wall = new Wall(new Coord(0, 0), new Coord(100, 100));
        Person person = new Person(50, 50);
        assertTrue(wall.touches(person));
    }

    @Test
    public void touchesReturnsFalseForSlantedLineWhenNotTouching() {
        Wall wall = new Wall(new Coord(0, 0), new Coord(100, 100));
        Person person = new Person(0, 50);
        assertFalse(wall.touches(person));
    }
}
