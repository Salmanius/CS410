import jeigen.DenseMatrix;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import static jeigen.Shortcuts.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class Driver {
    String fileName;
    ArrayList<DriverLine> driverLines;
    ArrayList<String> modelNames;
    Vector3d eye; //Location of the focal point (eye)
    Vector3d look; //the look at point
    Vector3d up; //the up vector
    double d; //the focal length, the distance from the focal point to the image plane
    double[] bounds = new double[4]; //The 'bounds' values indicate the minimum and maximum extend of the bounded image rectangle on the infinite image plane in the camera horizontal and vertical directions respectively.
    double resX; //resolution, horizontal
    double resY; //resolution vertical
    ArrayList<mySphere> spheres;
    ArrayList<Model> models;
    PPM ppm;

    public Driver(String fileName) throws IOException {
        this.fileName = fileName;
        models = new ArrayList<>();
        modelNames = new ArrayList<>();
        driverLines = new ArrayList<DriverLine>();
        spheres = new ArrayList<mySphere>();
    }

    public void initializePPM(String ppmFilename){
        ppm = new PPM(ppmFilename, (int)resX, (int)resY);
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
                switch (split[0]) {
                    case "model":
                        driverLines.add(new DriverLine(split[1],split[2],split[3],split[4],split[5],split[6],split[7],split[8],split[9]));
                        break;
                    case "eye":
                        eye = new Vector3d(Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                        break;
                    case "look":
                        look = new Vector3d(Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                        break;
                    case "up":
                        up = new Vector3d(Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                        break;
                    case "d":
                        d = Double.parseDouble(split[1]);
                        break;
                    case "bounds":
                        bounds[0] = Double.parseDouble(split[1]);
                        bounds[1] = Double.parseDouble (split[2]);
                        bounds[2] = Double.parseDouble(split[3]);
                        bounds[3] = Double.parseDouble(split[4]);
                        break;
                    case "res":
                        resX = Double.parseDouble(split[1]);
                        resY = Double.parseDouble(split[2]);
                        break;
                    case "sphere":
                        double cx = Double.parseDouble(split[1]);
                        double cy = Double.parseDouble(split[2]);
                        double cz = Double.parseDouble(split[3]);
                        double radius = Double.parseDouble(split[4]);
                        spheres.add(new mySphere(cx,cy,cz,radius));
                        break;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void getModelNames() throws IOException {
        for (int i = 0; i < driverLines.size();i++) {
            if (!(modelNames.contains(driverLines.get(i).target))) {
                String fileName = driverLines.get(i).target;
                String extension = fileName.substring(fileName.length() - 4);
                if(!(extension.equals(".obj"))){
                    throw new IOException("Target file was not of type obj.");
                }
                modelNames.add(driverLines.get(i).target);
            }
        }
    }

    void readModels(){
        for (String fileName: modelNames){
            models.add(new Model(fileName));
        }
    }

    void writeModels() throws IOException {
        ArrayList<Model> newModels = performActionsList(models);
        for (int i = 0; i < newModels.size(); i++){
            newModels.get(i).writeFile(fileName);
        }
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

    public Vector3d findPixelPoint(double i, double j){  //Finds the location of point to shoot the ray from or each pixel
        double px = i/(resX-1)*(bounds[2] - bounds[0]) + bounds[0];
        double py = j/(resY-1)*(bounds[3] - bounds[1]) + bounds[1];

        Vector3d temp = new Vector3d(eye);
        temp.sub(look);
        temp.scale(-1);
        Vector3d WV = new Vector3d(temp);
        WV.normalize();
        Vector3d UV = new Vector3d();
        UV.cross(up,WV);
        UV.normalize();
        Vector3d VV = new Vector3d();
        VV.cross(WV,UV);
        VV.normalize();

        Vector3d tempWV = new Vector3d(WV);
        Vector3d tempUV = new Vector3d(UV);
        Vector3d tempVV = new Vector3d(VV);
        tempWV.scale(d);
        tempUV.scale(px);
        tempVV.scale(py);

        double pixPointX = eye.getX() + tempWV.getX() + tempUV.getX() + tempVV.getX();
        double pixPointY = eye.getY() + tempWV.getY() + tempUV.getY() + tempVV.getY();
        double pixPointZ = eye.getZ() + tempWV.getZ() + tempUV.getZ() + tempVV.getZ();

        return new Vector3d(pixPointX,pixPointY,pixPointZ); //This vector - eye (then normed) is our directional vector from the pixel
    }

    //for each pixel
        //throw ray from pixel
        //calculate color along ray
        //fill in pixel

    public void shootRays(){
        double tmax = 0;
        double tmin = 10000;
        int counter0 = 0;
        int counter1 = 0;
        for (int r = 0; r < resX; r++) { //resX: horizontal resolution: number of columns: first box 2d arrays
            for (int c = 0; c < resY; c++) { //resY: vertical resolution: number of rows: second box in 2d arrays
                double t = 0;
                Vector3d pixelPoint = findPixelPoint(r, c);
                Vector3d pixelRay = new Vector3d(pixelPoint); //not ray yet
                pixelRay.sub(eye);
                pixelRay.normalize();
                //ray triangle intersection
                for (int m = 0; m < models.size(); m++) {
                    for (int f = 0; f < models.get(m).faces.size(); f++) {
                        double tTemp = rayIntersection(models.get(m).faces.get(f), pixelPoint, pixelRay, m);
                        //System.out.println(t);
                        if (tTemp <= 0) {
                            continue;
                        }
                        if (tTemp > tmax) {
                            tmax = tTemp;
                        } else if (tTemp < tmin) {
                            tmin = tTemp;
                        }
                        if (t <= 0) {
                            t = tTemp;
                        }
                        if (tTemp < t && tTemp > 0){
                            t = tTemp;
                        }
                    }
                }
                //ray sphere intersection
                //same loop but with spheres
                for (int s = 0; s < spheres.size(); s++) {
                    double tTemp = sphereIntersection(spheres.get(s), pixelPoint, pixelRay);
                    if (tTemp <= 0) {
                        continue;
                    }
                    if (tTemp > tmax) {
                        tmax = tTemp;
                    } else if (tTemp < tmin) {
                        tmin = tTemp;
                    }
                    if (tTemp < t && tTemp > 0){
                        t = tTemp;
                    }
                }
                if (t <= 0) {
                    ppm.setRed(0, r, c);
                    ppm.setGreen(0, r, c);
                    ppm.setBlue(0, r, c);
                }
                else {
                    int[] pixelRGB = calculateRGB(t, tmin, tmax);
                    //System.out.println(Arrays.toString(pixelRGB));
                    ppm.setRed(pixelRGB[0], r, c);
                    ppm.setGreen(pixelRGB[1], r, c);
                    ppm.setBlue(pixelRGB[2], r, c);
                }
                //System.out.println(t);
            }
        }
        System.out.println("tmin: " + tmin);
        System.out.println("tmax: " + tmax);
    }

    public double rayIntersection(Face face, Vector3d pixelPoint, Vector3d pixelRay, int mNum){
        double t = 0;
        //System.out.println(mNum);
        Matrix3d m = new Matrix3d();
        Vector3d a = new Vector3d();
        Vector3d b = new Vector3d();
        Vector3d c = new Vector3d();
        Vector3d y = new Vector3d();

        //setting a,b,c
        a.setX(models.get(mNum).vertices.get(face.point1 - 1).x);
        a.setY(models.get(mNum).vertices.get(face.point1 - 1).y);
        a.setZ(models.get(mNum).vertices.get(face.point1 - 1).z);
        b.setX(models.get(mNum).vertices.get(face.point2 - 1).x);
        b.setY(models.get(mNum).vertices.get(face.point2 - 1).y);
        b.setZ(models.get(mNum).vertices.get(face.point2 - 1).z);
        c.setX(models.get(mNum).vertices.get(face.point3 - 1).x);
        c.setY(models.get(mNum).vertices.get(face.point3 - 1).y);
        c.setZ(models.get(mNum).vertices.get(face.point3 - 1).z);


        //setting y
        y.setX(a.getX() - pixelPoint.getX());
        y.setY(a.getY() - pixelPoint.getY());
        y.setZ(a.getZ() - pixelPoint.getZ());

        //setting left column
        m.m00 = a.getX() - b.getX();
        m.m10 = a.getY() - b.getY();
        m.m20 = a.getZ() - b.getZ();
        //setting middle column
        m.m01 = a.getX() - c.getX();
        m.m11 = a.getY() - c.getY();
        m.m21 = a.getZ() - c.getZ();
        //setting right column
        m.m02 = pixelRay.getX();
        m.m12 = pixelRay.getY();
        m.m22 = pixelRay.getZ();

        //set up matrix from slide
        //cramers rule
        //to get beta: put y in the first column of M |
        //to get gamma: put y in the 2nd column of M  | Put left matrix determinate divided by determinate of just M
        //to get T: put y in the 3rd column of M      |

        double mDet = m.determinant();

        //getting beta
        Matrix3d mBeta = new Matrix3d(m);
        mBeta.m00 = y.getX();
        mBeta.m10 = y.getY();
        mBeta.m20 = y.getZ();
        double betaDet = mBeta.determinant();
        double beta = betaDet/mDet;
        //early exit
        if (beta < 0){
            return t;
        }

        //getting gamma
        Matrix3d mGamma = new Matrix3d(m);
        mGamma.m01 = y.getX();
        mGamma.m11 = y.getY();
        mGamma.m21 = y.getZ();
        double gammaDet = mGamma.determinant();
        double gamma = gammaDet/mDet;
        //early exit
        if (gamma < 0){
            return t;
        }
        if ((beta + gamma) > 1){
            return t;
        }

        //getting T
        Matrix3d mT = new Matrix3d(m);
        mT.m02 = y.getX();
        mT.m12 = y.getY();
        mT.m22 = y.getZ();
        double tDet = mT.determinant();
        t = tDet/mDet;

        return t;
    }

    double sphereIntersection(mySphere sphere, Vector3d pixelPoint, Vector3d pixelRay){
        double t = 0;
        Vector3d cVector = new Vector3d(); //line from pixelPoint to center of sphere
        cVector.x = sphere.getCx() - pixelPoint.getX();
        cVector.y = sphere.getCy() - pixelPoint.getY();
        cVector.z = sphere.getCz() - pixelPoint.getZ();
        double v = cVector.dot(pixelRay);
        double cSquared = cVector.dot(cVector);
        double dSquared = (sphere.radius)*(sphere.radius) - (cSquared-(v*v));
        if (dSquared < 0){
            return t;
        }
        double d = Math.sqrt(dSquared);
        t = v-d;
        return t;
    }


    public int[] calculateRGB(double t, double tmin, double tmax){
        int[] rgb = new int[3]; //rgb
        double ratio = 2 * (t - tmin) / (tmax - tmin);
        rgb[0] = (int)Math.max(0, 255 * (1 - ratio)); //red
        rgb[2] = (int)Math.max(0, 255 * (ratio - 1)); //blue
        rgb[1] = 255 - rgb[2] - rgb[0]; //green
        return rgb;
    }



}
