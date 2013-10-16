import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Door extends Wall {

    public Door(Coord c1, Coord c2) {
        super(c1, c2);
    }

    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeLine(startCoord.getX(), startCoord.getY(), endCoord.getX(), endCoord.getY());
    }

}
