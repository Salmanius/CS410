import javax.vecmath.Vector3d;


public class Material {
    //rgb values (0->1)
    String name = "sphere";
    Vector3d Ka = new Vector3d();
    Vector3d Kd = new Vector3d();
    Vector3d Ks = new Vector3d();
    double phong = 16;

    public Material(Vector3d ka, Vector3d kd, Vector3d ks){
        this.Ka = new Vector3d(ka);
        this.Kd = new Vector3d(kd);
        this.Ks = new Vector3d(ks);
    }

    public Material(Material copy){
        this.Ka = new Vector3d(copy.Ka);
        this.Kd = new Vector3d(copy.Kd);
        this.Ks = new Vector3d(copy.Ks);
    }

    public Material(){}

    public void setName(String name){
        this.name = name;
    }

    public void setPhong(double phong){
        this.phong = phong;
    }
}
