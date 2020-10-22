import Lamport.LamportHeavyweight;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainLamportHeavy {

    //HEAVY PORTS 3330, 3331
    //LIGHT PORTS 4440, 4441, 4442, 5550, 5551, 5552
    public static void main (String args[]){

        int numLights = 3;
        int baseLamportHeavyPort = 4440;

        try {

            int[] lamportLightPorts = new int[numLights];
            for (int i = 0; i < numLights; i++){
                lamportLightPorts[i] = baseLamportHeavyPort + i;
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
