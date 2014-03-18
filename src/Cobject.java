import javax.vecmath.Point2d;

// TODO: Doesn't need an explanation
public class Cobject {

    private int id;
    private int type; // Consider changing these data types
    private Point2d from;
    private Point2d to;
    private int floor;

    Cobject(int x1, int y1, int x2, int y2, int z, int id, int type) { //This will also change but leaving like this temporarily
        this.id = id;
        this.type = type;
        this.from = new Point2d(x1, y1);
        this.to = new Point2d(x2, y2);
        this.floor = z;
    }
    
    // I have removed setters, can add at a later date if necessary.
    
    public Point2d getFrom() {
        return this.from;
    }
    
    public Point2d getTo() {
        return this.to;
    }
    
    public String toString(){
    	// Need MOAR exceptions
    	return "ID: " + this.id + " TYPE: " + this.type + " FROM: " + this.from.toString() + " TO: " + this.to.toString();
    }
    public int getType(){
    	return this.type;
    }

    public int getFloor() {
        return floor;
    }
}

