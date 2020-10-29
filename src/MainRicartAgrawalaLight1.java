import ProcessB.Log;
import ProcessB.RicartAgrawalaLightweight;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainRicartAgrawalaLight1 {

    public static void main(String[] args) {
        int numLights = 2;
        int heavyPort = 3330;
        int baseRicartAgrawalaPort = 4440;

        int idLight = 1;

        int[] ricartAgrawalaLightPorts = new int[numLights];

        for (int i = 0; i < numLights; i++){
            ricartAgrawalaLightPorts[i] = baseRicartAgrawalaPort + i;
        }

        try{


            RicartAgrawalaLightweight instance = new RicartAgrawalaLightweight(ricartAgrawalaLightPorts, numLights, ricartAgrawalaLightPorts[idLight],
                    InetAddress.getLocalHost(), InetAddress.getLocalHost(), heavyPort, "lightLamport_" + String.valueOf(idLight), idLight);
            instance.start();

        }catch (UnknownHostException e){
            Log.logMessage("", "ERROR", "LAMPORT", "LIGHT");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}