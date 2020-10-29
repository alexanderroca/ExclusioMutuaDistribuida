import ProcessB.RicartAgrawalaHeavyweight;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainRicartAgrawalaHeavy {

    //HEAVY PORTS 3330, 3331
    //LIGHT PORTS 4440, 4441, 4442, 5550, 5551, 5552
    public static void main (String args[]){

        int numLights = 2;
        int baseRicartAgrawalaHeavyPort = 4440;

        try {

            int[] ricartAgrawalaLightPorts = new int[numLights];
            for (int i = 0; i < numLights; i++){
                ricartAgrawalaLightPorts[i] = baseRicartAgrawalaHeavyPort + i;
            }

            RicartAgrawalaHeavyweight ricartAgrawalaHeavyweight = null;

            ricartAgrawalaHeavyweight = new RicartAgrawalaHeavyweight(2, true, 3330,
                    ricartAgrawalaLightPorts, InetAddress.getLocalHost(), InetAddress.getLocalHost());

            ricartAgrawalaHeavyweight.start();


        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

}