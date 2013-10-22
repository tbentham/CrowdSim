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
        if (isHorizontal()) {
            Coord max = startCoord.getX() >= endCoord.getX() ? startCoord : endCoord;
            Coord min = startCoord.getX() >= endCoord.getX() ? endCoord : startCoord;
            if (min.getX() <= person.getCoord().getX() && person.getCoord().getX() <= max.getX()) {
                return (person.getSize() > Math.abs(endCoord.getY() - person.getCoord().getY()));
            } else {
                if (person.getCoord().getX() < min.getX()) {
                    return person.getSize() > person.getCoord().distanceFrom(min);
                }
                if (person.getCoord().getX() > max.getX()) {
                    return person.getSize() > person.getCoord().distanceFrom(max);
                }
            }
        }
        // Case: Line is vertical
        if (isVertical()) {
            Coord max = startCoord.getY() >= endCoord.getY() ? startCoord : endCoord;
            Coord min = startCoord.getY() >= endCoord.getY() ? endCoord : startCoord;
            if (min.getY() <= person.getCoord().getY() && person.getCoord().getY() <= max.getY()) {
                return (person.getSize() > Math.abs(endCoord.getX() - person.getCoord().getX()));
            } else {
                if (person.getCoord().getY() < min.getY()) {
                    return person.getSize() > person.getCoord().distanceFrom(min);
                }
                if (person.getCoord().getY() > max.getY()) {
                    return person.getSize() > person.getCoord().distanceFrom(max);
                }
            }
        }
        // Otherwise
        // http://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line
        // The following does not work
        double m = (endCoord.getY() - startCoord.getY()) / (endCoord.getX() - startCoord.getX());
        double c = startCoord.getY() - (m * startCoord.getX());

        double m2 = person.getCoord().getY() / person.getCoord().getX();
        double c2 = person.getCoord().getY() - (m2 * person.getCoord().getX());

        double n = -1 / m;
        double d = person.getCoord().getY() - (m * person.getCoord().getX());

        double xIntersect = (d - c) / (m - n);
        double yIntersect = (m * d - n * c) / (m - n);

        Coord maxX = startCoord.getX() >= endCoord.getX() ? startCoord : endCoord;
        Coord minX = startCoord.getX() >= endCoord.getX() ? endCoord : startCoord;

        Coord maxY = startCoord.getY() >= endCoord.getY() ? startCoord : endCoord;
        Coord minY = startCoord.getY() >= endCoord.getY() ? endCoord : startCoord;

        if (minX.getX() <= xIntersect && xIntersect <= maxX.getX()) {
            if (minY.getY() <= yIntersect && yIntersect <= maxY.getY()) {
                if (person.getCoord().getY() == m * person.getCoord().getX() + c || (m == m2 && c == c2)) {
                    return true;
                }
                double distance = person.getCoord().distanceFrom(new Coord(xIntersect, yIntersect));
                return person.getSize() > distance;
            }
        }

        return person.getSize() > Math.min(person.getCoord().distanceFrom(startCoord), person.getCoord().distanceFrom(endCoord));
    }

    private boolean isHorizontal() {
        return endCoord.getY() == startCoord.getY();
    }

    private Boolean isVertical() {
        return endCoord.getX() == startCoord.getX();
    }
}
