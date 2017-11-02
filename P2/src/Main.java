
public class Main {
    public static void main(String[] args) throws Exception {
        Driver driver = new Driver(args[0]);
        String ppmFileName = args[1];
        driver.parse();
        driver.getModelNames();
        driver.readModels();
        driver.initializePPM(ppmFileName);
        driver.performActionsList(driver.models);
        //driver.makeDir();
        //driver.writeModels();
        driver.shootRays();
        driver.ppm.writeFile();
    }
}
