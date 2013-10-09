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

    public static void main(String[] args) {
        objects = new ArrayList<BuildingObject>();
        objects.add(new Wall(20, 20, 220, 20));
        objects.add(new Wall(220, 20, 220, 220));
        objects.add(new Wall(220, 220, 20, 220));
        objects.add(new Wall(20, 220, 20, 20));
        objects.add(new Wall(20, 20, 220, 220));
        people = new People();
        try {
            people.add(objects, new Person(50, 40, 0));
        }
        catch (Exception e) {
           e.printStackTrace();
        }
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
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        for(BuildingObject buildingObject : objects) {
            buildingObject.draw(gc);
            System.out.println(buildingObject.touches(people.get(0)));
        }
        gc.setFill(Color.BLUE);
        for(Person person : people) {
            person.draw(gc);
        }
    }
}
