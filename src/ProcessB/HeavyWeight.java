package ProcessB;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class HeavyWeight extends Thread {

    protected Socket socket;
    protected String processName;
    protected String heavyProcessName;
    private final int NUM_LIGHTWEIGHTS = 2; //ProcessB.HeavyWeight for Ricart & Agrawala produce 2 threads
    private int token;

    public HeavyWeight(Socket clientSocket, String processName, String heavyProcessName) {
        this.socket = clientSocket;
        this.processName = processName;
        this.heavyProcessName = heavyProcessName;
    }

    public void run() {
        heavyWeightProcess();
    }

    public void heavyWeightProcess(){

        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            oos.writeObject(heavyProcessName);

            int answersfromLightweigth = 0;
            token = -1;

            while(true){

                while(token != 0) listenHeavyweight();  //TODO: mirar token

                for (int i=0; i<NUM_LIGHTWEIGHTS; i++)
                    sendActionToLightweight();

                while(answersfromLightweigth < NUM_LIGHTWEIGHTS)
                    listenLightweight();

                token = 0;
                sendTokenToHeavyweight();
            }   //while
        } catch (IOException e) {
            e.printStackTrace();
            System.err.print("IO Exception");
        }
    }

    private void listenHeavyweight(){

    }

    private void sendActionToLightweight(){

    }

    private void  listenLightweight(){

    }

    private void sendTokenToHeavyweight(){

    }
}