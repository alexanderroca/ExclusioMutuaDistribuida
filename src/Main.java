import ProcessB.HeavyWeight;
import ProcessB.LightWeight;

public class Main {

    public static void main (String args[]){

        if(args[0].equals("ProcessB")) {
            HeavyWeight heavyWeightB = new HeavyWeight(args[0], 3000);
            heavyWeightB.run();
        }   //if
        else if (args[0].equals("ProcessLWB1")){
            LightWeight lwb1 = new LightWeight(args[0], 3001, 1);
            lwb1.run();
        }   //else-if
        else if(args[0].equals("ProcessLWB2")){
            LightWeight lwb2 = new LightWeight(args[0], 3002, 2);
            lwb2.run();
        }   //else-if
    }
}
