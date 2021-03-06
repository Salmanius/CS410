import jeigen.DenseMatrix;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import static jeigen.Shortcuts.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


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
   Vector3d ambient = new Vector3d(); //ambient myLight
    ArrayList<myLight> myLights; //rbg values
    ArrayList<mySphere> spheres;
    ArrayList<Model> models;
    PPM ppm;
    int recursionLevel = 0;
    double[][][] rgbMatrix;
    double eta = 1;

    public Driver(String fileName) throws IOException {
        this.fileName = fileName;
        models = new ArrayList<>();
        modelNames = new ArrayList<>();
        driverLines = new ArrayList<DriverLine>();
        spheres = new ArrayList<mySphere>();
        myLights = new ArrayList<myLight>();
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
                    case "recursionLevel":
                        recursionLevel = Integer.parseInt(split[1]);
                        break;
                    case "ambient":
                        ambient.setX(Double.parseDouble(split[1]));
                        ambient.setY(Double.parseDouble(split[2]));
                        ambient.setZ(Double.parseDouble(split[3]));
                        break;
                    case "eta":
                        eta = Double.parseDouble(split[1]);
                        break;
                    case "light":
                        double x = Double.parseDouble(split[1]);
                        double y = Double.parseDouble(split[2]);
                        double z = Double.parseDouble(split[3]);
                        double w = Double.parseDouble(split[4]);
                        double red = Double.parseDouble(split[5]);
                        double green = Double.parseDouble(split[6]);
                        double blue = Double.parseDouble(split[7]);
                        myLights.add(new myLight(x,y,z,w,red,green,blue));
                        break;
                    case "sphere":
                        double cx = Double.parseDouble(split[1]);
                        double cy = Double.parseDouble(split[2]);
                        double cz = Double.parseDouble(split[3]);
                        double radius = Double.parseDouble(split[4]);

                        double ka_red = Double.parseDouble(split[5]);
                        double ka_green = Double.parseDouble(split[6]);
                        double ka_blue = Double.parseDouble(split[7]);
                        Vector3d ka = new Vector3d(ka_red,ka_green,ka_blue);

                        double kd_red = Double.parseDouble(split[8]);
                        double kd_green = Double.parseDouble(split[9]);
                        double kd_blue = Double.parseDouble(split[10]);
                        Vector3d kd = new Vector3d(kd_red, kd_green, kd_blue);

                        double ks_red = Double.parseDouble(split[11]);
                        double ks_green = Double.parseDouble(split[12]);
                        double ks_blue = Double.parseDouble(split[13]);
                        Vector3d ks = new Vector3d(ks_red, ks_green, ks_blue);

                        double kr_red = Double.parseDouble(split[14]);
                        double kr_green = Double.parseDouble(split[15]);
                        double kr_blue = Double.parseDouble(split[16]);
                        Vector3d kr = new Vector3d(kr_red, kr_green, kr_blue);

                        double ko_red = Double.parseDouble(split[17]);
                        double ko_green = Double.parseDouble(split[18]);
                        double ko_blue = Double.parseDouble(split[19]);
                        Vector3d ko = new Vector3d(ko_red, ko_green, ko_blue);

                        double eta = Double.parseDouble(split[20]);

                        spheres.add(new mySphere(cx,cy,cz,radius,ka,kd,ks,kr,ko,eta));

                        break;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void progressPercentage(int remain, int total) {
        if (remain > total) {
            throw new IllegalArgumentException();
        }
        int maxBareSize = 10; // 10unit for 100%
        int remainProcent = ((100 * remain) / total) / maxBareSize;
        char defaultChar = '-';
        String icon = "*";
        String bare = new String(new char[maxBareSize]).replace('\0', defaultChar) + "]";
        StringBuilder bareDone = new StringBuilder();
        bareDone.append("[");
        for (int i = 0; i < remainProcent; i++) {
            bareDone.append(icon);
        }
        String bareRemain = bare.substring(remainProcent, bare.length());
        System.out.print("\r" + bareDone + bareRemain + " " + remainProcent * 10 + "%");
        if (remain == total) {
            System.out.print("\n");
        }
    }

    public void shootRays(){
//        double tmax = 0;
//        double tmin = 10000;
        int counter = 0;
        int max = (int) (resY*resX);
        rgbMatrix = new double[(int)resY][(int)resX][3];
        for (int r = 0; r < resY; r++) { //resX: horizontal resolution: number of columns: first box 2d arrays
            for (int c = 0; c < resX; c++) { //resY: vertical resolution: number of rows: second box in 2d arrays
                Vector3d pixelPoint = findPixelPoint(c, r);
                Vector3d pixelRay = new Vector3d(pixelPoint); //not ray yet
                pixelRay.sub(eye);
                pixelRay.normalize();
                Vector3d refatt = new Vector3d(1,1,1); //reflection attenuation
                Vector3d accumulator = new Vector3d(0,0,0);
                Vector3d color = castRay(pixelPoint, pixelRay, accumulator, refatt, recursionLevel);
                rgbMatrix[r][c][0] = color.getX();
                rgbMatrix[r][c][1] = color.getY();
                rgbMatrix[r][c][2] = color.getZ();
                counter++;
            }
            progressPercentage(counter,max);
        }
        //System.out.println("\ntmin: " + tmin);
        //System.out.println("tmax: " + tmax);
        //ppm.giveT(tmatrix,tmin,tmax);
        ppm.giveRGB(rgbMatrix);
    }

    public Vector3d castRay(Vector3d pixelPoint, Vector3d pixelRay, Vector3d accumulator, Vector3d refatt, int level){
        double t = 0;
        double tmin = 0;
        Boolean firstTime = true;
        Vector3d surfaceNormal = new Vector3d();
        Vector3d hitPoint = new Vector3d();
        Material material = new Material();
        Vector3d center = new Vector3d();
        //ray triangle intersection
        for (int m = 0; m < models.size(); m++) {
            for (int f = 0; f < models.get(m).faces.size(); f++) {
                double tTemp = triangleIntersection(models.get(m).faces.get(f), pixelPoint, pixelRay, m);
                //System.out.println(t);
                if (tTemp <= 0.0001) {
                    continue;
                }
                if (tTemp < tmin) {
                    tmin = tTemp;
                }
                if (firstTime) {
                    firstTime = false;
                    t = tTemp;
                }
                if (tTemp <= t && tTemp > 0){
                    t = tTemp;
                    hitPoint = calculateHitPoint(t, pixelPoint, pixelRay);
                    surfaceNormal = calculateFaceSurfaceNormal(models.get(m).faces.get(f),m);
                    int materialIndex = models.get(m).faces.get(f).materialIndex;
                    material = models.get(m).materials.get(materialIndex);
                    //System.out.println(material.Kd);
                }

            }
        }
        //ray sphere intersection
        //same loop but with spheres
        for (int s = 0; s < spheres.size(); s++) {
            double tTemp = sphereIntersection(spheres.get(s), pixelPoint, pixelRay);
            if (tTemp <= 0.0001) {
                continue;
            }
            if (tTemp < tmin) {
                tmin = tTemp;
            }
            if (firstTime) {
                firstTime = false;
                t = tTemp;
            }
            if (tTemp <= t && tTemp > 0) {
                t = tTemp;
                hitPoint = calculateHitPoint(t, pixelPoint, pixelRay);
                Vector3d hitPointCopy = new Vector3d(hitPoint);
                surfaceNormal = calculateSphereSurfaceNormal(hitPointCopy, spheres.get(s));
                surfaceNormal.normalize();
                material = new Material(spheres.get(s).material);
                center = new Vector3d(spheres.get(s).cx,spheres.get(s).cy,spheres.get(s).cz);
            }
        }
        Vector3d color = new Vector3d();
        if(surfaceNormal.dot(pixelRay) > 0.0)
        {
            surfaceNormal.scale(-1);
        }
        if (t == 0){
            color.setX(0);
            color.setY(0);
            color.setZ(0);
            return accumulator;
        }
        else {
            color = colorifizer(surfaceNormal, hitPoint, material, pixelPoint);
        }
        pixelRay.scale(-1);
        pixelRay.normalize();
        Vector3d tempAcc;
        tempAcc = pairwiseProduct(refatt,pairwiseProduct(material.Ko,color));
        //tempAcc.scale((1.0 - material.reflectivity));
        accumulator.add(tempAcc);
        if (level > 0){
            Vector3d refR = new Vector3d(surfaceNormal); //reflection ray
            double refRNum = surfaceNormal.dot(pixelRay);
            refRNum = refRNum * 2;
            refR.scale(refRNum);
            refR.sub(pixelRay);
            refR.normalize();
            accumulator = castRay(hitPoint, refR, accumulator, pairwiseProduct(material.Kr,refatt), (level-1));
        }
        //check depth > 0, if sum ko < 3, refraction code
        double sum = material.Ko.getX() + material.Ko.getY() + material.Ko.getZ();
        if ((level > 0) && (sum < 3)){
            Vector3d thru = new Vector3d(0,0,0);
            Vector3d [] fraRayandPoint = refractExternal(center,pixelRay,hitPoint,material.eta,eta);
            if (fraRayandPoint != null){
                thru = castRay(fraRayandPoint[1],fraRayandPoint[0],thru,pairwiseProduct(material.Kr,refatt),(level-1));
                //Vector3d temp = new Vector3d(1,1,1);
                //temp.sub(material.Ko);
                //accumulator.add(pairwiseProduct(pairwiseProduct(refatt,temp),thru));
                accumulator.x += (refatt.x * (1.0 - material.Ko.x) * thru.x);
                accumulator.y += (refatt.y * (1.0 - material.Ko.y) * thru.y);
                accumulator.z += (refatt.z * (1.0 - material.Ko.z) * thru.z);
            }
        }
        return accumulator;
    }

    public Vector3d refractInternal(Vector3d W, Vector3d point, Vector3d surfaceNormal, double eta1, double eta2){ //W, pt, N, eta1, eta2) //w is direction vector (but with -1 scale) pt is hit point
        Vector3d T;
        double ratio = eta1/eta2;
        double alpha = -1.0 * ratio;
        double wn = W.dot(surfaceNormal);
        // radsq = etar**2 * (wn**2 - 1) + 1
        double radsq = ((Math.pow(ratio,2))*(Math.pow(wn,2) - 1)) + 1;
        if (radsq < 0.0){
            T = new Vector3d(0,0,0);
        }
        else {
            //b = (etar * wn) - sqrt(radsq)       #beta = mu(W.N) - rad
            //T = a * W + b * N                #T = alpha(W) + beta(N) = the refracted vector (outside->inside or inside->outside)
            double b = (ratio*wn) - Math.sqrt(radsq);
            T = new Vector3d(W);
            T.scale(alpha);
            Vector3d N = new Vector3d(surfaceNormal);
            N.scale(b);
            T.add(N);
        }
        T.normalize();
        return T;
    }

    public Vector3d[] refractExternal(Vector3d center, Vector3d W, Vector3d point, double etaIn, double etaOut){ //W, pt, eta_in, eta_out)
//        System.out.println("Center " + center);
//        System.out.println("W " + W);
//        System.out.println("Point " + point);
        Vector3d [] rayAndPoint = new Vector3d[2];
        Vector3d C = new Vector3d(center);
        Vector3d T1, T2;
        Vector3d pointMinusCenter = new Vector3d(point);
        pointMinusCenter.sub(C);
        T1 = refractInternal(W,point,pointMinusCenter,etaOut,etaIn);
        if ((T1.x + T1.y + T1.z) == 0){
            return null;
        }
        else {
            //exit = pt + 2 * np.dot((self.C - pt),T1) * T1
            Vector3d centerMinusPoint = new Vector3d(C);
            centerMinusPoint.sub(point);
            Vector3d exit = new Vector3d(point);
            double temp = centerMinusPoint.dot(T1);
            temp = temp*2;
            Vector3d T1Copy = new Vector3d(T1);
            T1Copy.scale(temp);
            exit.add(T1Copy);
            Vector3d insideNormal = new Vector3d(C);
            insideNormal.sub(exit);
            insideNormal.normalize();
            // T2 = self.refract_tray(-T1, exit, Nin, eta_in, eta_out)
            T1.scale(-1);
            T2 = refractInternal(T1,exit,insideNormal,etaIn,etaOut);
            rayAndPoint[1] = new Vector3d(exit);
            rayAndPoint[0] = new Vector3d(T2);
            return rayAndPoint;
        }
    }

    public boolean shadowCheck(Vector3d QL,Vector3d Q){
        Vector3d QLcopy = new Vector3d(QL);
        Vector3d Qcopy = new Vector3d(Q);
        for (int m = 0; m < models.size(); m++){
            for (int f = 0; f < models.get(m).faces.size(); f++){
                if(triangleIntersection(models.get(m).faces.get(f), Qcopy, QLcopy, m) > 0.0001){
                    return true;
                }
            }
        }
        for (int s = 0; s < spheres.size(); s++){
            if (sphereIntersection(spheres.get(s), Qcopy, QLcopy) > 0.0001){
                return true;
            }
        }
        return false;
    }


    public Vector3d colorifizer(Vector3d surfaceNormal, Vector3d Q, Material material, Vector3d pixelPoint){
        surfaceNormal.normalize();
        //ambient
        Vector3d color = new Vector3d(ambient);
        color = pairwiseProduct(color,material.Ka);
        //System.out.println("ambient pairwise: " + color);
        Vector3d diffuse = new Vector3d(0,0,0);
        Vector3d specular = new Vector3d(0,0,0);
        //specular and diffuse
        for (int i = 0; i < myLights.size(); i++){
            //diffuse
            Vector3d light = new Vector3d(myLights.get(i).getX(),myLights.get(i).getY(),myLights.get(i).getZ());
            Vector3d QL = new Vector3d();
            QL.sub(light,Q);
            QL.normalize();
            if (shadowCheck(QL,Q)){
                continue;
            }
            double diffuseCheck = surfaceNormal.dot(QL);
            if (diffuseCheck > 0){
                Vector3d lightRGB = new Vector3d(myLights.get(i).rgb);
                Vector3d tempDiffuse = pairwiseProduct(lightRGB,material.Kd);
                //System.out.println("diffuse pairwise:" + tempDiffuse);
                tempDiffuse.scale(diffuseCheck);
                diffuse.add(tempDiffuse);
            }
            //specular
            Vector3d QE = new Vector3d(pixelPoint.getX(),pixelPoint.getY(),pixelPoint.getZ()); //aka QE
            QE.sub(Q);
            QE.normalize();
            //System.out.println("QE:" + hitToPix);
            Vector3d P = new Vector3d(surfaceNormal);//point along the light exit ray
            P.scale((2*diffuseCheck));
            P.sub(QL);
            //System.out.println("P:" + P);
            double specularCheck = P.dot(QE);
            if (specularCheck >= 0) {
                Vector3d tempSpecular = pairwiseProduct(myLights.get(i).rgb,material.Ks);
                //System.out.println("specular pairwise:" + tempSpecular);
                double specularScale = Math.pow(specularCheck,material.phong);
                tempSpecular.scale(specularScale);
                specular.add(tempSpecular);
            }
        }
        color.add(diffuse);
        color.add(specular);
        return color;
    }

    Vector3d calculateFaceSurfaceNormal(Face f,int m){
        Vector3d a = new Vector3d();
        Vector3d b = new Vector3d();
        Vector3d c = new Vector3d();
        a.setX(models.get(m).vertices.get(f.point1 - 1).x);
        a.setY(models.get(m).vertices.get(f.point1 - 1).y);
        a.setZ(models.get(m).vertices.get(f.point1 - 1).z);
        b.setX(models.get(m).vertices.get(f.point2 - 1).x);
        b.setY(models.get(m).vertices.get(f.point2 - 1).y);
        b.setZ(models.get(m).vertices.get(f.point2 - 1).z);
        c.setX(models.get(m).vertices.get(f.point3 - 1).x);
        c.setY(models.get(m).vertices.get(f.point3 - 1).y);
        c.setZ(models.get(m).vertices.get(f.point3 - 1).z);

        Vector3d AB = new Vector3d(b);
        AB.sub(a);
        Vector3d AC = new Vector3d(c);
        AC.sub(a);

        Vector3d surfaceNormal = new Vector3d();
        surfaceNormal.cross(AB,AC);
        surfaceNormal.normalize();
        surfaceNormal.scale(-1);
        return surfaceNormal;
    }

    Vector3d calculateHitPoint(double t, Vector3d pixelPoint, Vector3d pixelRay){
        Vector3d hitPoint = new Vector3d(pixelPoint);
        Vector3d pixelRayCopy = new Vector3d(pixelRay);
        pixelRayCopy.scale(t);
        hitPoint.add(pixelRayCopy);
        return hitPoint;
    }

    Vector3d calculateSphereSurfaceNormal(Vector3d Q, mySphere sphere){
        Vector3d sphereCenter = new Vector3d(sphere.cx,sphere.cy,sphere.cz);
        Q.sub(sphereCenter);
        return Q;
    }

    Vector3d pairwiseProduct(Vector3d v1, Vector3d v2){
        return new Vector3d(v1.getX()*v2.getX(),v1.getY()*v2.getY(), v1.getZ()*v2.getZ());
    }

    public double triangleIntersection(Face face, Vector3d pixelPoint, Vector3d pixelRay, int mNum){
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
        if (((beta + gamma) > 1.001)){
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
        for (int i = 0; i < models.size(); i++){
            models.get(i).writeFile(fileName);
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

    public void performActionsList(ArrayList<Model> modelsList){
        ArrayList<Model> newModels = new ArrayList<>();
        for (int i = 0; i < driverLines.size(); i++){
            Model baseModel = modelsList.get(findModel(modelsList, driverLines.get(i).target));
            newModels.add(performActions(baseModel, driverLines.get(i)));
        }
        models = new ArrayList<>(newModels);
    }

    Model performActions(Model model, DriverLine dline){
        Model newModel = new Model(model);

        DenseMatrix scaleMatrix = scale(dline.scale);
        DenseMatrix translateMatrix = translate(dline.tx,dline.ty,dline.tz);
        DenseMatrix rotateMatrix = rotate(dline.wx,dline.wy,dline.wz, dline.theta);

        //DenseMatrix translateScaleMatrix = translateMatrix.mmul(scaleMatrix);
        DenseMatrix finalMatrix = translateMatrix.mmul((scaleMatrix.mmul(rotateMatrix)));
        //DenseMatrix finalMatrix = translateScaleMatrix.mmul(rotateMatrix);

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
        DenseMatrix finalMatrix = rTranspose.mmul(zMatrix.mmul(rotateMatrix));
        //DenseMatrix finalMatrix = rotateMatrix.mmul(zMatrix);
        //finalMatrix = finalMatrix.mmul(rTranspose);

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
        //System.out.println(translateMatrix);

        return translateMatrix;
    }

}
