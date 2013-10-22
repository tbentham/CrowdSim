import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WallTest {
    @Test
    public void touchesReturnsTrueForVerticalLineWhenTouching() {
        Wall wall = new Wall(new Coord(0, 0), new Coord(0, 100));
        Person person = new Person(new Coord(0, 50));
        assertTrue(wall.touches(person));
        Person person2 = new Person(new Coord(0, 0));
        assertTrue(wall.touches(person2));
    }

    @Test
    public void touchesReturnsFalseForVerticalLineWhenNotTouching() {
        Wall wall = new Wall(new Coord(0, 0), new Coord(0, 100));
        Person person = new Person(new Coord(10, 50));
        assertFalse(wall.touches(person));
        Person person2 = new Person(new Coord(0, 150));
        assertFalse(wall.touches(person2));
        Person person3 = new Person(new Coord(0, -10));
        assertFalse(wall.touches(person3));
        Person person4 = new Person(new Coord(10, 0));
        assertFalse(wall.touches(person4));
    }

    @Test
    public void touchesReturnsTrueForHorizontalLineWhenTouching() {
        Wall wall = new Wall(new Coord(0, 0), new Coord(100, 0));
        Person person = new Person(new Coord(50, 0));
        assertTrue(wall.touches(person));
        Person person2 = new Person(new Coord(0, 0));
        assertTrue(wall.touches(person2));
    }

    @Test
    public void touchesReturnsFalseForHorizontalLineWhenNotTouching() {
        Wall wall = new Wall(new Coord(0, 0), new Coord(100, 0));
        Person person = new Person(new Coord(10, 10));
        assertFalse(wall.touches(person));
        Person person2 = new Person(new Coord(150, 0));
        assertFalse(wall.touches(person2));
        Person person3 = new Person(new Coord(-10, 0));
        assertFalse(wall.touches(person3));
        Person person4 = new Person(new Coord(0, 10));
        assertFalse(wall.touches(person4));
    }

    @Test
    public void touchesReturnsTrueForSlantedLineWhenTouching() {
        Wall wall = new Wall(new Coord(0, 0), new Coord(100, 100));
        Person person = new Person(new Coord(50, 50));
        assertTrue(wall.touches(person));
        Person person2 = new Person(new Coord(0, 0));
        assertTrue(wall.touches(person2));
    }

    @Test
    public void touchesReturnsFalseForSlantedLineWhenNotTouching() {
        Wall wall = new Wall(new Coord(0, 0), new Coord(100, 100));
        Person person = new Person(new Coord(0, 50));
        assertFalse(wall.touches(person));
        Person person2 = new Person(new Coord(150, 150));
        assertFalse(wall.touches(person2));
        Person person3 = new Person(new Coord(-10, 50));
        assertFalse(wall.touches(person3));
        Person person4 = new Person(new Coord(50, 0));
        assertFalse(wall.touches(person4));
        Person person5 = new Person(new Coord(50, 150));
        assertFalse(wall.touches(person5));
    }
}
