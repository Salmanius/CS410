import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        Driver driver = new Driver(args[0]);
        driver.parse();
        ArrayList<String> modelNames = driver.getModelNames();
        ArrayList<Model> models = new ArrayList<>();
        for (String fileName: modelNames){
            models.add(new Model(fileName));
        }

//        Model newModel = new Model(models.get(0));
//        Model newModel2 = new Model(models.get(0));
//
//        System.out.println(models.get(0).topComment.get(0));
//        System.out.println(newModel.topComment.get(0));
//
//        System.out.println(models.get(0).vertices.get(0).x);
//        System.out.println(newModel.vertices.get(0).x);
//
//        models.get(0).faces.get(0).raw = "t";
//
//        System.out.println(models.get(0).faces.get(0).raw);
//        System.out.println(newModel.faces.get(0).raw);
//
//        System.out.println(models.get(0).num);
//        System.out.println(newModel.num);
//        System.out.println(newModel2.num);
//        driver.makeDir();
//        for (int j = 0; j < driver.driverLines.size(); j++) {
//            for (int i = 0; i < models.size(); i++){
//                if (driver.driverLines.get(j).target.equals(models.get(i).fileName)){
//                    models.get(i).writeFile(driver.fileName);
//                }
//            }
//        }

        //rotate, scale, translate
    }

}
