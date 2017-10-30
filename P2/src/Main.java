import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        Driver driver = new Driver(args[0]);
        String ppmFileName = args[1];

        driver.parse();
        driver.getModelNames();
        driver.readModels();
        
        driver.initializePPM(ppmFileName);
        driver.shootRays();
        driver.ppm.writeFile();

    }

}
