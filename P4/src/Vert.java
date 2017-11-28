
public class Vert {

    float x;
    float y;
    float z;

    public Vert(String x, String y, String z){
        this.x = Float.parseFloat(x);
        this.y = Float.parseFloat(y);
        this.z = Float.parseFloat(z);
    }

    public Vert(double x, double y, double z){
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
    }

    public Vert(Vert copy){
        this.x = copy.x;
        this.y = copy.y;
        this.z = copy.z;
    }


}
