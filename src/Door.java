import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Door extends Wall {

    public Door(double x1, double y1, double x2, double y2) {
        super(x1, y1, x2, y2);
    }

    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeLine(startX, startY, endX, endY);
    }

}
