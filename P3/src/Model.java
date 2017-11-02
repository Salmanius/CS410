import javax.vecmath.Vector3d;
import java.io.*;
import java.util.ArrayList;

public class Model {
    String fileName;
    ArrayList<String> topComment;
    String bottomComment;
    ArrayList<Vert> vertices;
    ArrayList<Face> faces;
    String materialFilename;
    ArrayList<Material> materials;
    int num;

    public Model (String fileName){
        this.fileName = fileName;
        vertices = new ArrayList<>();
        faces = new ArrayList<>();
        topComment = new ArrayList<>();
        bottomComment = "This file was modified from the original.";
        num = 0;
        materials = new ArrayList<Material>();
        parse();
    }

    public Model (Model copy) {
        this.fileName = copy.fileName;
        this.topComment = new ArrayList<String>(copy.topComment);
        this.bottomComment = copy.bottomComment;
        this.vertices = cloneVerts(copy.vertices);
        this.faces = cloneFaces(copy.faces);
        this.num = copy.num;
        this.materialFilename = copy.materialFilename;
        this.materials = copy.materials;
        copy.incrementNum();
    }

    void incrementNum(){
        num++;
    }

    void parse(){
        int defaultMaterialIndex = 0;
        int currentMaterialIndex = defaultMaterialIndex;
        File file = new File(fileName);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(" ");
                if (split[0].equals(("mtllib"))){
                    materialFilename = split[1];
                    parseMaterialFile();
                }
                if (split[0].equals("usemtl")){
                    int index = findMaterialIndex(split[1]);
                    if (index == -1){
                        System.out.println("Material referenced was not found, not changing material.");
                    }
                    else {
                        currentMaterialIndex = index;
                    }
                }
                if (split[0].equals("v")){
                    vertices.add(new Vert(split[1],split[2],split[3]));
                }
                else if (split[0].equals("f")){
                    faces.add(new Face(line.substring(2),currentMaterialIndex));
                }
                else if (split[0].equals("#")){
                    topComment.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void parseMaterialFile(){
        File file = new File(materialFilename);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(" ");
                if (split[0].equals("newmtl")){
                    String matName = split[1];
                    Vector3d Ka = new Vector3d();
                    Vector3d Kd = new Vector3d();
                    Vector3d Ks = new Vector3d();
                    double phong = 0;
                    while ((line = br.readLine()) != null) {
                        split = line.split(" ");
                        if (split[0].equals("illum")){
                            Material m = new Material(Ka,Kd,Ks);
                            m.setPhong(phong);
                            m.setName(matName);
                            materials.add(m);
                            break;
                        }
                        else if (split[0].equals("Ns")){
                            phong = Double.parseDouble(split[1]);
                        }
                        else if (split[0].equals("Ka")){
                            double red = Double.parseDouble(split[1]);
                            double green = Double.parseDouble(split[2]);
                            double blue = Double.parseDouble(split[3]);
                            Ka = new Vector3d(red,green,blue);
                        }
                        else if (split[0].equals("Kd")){
                            double red = Double.parseDouble(split[1]);
                            double green = Double.parseDouble(split[2]);
                            double blue = Double.parseDouble(split[3]);
                            Kd = new Vector3d(red,green,blue);
                        }
                        else if (split[0].equals("Ks")) {
                            double red = Double.parseDouble(split[1]);
                            double green = Double.parseDouble(split[2]);
                            double blue = Double.parseDouble(split[3]);
                            Ks = new Vector3d(red, green, blue);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int findMaterialIndex(String name){ //-1 for not found
        for (int i = 0; i < materials.size(); i++){
            if ((materials.get(i).name).equals(name)){
                return i;
            }
        }
        return -1;
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
