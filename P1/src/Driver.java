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

        DenseMatrix finalMatrix = scaleMatrix.mmul(rotateMatrix);
        finalMatrix.mmul(translateMatrix);

        System.out.println("Here's where I could mmul my final matrix to the matrix in the model. But i ran out of time so sadly it does not function.");
        return newModel;
    }

    DenseMatrix rotate(float wx, float wy, float wz, float theta){
        DenseMatrix rotateMatrix = zeros(4,4);
        rotateMatrix.set(3,3,1);

        Vector3d w = new Vector3d(wx,wy,wz);
        w.normalize();
        Vector3d m = new Vector3d();
//        DenseMatrix wMatrix = zeros(1,3);
//        wMatrix.set(0,0,(wx/(sqrt(Math.pow(wx,2)+Math.pow(wy,2)+Math.pow(wz,2)))));
//        wMatrix.set(0,1,(wy/(sqrt(Math.pow(wx,2)+Math.pow(wy,2)+Math.pow(wz,2)))));
//        wMatrix.set(0,2,(wz/(sqrt(Math.pow(wx,2)+Math.pow(wy,2)+Math.pow(wz,2)))));
//        //copying w to m
//        DenseMatrix mMatrix = zeros(1,3);
//        mMatrix.set(0,0,wMatrix.get(0,0));
//        mMatrix.set(0,1,wMatrix.get(0,1));
//        mMatrix.set(0,2,wMatrix.get(0,2));

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
        m.normalize();
//        mMatrix.set(0,smallest,1);
//        double mX = (mMatrix.get(0,0)/(sqrt(Math.pow(mMatrix.get(0,0),2)+Math.pow(mMatrix.get(0,1),2)+Math.pow(mMatrix.get(0,2),2))));
//        double my = (mMatrix.get(0,1)/(sqrt(Math.pow(mMatrix.get(0,0),2)+Math.pow(mMatrix.get(0,1),2)+Math.pow(mMatrix.get(0,2),2))));
//        double mz = (mMatrix.get(0,2)/(sqrt(Math.pow(mMatrix.get(0,0),2)+Math.pow(mMatrix.get(0,1),2)+Math.pow(mMatrix.get(0,2),2))));
//
//        mMatrix.set(0,0,mX);
//        mMatrix.set(0,1,my);
//        mMatrix.set(0,2,mz);

        Vector3d u = new Vector3d();
        u.cross(w,m);

        Vector3d v = new Vector3d();
        v.cross(w,u);

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

        DenseMatrix rTranspose = rotateMatrix.t();

        DenseMatrix zMatrix = zeros(4,4);
        zMatrix.set(3,3,1);
        zMatrix.set(2,2,1);

        zMatrix.set(0,0,Math.cos(theta));
        zMatrix.set(1,0,Math.sin(theta));
        zMatrix.set(1,1,Math.cos(theta));
        zMatrix.set(0,1,(Math.cos(theta)*(-1)));


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
