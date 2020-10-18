import ProcessB.HeavyWeight;
import ProcessB.LightWeight;

public class Main {

    public static void main (String args[]){

        if(args[1].equals("ProcessB")) {
            new HeavyWeight(args[1]);
        }   //if
        else if (args[1].equals("ProcessLWB1")){
            new LightWeight(args[1]);
        }   //else-if
        else if(args[1].equals("ProcessLWB1")){
            new LightWeight(args[1]);
        }   //else-if
    }
}
