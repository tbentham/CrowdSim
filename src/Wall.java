import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Wall implements BuildingObject {

    public Coord startCoord;
    public Coord endCoord;

    public Wall(Coord c1, Coord c2) {
        startCoord = c1;
        endCoord = c2;
    }

    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeLine(startCoord.getX(), startCoord.getY(), endCoord.getX(), endCoord.getY());
    }

    public boolean touches(Person person) {
        // Case: Line is horizontal
        if (endCoord.getY() == startCoord.getY()) {
            return (person.getSize() > Math.abs(endCoord.getY() - person.getCoord().getY()));
        }
        // Case: Line is vertical
        if (endCoord.getX() == startCoord.getX()) {
            return (person.getSize() > Math.abs(endCoord.getX() - person.getCoord().getX()));
        }
        // Otherwise
        // http://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line
        double gradient = (endCoord.getY() - startCoord.getY()) / (endCoord.getX() - startCoord.getX());
        double c = startCoord.getY() - (gradient * startCoord.getX());
        double distance = Math.abs((gradient * person.getCoord().getX() - person.getCoord().getY() + c)) / Math.sqrt(gradient * gradient + 1);
        return (distance < person.getSize());
    }
}
