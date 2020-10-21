import Lamport.LamportHeavyweight;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {

    //HEAVY PORTS 3330, 3331
    //LIGHT PORTS 4440, 4441, 4442, 5550, 5551, 5552
    public static void main (String args[]){

        int numLamports = 3;
        int baseLamportPort = 4440;

        //Lamport.Log.logMessage("Preparing heavyweights");
        try {

            int[] lamportLightPorts = new int[numLamports];
            for (int i = 0; i < numLamports; i++){
                lamportLightPorts[i] = baseLamportPort + i;
            }

            LamportHeavyweight lamportHeavyweight = null;

            lamportHeavyweight = new LamportHeavyweight(3, true, 3330,
                    lamportLightPorts, InetAddress.getLocalHost(), InetAddress.getLocalHost());

            lamportHeavyweight.start();


        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


    }
}
