package ProcessB;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class LightWeight implements Runnable{

    private int myID;
    private String name = "";
    private String heavyProcessName;
    private int PORT;
    private Timestamp token;
    private Socket clientSocket;

    public LightWeight(String name, int port, int myID) {

        this.name = name;
        PORT = port;
        this.myID = myID;
    }

    public void lightWeightProcess () {

        connectToHeavyWeight();

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

    private void connectToHeavyWeight(){

        InetAddress address;
        try {
            address = InetAddress.getLocalHost();

            try {

                int port = 3000;  //TODO: insert port from HeavyWeight
                clientSocket = new Socket(address, port);
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

                oos.writeObject(name);

                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

                String message = (String) ois.readObject();
                System.out.println(message);

                heavyProcessName = message;

            } catch (IOException e) {
                e.printStackTrace();
                System.err.print("IO Exception");
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void  waitHeavyWeight(){

        try {

            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

            token = new Timestamp(System.currentTimeMillis());
            oos.writeObject(token);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.print("IO Exception");
        }
    }

    private void requestCS(){

    }

    private void espera1Segon() {
        try {
            TimeUnit.MINUTES.sleep(1);
        } catch(InterruptedException e){
            e.printStackTrace();
        }
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
