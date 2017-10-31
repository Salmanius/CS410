import javax.vecmath.Vector3d;
import java.util.Vector;

public class Material {
    //rgb values (0->1)
    Vector3d Ka;
    Vector3d Kd;
    Vector3d Ks;

    public Material(Vector3d ka, Vector3d kd, Vector3d ks){
        this.Ka = new Vector3d(ka);
        this.Kd = new Vector3d(kd);
        this.Ks = new Vector3d(ks);
    }
}
