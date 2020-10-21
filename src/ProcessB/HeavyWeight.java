package ProcessB;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class HeavyWeight implements Runnable{

    private String name;
    private final int NUM_LIGHTWEIGHTS = 2; //ProcessB.HeavyWeight for Ricart & Agrawala produce 2 threads
    private int token;
    private int PORT;
    private ServerSocket serverSocket;
    private Socket[] process;
    private int i = 0;

    public HeavyWeight(String name, int port) {

        this.name = name;
        PORT = port;
        process = new Socket[NUM_LIGHTWEIGHTS];

        try {

            serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenTwoClients(){

        while(i < 2) {
            try {

                Socket petition = serverSocket.accept();

                process[i] = petition;
                i++;

                ObjectInputStream ois = new ObjectInputStream(petition.getInputStream());

                String message = (String) ois.readObject();
                System.out.println(message);

            } catch (SocketTimeoutException s) {
                System.out.println("Socket timed out!");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }   //while
    }

    public void heavyWeightProcess(){

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
    }

    private void listenHeavyweight(){
        listenTwoClients();
    }

    private void sendActionToLightweight(){

    }

    private void  listenLightweight(){

    }

    private void sendTokenToHeavyweight(){

    }

    @Override
    public void run() {
        heavyWeightProcess();
    }
}
