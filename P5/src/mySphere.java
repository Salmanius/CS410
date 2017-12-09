import javax.vecmath.Vector3d;
import java.util.Vector;

public class mySphere {
    double cx;
    double cy;
    double cz;
    double radius;
    double eta;
    Material material;

    public mySphere(double cx, double cy, double cz, double radius, Vector3d ka, Vector3d kd, Vector3d ks, Vector3d kr, Vector3d ko, double eta){
        this.cx = cx;
        this.cy = cy;
        this.cz = cz;
        this.radius = radius;
        this.material = new Material(ka,kd,ks,kr,ko,eta);
    }

    public double getCx() {
        return cx;
    }

    public double getCy() {
        return cy;
    }

    public double getCz() {
        return cz;
    }

    public double getRadius() {
        return radius;
    }
}
