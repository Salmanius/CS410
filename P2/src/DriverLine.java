
public class DriverLine {
    float wx;
    float wy;
    float wz;
    float theta;
    float scale;
    float tx;
    float ty;
    float tz;
    String target;

    public DriverLine(String wx,
                      String wy,
                      String wz,
                      String theta,
                      String scale,
                      String tx,
                      String ty,
                      String tz,
                      String target) {

        this.wx = Float.parseFloat(wx);
        this.wy = Float.parseFloat(wy);
        this.wz = Float.parseFloat(wz);
        this.theta = Float.parseFloat(theta);
        this.scale = Float.parseFloat(scale);
        this.tx = Float.parseFloat(tx);
        this.ty = Float.parseFloat(ty);
        this.tz = Float.parseFloat(tz);
        this.target = target;
    }
}
