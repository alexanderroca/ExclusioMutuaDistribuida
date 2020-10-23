import ProcessB.HeavyServer;
import ProcessB.LightWeight;

public class Main {

    public static void main (String args[]){

        if(args[0].equals("ProcessB")) {
            HeavyServer heavyWeightB = new HeavyServer(args[0], 3000);
            LightWeight loopback = new LightWeight(args[0], 3000, 0);
            Thread server = new Thread(heavyWeightB);
            Thread client = new Thread(loopback);
            server.start();
            client.start();
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
