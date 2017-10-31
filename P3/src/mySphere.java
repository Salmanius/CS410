
public class mySphere {
    double cx;
    double cy;
    double cz;
    double radius;
    double kaRed;
    double kaGreen;
    double kaBlue;
    double kdRed;
    double kdGreen;
    double kdBlue;
    double ksRed;
    double ksGreen;
    double ksBlue;

    public mySphere(double cx, double cy, double cz, double radius){
        this.cx = cx;
        this.cy = cy;
        this.cz = cz;
        this.radius = radius;
    }

    public void setKa(double ka_red,double ka_green, double ka_blue){
        this.kaRed = ka_red;
        this.kaGreen = ka_green;
        this.kaBlue = ka_blue;
    }

    public void setKd(double kd_red, double kd_green, double kd_blue){
        this.kdRed = kd_red;
        this.kdGreen = kd_green;
        this.kdBlue = kd_blue;
    }

    public void setKs(double ks_red, double ks_green, double ks_blue){
        this.ksRed = ks_red;
        this.ksGreen = ks_green;
        this.ksBlue = ks_blue;
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
