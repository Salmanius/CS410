import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import jeigen.DenseMatrix;
import jeigen.SparseMatrixLil;
import javax.vecmath.Vector3d;
import static java.lang.Math.sqrt;
import static jeigen.Shortcuts.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;

import static jeigen.SparseMatrixLil.speye;

public class Driver {
    String fileName;
    ArrayList<DriverLine> driverLines;

    public Driver(String fileName){
        this.fileName = fileName;
        driverLines = new ArrayList<DriverLine>();
    }

    void parse() throws IOException {
        String extension = fileName.substring(fileName.length() - 4);
        if (!(extension.equals(".txt"))) {
            throw new IOException("Driver File must be of type '.txt'.");
        }
        File file = new File(fileName);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(" ");
                if (split.length < 9){
                    throw new IOException("Line in file does not contain all required information: \n" + line);
                }
                else if (!(split[0].equals("model"))){
                    throw new IOException("Line not properly formatted.");
                }
                driverLines.add(new DriverLine(split[1],split[2],split[3],split[4],split[5],split[6],split[7],split[8],split[9]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ArrayList<String> getModelNames() throws IOException {
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < driverLines.size();i++) {
            if (!(names.contains(driverLines.get(i).target))) {
                String fileName = driverLines.get(i).target;
                String extension = fileName.substring(fileName.length() - 4);
                if(!(extension.equals(".obj"))){
                    throw new IOException("Target file was not of type obj.");
                }
                names.add(driverLines.get(i).target);
            }
        }
        return names;
    }


    void makeDir() throws Exception {
        File dir = new File(fileName.substring(0,fileName.length() - 4));
        if (!(dir.exists())) {
            try {
                dir.mkdir();
            } catch (Exception e) {
                throw new Exception("Could not create new directory to house .obj files.");
            }
        }
    }

    int findModel(ArrayList<Model> models, String target){
        for (int i = 0; i < models.size(); i++){
            if (models.get(i).fileName.equals(target)){
                return i;
            }
        }
        return -1;
    }

    ArrayList<Model> performActionsList(ArrayList<Model> models){
        ArrayList<Model> newModels = new ArrayList<>();
        for (int i = 0; i < driverLines.size(); i++){
            Model baseModel = models.get(findModel(models, driverLines.get(i).target));
            newModels.add(performActions(baseModel, driverLines.get(i)));
        }
        return newModels;
    }

    Model performActions(Model model, DriverLine dline){
        Model newModel = new Model(model);

        DenseMatrix scaleMatrix = scale(dline.scale);
        DenseMatrix translateMatrix = translate(dline.tx,dline.ty,dline.tz);
        DenseMatrix rotateMatrix = rotate(dline.wx,dline.wy,dline.wz, dline.theta);
//        System.out.println("Rotate");
//        System.out.println(rotateMatrix);
//        System.out.println("Scale");
//        System.out.println(scaleMatrix);
//        System.out.println("Translate");
//        System.out.println(translateMatrix);

        DenseMatrix scaleTranslateMatrix = scaleMatrix.mmul(translateMatrix);
        //System.out.println(scaleTranslateMatrix);
        DenseMatrix finalMatrix = rotateMatrix.mmul(scaleTranslateMatrix);
        //System.out.println(finalMatrix);

        for (int i = 0; i < model.vertices.size(); i++){
            DenseMatrix row = ones(4,1);
            row.set(0,0,model.vertices.get(i).x);
            row.set(1,0,model.vertices.get(i).y);
            row.set(2,0,model.vertices.get(i).z);
            DenseMatrix newRow = finalMatrix.mmul(row);
            newModel.vertices.set(i,new Vert(newRow.get(0,0),newRow.get(1,0),newRow.get(2,0)));
        }

        return newModel;
    }



    DenseMatrix rotate(float wx, float wy, float wz, float theta){
        DenseMatrix rotateMatrix = zeros(4,4);
        rotateMatrix.set(3,3,1);

        Vector3d w = new Vector3d(wx,wy,wz);
        w.normalize();
        Vector3d m = new Vector3d(w);

        double check = w.getX();
        int smallest = 0;
        if (check > w.getY()){
            smallest = 1;
            check = w.getY();
        }
        if (check > w.getZ()){
            smallest = 2;
        }

        if (smallest == 0){
            m.setX(1);
        }
        else if (smallest == 1){
            m.setY(1);
        }
        else if (smallest == 2){
            m.setZ(1);
        }
        //System.out.println("VectorW: " + w);
        m.normalize();
        //System.out.println("VectorM "  + m);
        Vector3d u = new Vector3d();
        u.cross(w,m);
        //System.out.println("VectorU: " + u);
        u.normalize();
        //System.out.println("VectorU-NORMED: " + u);
        Vector3d v = new Vector3d();
        v.cross(w,u);
        //System.out.println("VectorV: " + v);
        v.normalize();
        //System.out.println("VectorV-NORMED: " + v);

        //set first row
        rotateMatrix.set(0,0,u.getX());
        rotateMatrix.set(0,1,u.getY());
        rotateMatrix.set(0,2,u.getZ());
        //set second
        rotateMatrix.set(1,0,v.getX());
        rotateMatrix.set(1,1,v.getY());
        rotateMatrix.set(1,2,v.getZ());
        //set third
        rotateMatrix.set(2,0,w.getX());
        rotateMatrix.set(2,1,w.getY());
        rotateMatrix.set(2,2,w.getZ());

        //System.out.println(rotateMatrix);

        DenseMatrix rTranspose = rotateMatrix.t();


        DenseMatrix zMatrix = zeros(4,4);
        zMatrix.set(3,3,1);
        zMatrix.set(2,2,1);

        double radians = Math.toRadians(theta);
        zMatrix.set(0,0,Math.cos(radians));
        zMatrix.set(1,0,Math.sin(radians));
        zMatrix.set(1,1,Math.cos(radians));
        zMatrix.set(0,1,(Math.sin(radians)*(-1)));

        //System.out.println(zMatrix);

        DenseMatrix finalMatrix = rotateMatrix.mmul(zMatrix);
        finalMatrix = finalMatrix.mmul(rTranspose);

        return finalMatrix;
    }

    DenseMatrix scale(float scale){
        DenseMatrix scaleMatrix = zeros(4,4);
        scaleMatrix.set(0,0,scale);
        scaleMatrix.set(1,1,scale);
        scaleMatrix.set(2,2,scale);
        scaleMatrix.set(3,3,1);

        return scaleMatrix;
    }

    DenseMatrix translate(float tx, float ty, float tz){
        DenseMatrix translateMatrix = zeros(4,4);
        translateMatrix.set(0,0,1);
        translateMatrix.set(1,1,1);
        translateMatrix.set(2,2,1);
        translateMatrix.set(3,3,1);
        //identity made
        translateMatrix.set(0,3,tx);
        translateMatrix.set(1,3,ty);
        translateMatrix.set(2,3,tz);

        return translateMatrix;
    }



}
