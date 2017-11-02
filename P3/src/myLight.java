import javax.vecmath.Vector3d;

public class myLight {
    double x;
    double y;
    double z;
    double w;
    Vector3d rgb = new Vector3d();

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getX() {

        return x;
    }

    public myLight(double x, double y, double z, double w, double red, double green, double blue){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.rgb = new Vector3d(red,green,blue);
    }
}
