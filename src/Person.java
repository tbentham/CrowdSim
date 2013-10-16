import javafx.scene.canvas.GraphicsContext;

public class Person {

    private Coord coord;
    private double size = 5.0;
    private Coord goal;
    private boolean goalSet;

    public Person(Coord c1) {
        coord = c1;
        goalSet = false;
    }

    public void setGoal(Coord c1) {
        goal = c1;
        goalSet = true;
    }

    public Coord getGoal() {
        return goal;
    }

    public void setCoord(Coord c1) {
        coord = c1;
    }

    public Coord getCoord() {
        return coord;
    }

    public double getSize() {
        return size;
    }

    public boolean hasGoal() {
        return goalSet;
    }

    public void advance(double distanceToMove) throws NoGoalException {
        if (!this.hasGoal()) {
            throw new NoGoalException("Person.advance() called with no goal set");
        } else {
            Coord diff = new Coord(goal.getX() - coord.getX(), goal.getY() - coord.getY());
            double distance = Math.sqrt((diff.getX() * diff.getX()) + (diff.getY() * diff.getY()));
            Coord norm = new Coord(diff.getX() / distance, diff.getY() / distance);
            coord = new Coord(coord.getX() + norm.getX() * distanceToMove, coord.getY() + norm.getY() * distanceToMove);
        }

    }

    public void draw(GraphicsContext gc) {
        gc.fillOval(coord.getX() - (size / 2.0), coord.getY() - (size / 2.0), size, size);
    }


}
