import javafx.scene.canvas.GraphicsContext;

public interface BuildingObject {

    public abstract void draw(GraphicsContext gc);

    public abstract boolean touches(Person person);

}
