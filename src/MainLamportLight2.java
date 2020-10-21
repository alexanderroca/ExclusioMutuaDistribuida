import Lamport.LamportLightweight;
import Lamport.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainLamportLight2 {

    public static void main(String[] args) {
        int numLights = 3;
        int heavyPort = 3330;
        int baseLamportPort = 4440;

        int idLight = 1;

        int[] lamportLightPorts = new int[numLights];

        for (int i = 0; i < numLights; i++){
            lamportLightPorts[i] = baseLamportPort + i;
        }

        try{


            LamportLightweight instance = new LamportLightweight(lamportLightPorts, numLights, lamportLightPorts[idLight],
                    InetAddress.getLocalHost(), InetAddress.getLocalHost(), heavyPort, "lightLamport_" + String.valueOf(idLight), idLight);
            instance.start();

        }catch (UnknownHostException e){
            Log.logMessage("", "ERROR", "LAMPORT", "LIGHT");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
