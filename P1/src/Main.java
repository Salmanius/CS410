import java.sql.Array;
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
        ArrayList<Model> newModels = driver.performActionsList(models);

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
        driver.makeDir();
        for (int i = 0; i < newModels.size(); i++){
            newModels.get(i).writeFile(driver.fileName);
        }

        //rotate, scale, translate
    }

}
