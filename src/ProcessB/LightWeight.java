package ProcessB;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class LightWeight implements Runnable{

    private int myID;
    private String name = "";
    private int PORT;

    public LightWeight(String name, int port, int myID) {

        this.name = name;
        PORT = port;
        this.myID = myID;
    }

    public void lightWeightProcess () {
        myID = -1; //TODO: Mirar myID

        while(true){
            waitHeavyWeight();
            requestCS();
            for (int i=0; i<10; i++){
                System.out.println("Sóc el procés lightweight " +  myID);
                espera1Segon();
            }
            releaseCS();
            notifyHeavyWeight();
        }
    }

    private void  waitHeavyWeight(){

        InetAddress address;
        try {
            address = InetAddress.getLocalHost();

            try {

                int port = 3000;  //TODO: insert port from HeavyWeight
                Socket clientSocket = new Socket(address, port);
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

                oos.writeObject("LightWeight: " + name + " connected to HeavyWeight-ProcessB");

            } catch (IOException e) {
                e.printStackTrace();
                System.err.print("IO Exception");
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void requestCS(){

    }

    private void espera1Segon(){

    }

    private void  releaseCS(){

    }

    private void notifyHeavyWeight(){

    }

    @Override
    public void run() {
        lightWeightProcess();
    }
}
