import java.util.Arrays;

public class Face {
    String raw;


    public Face(String raw){
        this.raw = raw;
    }

    public Face(Face copy){
        this.raw = copy.raw;
    }

    float getV(){ //BAD CODE, NEEDS TRUE PARSING LATER
        String[] rawSplit = raw.split("//");
        return (Float.parseFloat(rawSplit[0]));
    }
}

//    String test1 = "1 2 3";
//    String test2 = "1//2 2//2 3//3";
//    String test3 = "1/3/3 2/3/3 3/3/3";
//
//    String[] testSplit = test1.split(" |/");
//
//    System.out.println(Arrays.toString(testSplit));