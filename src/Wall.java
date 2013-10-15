import javafx.scene.canvas.GraphicsContext;

public class Wall implements BuildingObject {

    public double xStartPos;
    public double xEndPos;
    public double yStartPos;
    public double yEndPos;

    public Wall(double x1, double y1, double x2, double y2) {
        xStartPos = x1;
        yStartPos = y1;
        xEndPos = x2;
        yEndPos = y2;
    }

    public void draw(GraphicsContext gc) {
        gc.strokeLine(xStartPos, yStartPos, xEndPos, yEndPos);
    }

    public boolean touches(Person person) {
        // Case: Line is horizontal
        if (yEndPos == yStartPos) {
           return (person.getSize() > Math.abs(yEndPos - person.getPosY()));
        }
        // Case: Line is vertical
        if (xEndPos == xStartPos) {
            return (person.getSize() > Math.abs(xEndPos - person.getPosX()));
        }
        // Otherwise
        // http://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line
        double gradient = (yEndPos - yStartPos) / (xEndPos - xStartPos);
        double c = yStartPos - (gradient * xStartPos);
        double distance = Math.abs((gradient * person.getPosX() - person.getPosY() + c)) / Math.sqrt(gradient*gradient + 1);
        return (distance < person.getSize());
    }
}
