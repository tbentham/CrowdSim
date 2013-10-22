import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class BasicCanvas extends Application {

    private static List<BuildingObject> objects;
    private static People people;

    public static void main(String[] args) throws Exception {
        objects = new ArrayList<BuildingObject>();
        objects.add(new Wall(new Coord(20, 20), new Coord(220, 20)));
        objects.add(new Wall(new Coord(220, 20), new Coord(220, 220)));
        objects.add(new Wall(new Coord(220, 220), new Coord(20, 220)));
        objects.add(new Wall(new Coord(20, 220), new Coord(20, 20)));
        objects.add(new Door(new Coord(90, 220), new Coord(110, 220)));
        people = new People();
        Person p1 = new Person(new Coord(50, 40));
        p1.setGoal(new Coord(90, 90));
        people.add(objects, p1);
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Drawing Operations Test");
        Group root = new Group();
        Canvas canvas = new Canvas(500, 500);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawShapes(gc);
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public void drawShapes(GraphicsContext gc) {
        for (BuildingObject buildingObject : objects) {
            buildingObject.draw(gc);
        }
        gc.setFill(Color.BLUE);
        for (Person person : people) {
            person.draw(gc);
        }
    }
}
