import ProcessB.HeavyWeight;
import ProcessB.LightWeight;

public class Main {

    public static void main (String args[]){

        if(args[0].equals("ProcessB")) {
            new HeavyWeight(args[0]);
        }   //if
        else if (args[0].equals("ProcessLWB1")){
            new LightWeight(args[0]);
        }   //else-if
        else if(args[0].equals("ProcessLWB1")){
            new LightWeight(args[0]);
        }   //else-if
    }
}
