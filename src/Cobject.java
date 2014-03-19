import javax.vecmath.Point3d;

// TODO: Doesn't need an explanation
public class Cobject {

    private int id;
    private int type; // Consider changing these data types
    private Point3d from;
    private Point3d to;

    Cobject(int x1, int y1, int z1, int x2, int y2, int z2, int id, int type) { //This will also change but leaving like this temporarily
        this.id = id;
        this.type = type;
        this.from = new Point3d(x1, y1, z1);
        this.to = new Point3d(x2, y2, z2);    }
    
    // I have removed setters, can add at a later date if necessary.
    
    public Point3d getFrom() {
        return this.from;
    }
    
    public Point3d getTo() {
        return this.to;
    }
    
    public String toString(){
    	// Need MOAR exceptions
    	return "ID: " + this.id + " TYPE: " + this.type + " FROM: " + this.from.toString() + " TO: " + this.to.toString();
    }
    public int getType(){
    	return this.type;
    }
}

