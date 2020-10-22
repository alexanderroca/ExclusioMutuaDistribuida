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
    private ArrayList<Socket> lightsConnectedTo;
    private boolean opened;

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
        this.lightPorts = lightPorts;
        opened = false;
        lightsConMe = new ArrayList<Socket>();
        requestQueue = new int[lightQuantity];
        lightsConnectedTo = new ArrayList<Socket>();
        clock = new DirectClock(lightQuantity, id);
        lightSocketServer = new LightSocketServer(myPort);

        
    }

    public void killLightweight(){
        this.interrupt();
    }

    @Override
    public synchronized void run(){


        Log.logMessage(identifier + " port: " + myPort + " exists", "INFO",
                "LAMPORT", "LIGHT", identifier);
        connectToHeavy();
        connectToLightweights();

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

    private synchronized void notifyHeavyWeight(){


    }


    private void waitOneSec(){

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Log.logMessage("error when waiting 1 second", "ERROR", "LAMPORT", "LIGHT", identifier);
        }

    }


    private synchronized void releaseCS(){


    }

    private synchronized void requestCS(){

        clock.tick();
        requestQueue[id] = clock.getClock(id);
        sendBroadcastRequest();
        waitTurn();

    }

    private synchronized void waitTurn(){

        while(true){


        }

    }

    private synchronized void sendBroadcastRequest(){

        for (Socket socket : lightsConnectedTo){

            try {
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                LamportMessage message = new LamportMessage(clock.getClock(id), id, "request");
                oos.writeObject(message);

            } catch (IOException e) {

            }

        }

    }


    private void waitHeavyweight(){

        while (!opened);

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
        public synchronized void run(){

            Log.logMessage(identifier + " started listener", "INFO", "LAMPORT", "LIGHT", identifier);

            while(true){

                try {
                    LamportMessage auxLM = (LamportMessage) ois.readObject();

                    switch (auxLM.getType()){

                        //request
                        case 1:

                            //add request to queue
                            requestQueue[auxLM.getId()] = auxLM.getTimestamp();

                            //update timestamp of sender
                            clock.catchUp(auxLM.getId(), auxLM.getTimestamp());
                            Log.logMessage("Request received", "INFO", "LAMPORT", "LIGHT", identifier);
                            break;

                        //release
                        case 2:

                            //update timestamp of sender
                            clock.catchUp(auxLM.getId(), auxLM.getTimestamp());
                            Log.logMessage("Release received", "INFO", "LAMPORT", "LIGHT", identifier);
                            break;

                        //acknowledge
                        case 3:

                            //update timestamp of sender
                            clock.catchUp(auxLM.getId(), auxLM.getTimestamp());
                            Log.logMessage("Acknowledge received", "INFO", "LAMPORT", "LIGHT", identifier);
                            break;
                    }


                } catch (IOException e) {
                    Log.logMessage("Can't read object, IO exception", "ERROR", "LAMPORT", "LIGHT", identifier);
                } catch (ClassNotFoundException e) {
                    Log.logMessage("Can't read object, class not found exception", "ERROR", "LAMPORT", "LIGHT", identifier);
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
        public synchronized void run(){

            Log.logMessage(identifier + " server started", "INFO",
                    "LAMPORT", "LIGHT", identifier);

            while(serverStatus){

                try {

                    Socket auxSocket = serverSocket.accept();
                    lightsConMe.add(auxSocket);
                    LightSocketListener auxListener = new LightSocketListener(auxSocket);
                    Log.logMessage(identifier +". Lightweight with port: " + auxSocket.toString() + " has connected to me",
                            "INFO", "LAMPORT", "LIGHT", identifier);

                } catch (IOException e) {
                    Log.logMessage(identifier + "Error when accepting incoming light connection",
                            "ERROR", "LAMPORT", "LIGHT", identifier);
                    e.printStackTrace();
                }

            }

        }


        public void shutDownServer(){

            serverStatus = false;
        }

    }

    private synchronized void connectToLightweights(){

        boolean connected = false;

        for (int i = 0; i < lightQuantity; i++ ){

            //avoid connecting to myself
            if(i != id){

                connected = false;
                while(!connected){

                    try {

                        Socket auxSocket = new Socket(myAddress.getHostName(), lightPorts[i]);
                        lightsConnectedTo.add(auxSocket);
                        connected = true;
                        Log.logMessage(identifier + " connected to lightweight: " + lightPorts[i],
                                "INFO", "LAMPORT", "LIGHT", identifier);

                    }catch (IOException e){
                        Log.logMessage(identifier + " couldn't connect to lightweight: " + i, "ERROR",
                                "LAMPORT", "LIGHT", identifier);
                    }

                }

            }
            
        }

    }


    private synchronized void connectToHeavy(){

        boolean connected = false;
        while(!connected){

            try {
                heavySocket = new Socket(heavyAddress.getHostName(), heavyPort);
                connected = true;

                //TODO missing generation of thread to listen the heavyweight

                Log.logMessage(identifier + " connected to heavyweight with destination port: " + heavySocket.getPort()
                        + " and exit port: " + heavySocket.getLocalPort(), "INFO", "LAMPORT",
                        "LIGHT", identifier);
            } catch (IOException e) {
                Log.logMessage(identifier + " port: " + myPort + "cant connect to heavy, retrying...",
                        "ERROR", "LAMPORT", "LIGHT", identifier);
            }

        }

    }

    //TODO Missing lightListens heavy

}
