import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PeopleTest {

    @Test(expected = OverlapException.class)
    public void addThrowsOverlapExceptionOnIntercept() throws OverlapException {
        Wall mockWall = mock(Wall.class);
        List<BuildingObject> buildingObjectList = mock(List.class);
        Iterator buildingObjectIterator = mock(Iterator.class);
        when(buildingObjectIterator.hasNext()).thenReturn(true, false);
        when(buildingObjectIterator.next()).thenReturn(mockWall);
        when(buildingObjectList.iterator()).thenReturn(buildingObjectIterator);
        Person person = mock(Person.class);
        when(mockWall.touches(person)).thenReturn(true);
        People people = new People();
        people.add(buildingObjectList, person);
    }

    @Test
    public void addFunctionsWithoutOverlap() throws OverlapException {
        Wall mockWall = mock(Wall.class);
        List<BuildingObject> buildingObjectList = mock(List.class);
        Iterator<BuildingObject> buildingObjectIterator = mock(Iterator.class);
        when(buildingObjectIterator.hasNext()).thenReturn(true, false);
        when(buildingObjectIterator.next()).thenReturn(mockWall);
        when(buildingObjectList.iterator()).thenReturn(buildingObjectIterator);
        Person person = mock(Person.class);
        when(mockWall.touches(person)).thenReturn(false);
        People people = new People();
        people.add(buildingObjectList, person);
        assertTrue(people.contains(person));
    }



}
