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
    int[][] red = new int[resY][resX]; //rows by columns
    int[][] green = new int[resY][resX]; //ex:  a [10][5]
    int[][] blue = new int[resY][resX]; //so 10 tall 5 wide
                                                                        //


    public PPM(String fileName, int xRes, int yRes){
        this.resX = xRes;
        this.resY = yRes;
        this.fileName = fileName;
    }

    public void writeFile() throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(topLine);
        writer.newLine();
        writer.write(resX + " " + resY + " " + valueRange);
        for (int r = 0; r < resY; r++){ //rows
            writer.newLine();
            for (int c = 0; c < resX; c++){ //columns
                //writer.write(red[r][c] + " " + green[r][c] + " " + blue[r][c] + " ");
                writer.write(255 + " " + 255 + " " + 255 + " ");
            }
        }
        writer.close();
    }


}