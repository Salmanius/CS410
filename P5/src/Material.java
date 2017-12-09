import javax.vecmath.Vector3d;


public class Material {
    //rgb values (0->1)
    String name = "sphere";
    Vector3d Ka = new Vector3d();
    Vector3d Kd = new Vector3d();
    Vector3d Ks = new Vector3d();
    Vector3d Kr = new Vector3d(1,1,1);
    Vector3d Ko = new Vector3d(1,1,1);
    double phong = 16;
    double reflectivity = 0.0;
    double eta = 0;

    public Material(Vector3d ka, Vector3d kd, Vector3d ks){
        this.Ka = new Vector3d(ka);
        this.Kd = new Vector3d(kd);
        this.Ks = new Vector3d(ks);
    }

    public Material(Vector3d ka, Vector3d kd, Vector3d ks, Vector3d kr, Vector3d ko,double eta){
        this.Ka = new Vector3d(ka);
        this.Kd = new Vector3d(kd);
        this.Ks = new Vector3d(ks);
        this.Kr = new Vector3d(kr);
        this.Ko = new Vector3d(ko);
        this.eta = eta;
    }

    public Material(Material copy){
        this.name = copy.name;
        this.reflectivity = copy.reflectivity;
        this.phong = copy.phong;
        this.eta = copy.eta;
        this.Ka = new Vector3d(copy.Ka);
        this.Kd = new Vector3d(copy.Kd);
        this.Ks = new Vector3d(copy.Ks);
        this.Kr = new Vector3d(copy.Kr);
        this.Ko = new Vector3d(copy.Ko);
    }

    public Material(){}

    public void setName(String name){
        this.name = name;
    }

    public void setPhong(double phong){
        this.phong = phong;
    }
}
