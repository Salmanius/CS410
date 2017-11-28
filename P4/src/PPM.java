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
    double [][][] rgbMatrix;

    public PPM(String fileName, int xRes, int yRes) {
        this.resX = xRes;
        this.resY = yRes;
        this.fileName = fileName;
    }


    public void giveT(double[][] tmatrix, double tmin, double tmax) {
        this.tmatrix = tmatrix;
        this.tmin = tmin;
        this.tmax = tmax;
    }

    public void giveRGB(double[][][] rgbMatrix) {
        this.rgbMatrix = rgbMatrix;
    }

    public void writeScene() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(topLine);
        writer.newLine();
        writer.write(resX + " " + resY + " " + valueRange);
        for (int r = resY - 1; r > -1; r--) {
            writer.newLine();
            for (int c = resX - 1; c > -1; c--) {
                int red = (int)(rgbMatrix[r][c][0]*255);
                int green = (int)(rgbMatrix[r][c][1]*255);
                int blue = (int)(rgbMatrix[r][c][2]*255);
                writer.write(red + " " + green + " " + blue + " ");
                //writer.write(rgbMatrix[r][c][0] + " " + rgbMatrix[r][c][1] + " " + rgbMatrix[r][c][2] + " "); //uncomment for beauty
            }
        }
        writer.close();
    }

    public void writeDepthMap() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(topLine);
        writer.newLine();
        writer.write(resX + " " + resY + " " + valueRange);
        //for (int r = 0; r < resY; r++){ //rows
        for (int r = resY - 1; r > -1; r--) {
            writer.newLine();
            for (int c = resX - 1; c > -1; c--) { //columns
                //for (int c = 0; c < resX; c++){
                if (tmatrix[r][c] == 0) {
                    writer.write(0 + " " + 0 + " " + 0 + " ");
                    continue;
                }
                int[] rgb = calculateRGB(tmatrix[r][c], tmin, tmax);
                writer.write(rgb[0] + " " + rgb[1] + " " + rgb[2] + " ");
            }
        }
        writer.close();
    }

    public int[] calculateRGB(double t, double tmin, double tmax) {
        int[] rgb = new int[3]; //rgb
        double ratio = 2 * (t - tmin) / (tmax - tmin);
        rgb[0] = (int) Math.max(0, 255 * (1 - ratio)); //red
        rgb[2] = (int) Math.max(0, 255 * (ratio - 1)); //blue
        rgb[1] = 255 - rgb[2] - rgb[0]; //green
        return rgb;
    }
}