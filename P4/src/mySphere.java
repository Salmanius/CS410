import javax.vecmath.Vector3d;

public class mySphere {
    double cx;
    double cy;
    double cz;
    double radius;
    Material material;


    public mySphere(double cx, double cy, double cz, double radius, Vector3d ka, Vector3d kd, Vector3d ks){
        this.cx = cx;
        this.cy = cy;
        this.cz = cz;
        this.radius = radius;
        this.material = new Material(ka,kd,ks);
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
