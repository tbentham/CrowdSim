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

    public double distanceFrom(Coord c1) {
        return Math.sqrt(Math.pow(Math.abs(x - c1.getX()), 2) + Math.pow(Math.abs(y - c1.getY()), 2));
    }

}
