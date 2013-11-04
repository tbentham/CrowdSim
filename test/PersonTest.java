//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//public class PersonTest {
//
//    Person person;
//
//    @Before
//    public void init() {
//        person = new Person(1, 1);
//    }
//
//    @Test
//    public void setGoalSetsBooleanAndCorrectGoal() {
//        person.setGoal(2.0, 2.0);
//        assertTrue(person.getGoal().x == 2.0);
//        assertTrue(person.getGoal().y == 2.0);
//        assertTrue(person.isGoalSet());
//    }
//
//    @Test
//    public void setGoalCorrespondsToGetGoal() {
//        person.setGoal(2.0, 2.0);
//        assertTrue(person.getGoal().x == 2.0);
//        assertTrue(person.getGoal().y == 2.0);
//    }
//
//    @Test
//    public void setLocationSetsCorrectLocation() {
//        person.setLocation(2, 2);
//        assertTrue(person.getLocation().x == 2);
//        assertTrue(person.getLocation().y == 2);
//    }
//
//    @Test
//    public void isGoalSetReturnsCorrectAnswer() {
//        assertFalse(person.isGoalSet());
//        person.setGoal(2, 2);
//        assertTrue(person.isGoalSet());
//    }
//}
