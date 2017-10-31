import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class PPM {
    String fileName;
    String topLine = "P3";
    int resX;
    int resY;
    int valueRange = 255;
    double tmin;
    double tmax;
    double[][] tmatrix;

    public PPM(String fileName, int xRes, int yRes){
        this.resX = xRes;
        this.resY = yRes;
        this.fileName = fileName;
    }


    public void giveT(double[][] tmatrix, double tmin,double tmax){
        this.tmatrix = tmatrix;
        this.tmin = tmin;
        this.tmax = tmax;
    }

    public void writeScene() throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(topLine);
        writer.newLine();
        writer.write(resX + " " + resY + " " + valueRange);
    }

    public void writeDepthMap() throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(topLine);
        writer.newLine();
        writer.write(resX + " " + resY + " " + valueRange);
        //for (int r = 0; r < resY; r++){ //rows
        for (int r = resY-1; r > -1; r--){
            writer.newLine();
            for (int c = resX-1; c > -1; c--){ //columns
            //for (int c = 0; c < resX; c++){
                if (tmatrix[r][c] == 0){
                    writer.write(0 + " " + 0 + " " + 0 + " ");
                    continue;
                }
                int[] rgb = calculateRGB(tmatrix[r][c],tmin,tmax);
                writer.write(rgb[0] + " " + rgb[1] + " " + rgb[2] + " ");
            }
        }
        writer.close();
    }

    public int[] calculateRGB(double t, double tmin, double tmax){
        int[] rgb = new int[3]; //rgb
        double ratio = 2 * (t - tmin) / (tmax - tmin);
        rgb[0] = (int)Math.max(0, 255 * (1 - ratio)); //red
        rgb[2] = (int)Math.max(0, 255 * (ratio - 1)); //blue
        rgb[1] = 255 - rgb[2] - rgb[0]; //green
        return rgb;
    }






//    public int[][] getRed() {
//        return red;
//    }
//
//    public void setRed(int value, int r, int c) {
//        this.red[r][c] = value;
//    }
//
//    public int[][] getGreen() {
//        return green;
//    }
//
//    public void setGreen(int value, int r, int c) {
//        this.green[r][c] = value;
//    }
//
//    public int[][] getBlue() {
//        return blue;
//    }
//
//    public void setBlue(int value, int r, int c) {
//        this.blue[r][c] = value;
//    }
}

//if (t <= 0) {
//        ppm.setRed(0, r, c);
//        ppm.setGreen(0, r, c);
//        ppm.setBlue(0, r, c);
//        }
//        else {
//        int[] pixelRGB = calculateRGB(t, tmin, tmax);
//        //System.out.println(Arrays.toString(pixelRGB));
//        ppm.setRed(pixelRGB[0], r, c);
//        ppm.setGreen(pixelRGB[1], r, c);
//        ppm.setBlue(pixelRGB[2], r, c);