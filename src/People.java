import java.util.ArrayList;
import java.util.List;

public class People extends ArrayList<Person> {

      public void add(List<BuildingObject> buildingObjects, Person person) throws OverlapException {
          for(BuildingObject buildingObject : buildingObjects) {
              if (buildingObject.touches(person)) {
                  throw new OverlapException("Person cannot intercept wall");
              }
              else {
                  this.add(person);
              }
          }
      }

}
