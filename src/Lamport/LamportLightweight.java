package Lamport;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class LamportLightweight extends Thread{

    private int[] lightPorts;
    private int heavyPort;
    private InetAddress heavyAddress;
    private InetAddress myAddress;
    private int lightQuantity;
    private int myPort;
    private Socket heavySocket;
    private String identifier;
    private int id;
    private LightSocketServer lightSocketServer;
    private ArrayList<Socket> lightsConMe;

    //LAMPORT
    private int[] requestQueue;
    DirectClock clock;

    public LamportLightweight(int[] lightPorts, int lightQuantity, int myPort, InetAddress heavyAddress,
                              InetAddress myAddress, int heavyPort, String identifier, int id) throws IOException {

        this.myPort = myPort;
        this.lightQuantity = lightQuantity;
        this.heavyAddress = heavyAddress;
        this.myAddress = myAddress;
        this.heavyPort = heavyPort;
        this.identifier = identifier;
        this.id = id;
        requestQueue = new int[lightQuantity];
        clock = new DirectClock(lightQuantity, id);
        lightSocketServer = new LightSocketServer(myPort);
        
    }

    public void killLightweight(){
        this.interrupt();
    }

    @Override
    public void run(){


        Log.logMessage(identifier + " port: " + myPort + " exists", "INFO",
                "LAMPORT", "LIGHT");
        connectToHeavy();

        //Base skeleton
        while(true){

            waitHeavyweight();
            requestCS();

            for (int i=0; i<10; i++){
                System.out.println("Soc el " + identifier);
                waitOneSec();
            }

            releaseCS();
            notifyHeavyWeight();

        }


    }

    private void notifyHeavyWeight(){


    }


    private void waitOneSec(){

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Log.logMessage("error when waiting 1 second", "ERROR", "LAMPORT", "LIGHT");
        }

    }


    private synchronized void releaseCS(){


    }

    private synchronized void requestCS(){

        clock.tick();
        requestQueue[id] = clock.getClock(id);
        sendBroadcastRequest();

    }

    private synchronized void sendBroadcastRequest(){


    }


    private void waitHeavyweight(){


    }

    private class LightSocketListener extends Thread{

        private Socket socket;
        private ObjectInputStream ois;
        private ObjectOutput oos;

        public LightSocketListener(Socket socket) throws IOException {

            this.socket = socket;
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            this.start();
        }

        @Override
        public void run(){

            while(true){

                try {
                    LamportMessage auxLM = (LamportMessage) ois.readObject();

                    switch (auxLM.getType()){

                        //request
                        case 1:
                            break;

                        //release
                        case 2:
                            break;

                        //acknowledge
                        case 3:
                            break;
                    }


                } catch (IOException e) {
                    Log.logMessage("Can't read object", "ERROR", "LAMPORT", "LIGHT");
                } catch (ClassNotFoundException e) {
                    Log.logMessage("", "", "", "");
                }

            }

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
                    //missing opening connection thread
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


    private void connectToHeavy(){

        boolean connected = false;
        while(!connected){

            try {
                heavySocket = new Socket(heavyAddress.getHostName(), heavyPort);
                connected = true;
                Log.logMessage(identifier + " connecting to heavyweight with destination port: " + heavySocket.getPort()
                        + " and exit port: " + heavySocket.getLocalPort(), "INFO", "LAMPORT", "LIGHT");
            } catch (IOException e) {
                Log.logMessage(identifier + " port: " + myPort + "cant connect to heavy, retrying...", "ERROR", "LAMPORT", "LIGHT");
            }

        }

    }

}
