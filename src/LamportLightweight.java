import java.net.InetAddress;
import java.net.Socket;

public class LamportLightweight extends Thread{

    private int[] lightPorts;
    private int heavyPort;
    private InetAddress heavyAddress;
    private InetAddress myAddress;
    private int lightQuantity;
    private int myPort;
    private Socket heavyConnection;

    public LamportLightweight(int[] lightPorts, int lightQuantity, int myPort, InetAddress heavyAddress, InetAddress myAddress){

        this.myPort = myPort;
        this.lightQuantity = lightQuantity;
        this.heavyAddress = heavyAddress;
        this.myAddress = myAddress;

    }

    public void killLightweight(){
        this.interrupt();
    }

    @Override
    public void run(){

        System.out.println("LAMPORT: lightweight with port: " + myPort + " exists");

        while(true){
            

        }

    }

    private void connectToLightweights(){

        for (int i = 0; i < lightQuantity; i++ ){

            //avoid connecting to myself

        }


    }

    private void connectToHeavy(){

    }



}
