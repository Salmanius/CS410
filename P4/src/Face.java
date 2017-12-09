import java.util.Arrays;

public class Face {
    String raw;
    int point1 = 1;
    int point2 = 1;
    int point3 = 1;
    int materialIndex = 0;

    public Face(String raw){
        this.raw = raw;
        parse();
    }

    public Face(String raw, int index){
        this.raw = raw;
        this.materialIndex = index;
        parse();
    }

    public Face(Face copy){
        this.raw = copy.raw;
        this.point1 = copy.point1;
        this.point2 = copy.point2;
        this.point3 = copy.point3;
        this.materialIndex = copy.materialIndex;
    }

    void parse(){
        String[] spaceSplit = raw.split(" ");
//        if (raw.contains("//")){
//            String[] point1Raw = spaceSplit[0].split("//");
//            point1 = Integer.parseInt(point1Raw[0]);
//            String[] point2Raw = spaceSplit[1].split("//");
//            point2Raw = spaceSplit[1].split("/");
//            String[] point3Raw = spaceSplit[2].split("//");
//            point3 = Integer.parseInt(point3Raw[0]);
//        }
//        else if (raw.contains("/")){
//            String[] point1Raw = spaceSplit[0].split("/");
//            point1 = Integer.parseInt(point1Raw[0]);
//            String[] point2Raw = spaceSplit[1].split("/");
//            point2 = Integer.parseInt(point2Raw[0]);
//            String[] point3Raw = spaceSplit[2].split("/");
//            point3 = Integer.parseInt(point3Raw[0]);
//        }
//        else {
//            point1 = Integer.parseInt(spaceSplit[1]);
//            point2 = Integer.parseInt(spaceSplit[2]);
//            point3 = Integer.parseInt(spaceSplit[3]);
//        }
//        point1 = Integer.parseInt(spaceSplit[1]);
//        point2 = Integer.parseInt(spaceSplit[2]);
//        point3 = Integer.parseInt(spaceSplit[3]);
        String[] point1Raw = spaceSplit[0].split("//");
        if (point1Raw.length == 1){
            point1Raw = spaceSplit[0].split("/");
        }
        point1 = Integer.parseInt(point1Raw[0]);
        String[] point2Raw = spaceSplit[1].split("//");
        if (point2Raw.length == 1){
            point2Raw = spaceSplit[1].split("/");
        }
        point2 = Integer.parseInt(point2Raw[0]);
        String[] point3Raw = spaceSplit[2].split("//");
        if (point3Raw.length == 1){
            point3Raw = spaceSplit[1].split("/");
        }
        point3 = Integer.parseInt(point3Raw[0]);
    }
}
