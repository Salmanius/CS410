import java.io.*;
import java.util.ArrayList;

public class Model {
    String fileName;
    ArrayList<String> topComment;
    String bottomComment;
    ArrayList<Vert> vertices;
    ArrayList<Face> faces;
    int num;

    public Model (String fileName){
        this.fileName = fileName;
        vertices = new ArrayList<>();
        faces = new ArrayList<>();
        topComment = new ArrayList<>();
        bottomComment = "This file was modified from the original.";
        num = 0;
        parse();
    }


    public Model (Model copy) {
        this.fileName = copy.fileName;
        this.topComment = new ArrayList<String>(copy.topComment);
        this.bottomComment = copy.bottomComment;
        this.vertices = cloneVerts(copy.vertices);
        this.faces = cloneFaces(copy.faces);
        this.num = copy.num;
        copy.incrementNum();
    }

    void incrementNum(){
        num++;
    }

    void parse(){
        File file = new File(fileName);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(" ");
                if (split[0].equals("v")){
                    vertices.add(new Vert(split[1],split[2],split[3]));
                }
                else if (split[0].equals("f")){
                    faces.add(new Face(line.substring(2)));
                }
                else if (split[0].equals("#")){
                    topComment.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void writeFile(String driverName) throws IOException {
        String extension = fileName.substring(fileName.length() - 4);
        String fileNameNoExtension = fileName.substring(0, fileName.length() - 4);
        String driverNameNoExtension = driverName.substring(0,driverName.length()-4);
        String numFormatted = String.format ("%02d",num);
        BufferedWriter writer = new BufferedWriter(new FileWriter(driverNameNoExtension + "/" +  fileNameNoExtension + "_mw" + numFormatted + extension));
        for (String line: topComment) {
            writer.write(line);
            writer.newLine();
        }
        writer.write(bottomComment);
        writer.newLine();
        for (Vert v: vertices) {
            writer.write("v " + v.x + " " + v.y + " " + v.z);
            writer.newLine();
        }
        for (Face f: faces) {
            writer.write("f " + f.raw);
            writer.newLine();
        }
        writer.close();
    }

    public ArrayList<Vert> cloneVerts(ArrayList<Vert> verts) {
        ArrayList<Vert> clone = new ArrayList<Vert>(verts.size());
        for (int i = 0; i < verts.size(); i++){
            clone.add(new Vert(verts.get(i)));
        }
        return clone;
    }

    public ArrayList<Face> cloneFaces(ArrayList<Face> faces) {
        ArrayList<Face> clone = new ArrayList<Face>(faces.size());
        for (int i = 0; i < faces.size(); i++){
            clone.add(new Face(faces.get(i)));
        }
        return clone;
    }


}
