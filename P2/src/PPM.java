import javax.vecmath.Matrix3d;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class PPM {
    String fileName;
    String topLine = "P3";
    int resX;
    int resY;
    int valueRange = 255;
    //int pixelCount = resX*resY;
    int[][] red; //rows by columns
    int[][] green; //ex:  a [10][5]
    int[][] blue; //so 10 tall 5 wide


    public PPM(String fileName, int xRes, int yRes){
        this.resX = xRes;
        this.resY = yRes;
        this.fileName = fileName;
        red = new int[resY][resX];
        green = new int[resY][resX];
        blue = new int[resY][resX];
    }

    public void writeFile() throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(topLine);
        writer.newLine();
        writer.write(resX + " " + resY + " " + valueRange);
        for (int r = 0; r < resY; r++){ //rows
            writer.newLine();
            for (int c = 0; c < resX; c++){ //columns
                writer.write(red[r][c] + " " + green[r][c] + " " + blue[r][c] + " ");
            }
        }
        writer.close();
    }

    public int[][] getRed() {
        return red;
    }

    public void setRed(int value, int r, int c) {
        this.red[r][c] = value;
    }

    public int[][] getGreen() {
        return green;
    }

    public void setGreen(int value, int r, int c) {
        this.green[r][c] = value;
    }

    public int[][] getBlue() {
        return blue;
    }

    public void setBlue(int value, int r, int c) {
        this.blue[r][c] = value;
    }
}