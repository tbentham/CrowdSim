import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PersonTest {
    @Test
    public void ConstructorAddsCoordinates() throws Exception {
        Person person = new Person(new Coord(1.0, 2.0));
        assertTrue(person.getCoord().getX() == 1.0);
        assertTrue(person.getCoord().getY() == 2.0);
    }

    @Test(expected = NoGoalException.class)
    public void personThrowsExceptionWhenAdvanceIsCalledWithoutGoalDefined() throws NoGoalException {
        Person p1 = new Person(new Coord(0.0, 0.0));
        p1.advance(0.0);
    }

    @Test
    public void advanceMovesPersonCorrectDistanceInSimpleCase() throws NoGoalException {
        Person p1 = new Person(new Coord(0.0, 0.0));
        p1.setGoal(new Coord(3.0, 4.0));
        p1.advance(5.0);
        assertTrue(p1.getCoord().getX() == 3.0);
        assertTrue(p1.getCoord().getY() == 4.0);
    }

    @Test
    public void setGoalSetsNewGoal() {
        Person p1 = new Person(new Coord(0.0, 0.0));
        p1.setGoal(new Coord(3.0, 4.0));
        assertTrue(p1.getGoal().getX() == 3.0);
        assertTrue(p1.getGoal().getY() == 4.0);
    }

    @Test
    public void setCoordSetsNewCoords() {
        Person p1 = new Person(new Coord(0.0, 0.0));
        p1.setCoord(new Coord(3.0, 4.0));
        assertTrue(p1.getCoord().getX() == 3.0);
        assertTrue(p1.getCoord().getY() == 4.0);
    }
}
