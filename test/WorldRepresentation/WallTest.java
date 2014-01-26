package WorldRepresentation;

import org.junit.Test;

import javax.vecmath.Point2d;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WallTest {
    @Test
    public void touchesReturnsTrueForVerticalLineWhenTouching() {
        Wall wall = new Wall(0, 0, 0, 100);
        Person person = new Person(0, 50);
        assertTrue(wall.touches(person));
        Person person2 = new Person(0, 0);
        assertTrue(wall.touches(person2));
    }

    @Test
    public void touchesReturnsFalseForVerticalLineWhenNotTouching() {
        Wall wall = new Wall(0, 0, 0, 100);
        Person person = new Person(10, 50);
        assertFalse(wall.touches(person));
        Person person2 = new Person(0, 150);
        assertFalse(wall.touches(person2));
        Person person3 = new Person(0, -10);
        assertFalse(wall.touches(person3));
        Person person4 = new Person(10, 0);
        assertFalse(wall.touches(person4));
    }

    @Test
    public void touchesReturnsTrueForHorizontalLineWhenTouching() {
        Wall wall = new Wall(0, 0, 100, 0);
        Person person = new Person(50, 0);
        assertTrue(wall.touches(person));
        Person person2 = new Person(0, 0);
        assertTrue(wall.touches(person2));
    }

    @Test
    public void touchesReturnsFalseForHorizontalLineWhenNotTouching() {
        Wall wall = new Wall(0, 0, 100, 0);
        Person person = new Person(10, 10);
        assertFalse(wall.touches(person));
        Person person2 = new Person(150, 0);
        assertFalse(wall.touches(person2));
        Person person3 = new Person(-10, 0);
        assertFalse(wall.touches(person3));
        Person person4 = new Person(0, 10);
        assertFalse(wall.touches(person4));
    }

    @Test
    public void touchesReturnsTrueForSlantedLineWhenTouching() {
        Wall wall = new Wall(0, 0, 100, 100);
        Person person = new Person(50, 50);
        assertTrue(wall.touches(person));
        Person person2 = new Person(0, 0);
        assertTrue(wall.touches(person2));
    }

    @Test
    public void touchesReturnsFalseForSlantedLineWhenNotTouching() {
        Wall wall = new Wall(0, 0, 100, 100);
        Person person = new Person(0, 50);
        assertFalse(wall.touches(person));
        Person person2 = new Person(150, 150);
        assertFalse(wall.touches(person2));
        Person person3 = new Person(-10, 50);
        assertFalse(wall.touches(person3));
        Person person4 = new Person(50, 0);
        assertFalse(wall.touches(person4));
        Person person5 = new Person(50, 150);
        assertFalse(wall.touches(person5));
    }

    @Test
    public void wallOfZeroLengthReturnsPointDistance() {
        Wall wall = new Wall(1, 1, 1, 1);
        Person person = new Person(1, 2);
        assertTrue(wall.distance(person) == 1);
    }

    @Test
    public void wallReturnsCorrectDistanceToPoint() {
        Wall wall = new Wall(1, 1, 1, 1);
        Point2d point2d = new Point2d(1, 2);
        assertTrue(wall.distance(point2d) == 1);
    }

    @Test
    public void wallTouchesNearbyPoint() {
        Wall wall = new Wall(1, 1, 2, 2);
        Point2d point2d = new Point2d(1, 2);
        assertTrue(wall.touches(point2d, 2));
    }

    @Test
    public void wallReturnsNearestPointForSimpleExample() {
        Wall wall = new Wall(1, 1, 5, 1);
        Person person = new Person(3, 3);
        assertTrue(wall.nearestPoint(person).x == 3.0);
        assertTrue(wall.nearestPoint(person).y == 1.0);
    }

    @Test
    public void nearestPointReturnsNearestPoint1() {
        Wall wall = new Wall(2, 2, 5, 5);
        Person person = new Person(0, 0);
        Point2d closestPoint = wall.nearestPoint(person);
        assertTrue(closestPoint.x == 2.0);
        assertTrue(closestPoint.y == 2.0);
    }

    @Test
    public void nearestPointReturnsNearestPoint2() {
        Wall wall = new Wall(0, 0, 5, 5);
        Person person = new Person(3, 3);
        Point2d closestPoint = wall.nearestPoint(person);
        assertTrue(closestPoint.x == 3.0);
        assertTrue(closestPoint.y == 3.0);
    }

    @Test
    public void wallWidthTest() {
//        Wall wall = new Wall(50, 0, 50, 50);
//        Wall wall2 = new Wall(14.3, 59.0, 67.9, 5.3);
//        assertTrue(wall.intersects(wall2, 1));
    }
}
