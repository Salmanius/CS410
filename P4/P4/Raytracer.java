import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Raytracer {
    public static void main(String[] args) throws Exception {
        final long start = System.nanoTime();
        Driver driver = new Driver(args[0]);
        String ppmFileName = args[1];
        driver.parse();
        driver.initializePPM(ppmFileName);
        driver.getModelNames();
        driver.readModels();
        driver.performActionsList(driver.models);
        driver.shootRays();
        driver.ppm.writeScene();
        final long end = System.nanoTime();
        NumberFormat formatter = new DecimalFormat("#0.00000");
        System.out.print("Execution time is " + formatter.format((end - start) / 1000000000d) + " seconds");
    }
}
