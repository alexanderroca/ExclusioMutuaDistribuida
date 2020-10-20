import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class LamportLightweight extends Thread{

    private int[] lightPorts;
    private int heavyPort;
    private InetAddress heavyAddress;
    private InetAddress myAddress;
    private int lightQuantity;
    private int myPort;
    private Socket heavySocket;
    private String identifier;

    public LamportLightweight(int[] lightPorts, int lightQuantity, int myPort, InetAddress heavyAddress, InetAddress myAddress, int heavyPort, String identifier){

        this.myPort = myPort;
        this.lightQuantity = lightQuantity;
        this.heavyAddress = heavyAddress;
        this.myAddress = myAddress;
        this.heavyPort = heavyPort;
        this.identifier = identifier;
        //this.start();
        
    }

    public void killLightweight(){
        this.interrupt();
    }

    @Override
    public void run(){

        try{

            Log.logMessage("LAMPORT: lightweight with port: " + myPort + " exists");
            connectToHeavy();

            /*
            while(true){


            }
            */
            heavySocket.close();
        }catch (IOException e){
            Log.logMessage("ERROR: LAMPORT lightweight port: " + myPort);

        }

    }

    private void connectToLightweights(){

        for (int i = 0; i < lightQuantity; i++ ){

            //avoid connecting to myself
            
        }

    }

    private void connectToHeavy() throws IOException {
        heavySocket = new Socket(heavyAddress.getHostName(), heavyPort);
        Log.logMessage("LAMPORT: " + identifier + " connecting to heavyweight with exit port: " + heavySocket.getPort()
                + " and destination port: " + heavySocket.getLocalPort());
    }



}
