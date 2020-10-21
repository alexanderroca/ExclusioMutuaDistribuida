import com.sun.security.ntlm.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class LamportLightweight extends Thread{

    private int[] lightPorts;
    private int heavyPort;
    private InetAddress heavyAddress;
    private InetAddress myAddress;
    private int lightQuantity;
    private int myPort;
    private Socket heavySocket;
    private String identifier;
    private LightSocketServer lightSocketServer;
    private ArrayList<Socket> lightsConMe;

    public LamportLightweight(int[] lightPorts, int lightQuantity, int myPort, InetAddress heavyAddress,
                              InetAddress myAddress, int heavyPort, String identifier) throws IOException {

        this.myPort = myPort;
        this.lightQuantity = lightQuantity;
        this.heavyAddress = heavyAddress;
        this.myAddress = myAddress;
        this.heavyPort = heavyPort;
        this.identifier = identifier;
        lightSocketServer = new LightSocketServer(myPort);
        
    }

    public void killLightweight(){
        this.interrupt();
    }

    @Override
    public void run(){

        try{

            Log.logMessage(identifier + " port: " + myPort + " exists", "INFO",
                    "LAMPORT", "LIGHT");
            connectToHeavy();

            //SKELETON GOES HERE
            while(true){


            }

            //heavySocket.close();
        }catch (IOException e){
            Log.logMessage(identifier + " port: " + myPort, "ERROR", "LAMPORT", "LIGHT");

        }

    }

    //Accepts incoming connections from other lightweights
    private class LightSocketServer extends Thread{

        private int port;
        private boolean serverStatus;
        private ServerSocket serverSocket;


        public LightSocketServer(int port) throws IOException {

            this.port = port;
            serverStatus = true;
            serverSocket = new ServerSocket(port);
            this.start();

        }


        @Override
        public void run(){

            Log.logMessage(identifier + " server started", "INFO",
                    "LAMPORT", "LIGHT");

            while(serverStatus){

                try {

                    Socket auxSocket = serverSocket.accept();
                    lightsConMe.add(auxSocket);
                    Log.logMessage(identifier +". Lightweight with port: " + auxSocket.toString() + " has connected to me",
                            "INFO", "LAMPORT", "LIGHT");

                } catch (IOException e) {
                    Log.logMessage(identifier + "Error when accepting incoming light connection",
                            "ERROR", "LAMPORT", "LIGHT");
                    e.printStackTrace();
                }

            }

        }


        public void shutDownServer(){

            serverStatus = false;
        }

    }

    private void connectToLightweights(){

        for (int i = 0; i < lightQuantity; i++ ){

            //avoid connecting to myself
            
        }

    }


    private void connectToHeavy() throws IOException {
        heavySocket = new Socket(heavyAddress.getHostName(), heavyPort);
        Log.logMessage(identifier + " connecting to heavyweight with destination port: " + heavySocket.getPort()
                + " and exit port: " + heavySocket.getLocalPort(), "INFO", "LAMPORT", "LIGHT");
    }


}
