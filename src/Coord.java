public class Coord {

    public double x;
    public double y;
    public double z;

    public Coord(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Coord(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = x;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

}
