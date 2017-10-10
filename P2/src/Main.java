import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        Driver driver = new Driver(args[0]);
        String ppmFileName = args[1];

        driver.parse();
        driver.getModelNames();
        driver.readModels();


        driver.makeDir();
        driver.shootRays();
        PPM ppm = new PPM(ppmFileName,(int)driver.resX,(int)driver.resY);
        ppm.writeFile();

    }

}
