public class Coord {

    public double x;
    public double y;
    public int z;

    public Coord(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Coord(double x, double y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

}
