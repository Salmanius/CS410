import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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

//    Model rotate(){
//
//    }
//
//    Model scale(){
//
//    }
//
//    Model translate(){
//
//    }
}
