package Lamport;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class LamportLightweight extends Thread{

    private final int[] lightPorts;
    private final int heavyPort;
    private final InetAddress heavyAddress;
    private final InetAddress myAddress;
    private final int lightQuantity;
    private final int myPort;
    private Socket heavySocket;
    private final String identifier;
    private final int id;
    private ArrayList<Socket> lightsConnectedTo; // added but not used
    private volatile boolean opened;
    private final LightSocketServer lightSocketServer;
    private LightListensHeavy lightListensHeavy;
    private ObjectOutputStream[] oosConnectedTo;
    private ObjectOutputStream heavyOos;
    private volatile int connectionCount;

    //LAMPORT
    private int[] requestQueue;
    DirectClock clock;
    private final int QUEUE_NOT_REQUESTED = -1;

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
        connectionCount = 0;
        opened = false;
        requestQueue = new int[lightQuantity];
        for (int i = 0; i < lightQuantity; i++){
            requestQueue[i] = 0;
        }
        lightsConnectedTo = new ArrayList<Socket>(lightQuantity);
        oosConnectedTo = new ObjectOutputStream[lightQuantity];
        clock = new DirectClock(lightQuantity, id);
        lightSocketServer = new LightSocketServer(myPort);
        
    }

    public void killLightweight(){
        this.interrupt();
    }


    //SKELETON
    @Override
    public synchronized void run(){


        Log.logMessage(identifier + " port: " + myPort + " exists", "INFO",
                "LAMPORT", "LIGHT", identifier);
        connectToHeavy();
        connectToLightweights();

        while(!allConnected());

        //Base skeleton
        while(true){

            waitHeavyweight();
            requestCS();

            for (int i=0; i<10; i++){
                System.out.println("Soc el: " + identifier);
                waitOneSec();
            }
            System.out.println("\nFI\n");
            releaseCS();

            notifyHeavyWeight();

        }

    }

    private synchronized boolean allConnected(){
        return connectionCount == (lightQuantity * 2) - 2;
    }

    private synchronized void notifyHeavyWeight(){

        LamportLightHeavyMessage auxLLHM = new LamportLightHeavyMessage(true);
        try {
            heavyOos.writeObject(auxLLHM);
        } catch (IOException e) {
            e.printStackTrace();
        }
        opened = false;

    }


    private void waitOneSec(){

        try {
            //TimeUnit.MILLISECONDS.sleep(100);
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Log.logMessage("error when waiting 1 second", "ERROR", "LAMPORT", "LIGHT", identifier);
        }

    }


    private synchronized void releaseCS(){
        //TODO not sure if this tick is needed
        requestQueue[id] = QUEUE_NOT_REQUESTED;
        sendBroadCastRelease();

    }

    private synchronized void requestCS(){

        clock.tick();
        requestQueue[id] = clock.getClock(id);
        sendBroadcastRequest();
        Log.logMessage("CS requested, waiting for my turn", "INFO", "LAMPORT",
                "LIGHT", identifier);
        while(!myTurn());
        Log.logMessage("Its my turn, CS released for me!", "INFO", "LAMPORT", "LIGHT", identifier);

    }

    private synchronized boolean myTurn(){

        try {
            wait(10);
            printClock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < lightQuantity; i++){
            if (i != id)
                if(iAmGreater(i))
                    return false;

        }

        return true;

    }

    private synchronized boolean iAmGreater(int foreignId){

        //Check if the foreign node hasn't requested the CS
        if(requestQueue[foreignId] == QUEUE_NOT_REQUESTED)
            return false;

        //Check if my request is oldest than the other requests
        if(requestQueue[id] > requestQueue[foreignId] || requestQueue[id] == requestQueue[foreignId] && id > foreignId)
            return true;

        //Check if my request is older than the other clocks
        if(requestQueue[id] > clock.getClock(foreignId) || requestQueue[id] == clock.getClock(foreignId) && id > foreignId)
            return true;

        return false;

    }

    private synchronized void sendBroadCastRelease(){
        clock.tick();
        for (int i = 0; i < lightQuantity; i++){

            if(i != id) {

                try {
                    LamportMessage releaseCsMessage = new LamportMessage(clock.getClock(id), id, "release");
                    oosConnectedTo[i].writeObject(releaseCsMessage);

                } catch (IOException e) {

                    Log.logMessage("couldn't send release message to light", "ERROR", "LAMPORT",
                            "LIGHT", identifier);

                }

            }

        }

        Log.logMessage("all release CS messages sent", "INFO", "LAMPORT", "LIGHT",
                identifier);

    }


    private synchronized void sendBroadcastRequest(){

        for (int i = 0; i < lightQuantity;  i++){

            if(i != id) {

                try {

                    LamportMessage requestCsMessage = new LamportMessage(clock.getClock(id), id, "request");
                    oosConnectedTo[i].writeObject(requestCsMessage);

                } catch (IOException e) {
                    Log.logMessage("can't send broadcast request to: " + i, "ERROR", "LAMPORT",
                            "LIGHT", identifier);
                }

            }

        }

    }


    private synchronized void sendAcknowledge(int foreignId) throws IOException {
        clock.tick();
        LamportMessage aLM = new LamportMessage(clock.getClock(id), id, "acknowledge");
        oosConnectedTo[foreignId].writeObject(aLM);

        Log.logMessage("Acknowledge sent to " + foreignId, "INFO", "LAMPORT",
                "LIGHT", identifier);

    }

    private void waitHeavyweight(){

        while (!opened){
            printClock();
            try {
                wait(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        };

    }

    private class LightSocketListener extends Thread{

        private Socket socket;
        private ObjectInputStream ois;
        //private int listenerId;

        public LightSocketListener(Socket socket) throws IOException {

            this.socket = socket;
            this.start();

        }

        @Override
        public synchronized void run(){


            Log.logMessage(identifier + " started listener", "INFO", "LAMPORT", "LIGHT", identifier);
            try {
                ois = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            connectionCount++;
            while(true){

                try {
                    LamportMessage auxLM = (LamportMessage) ois.readObject();

                    switch (auxLM.getType()){

                        //handshake
                        case 0:
                            Log.logMessage("handshake received", "INFO", "LAMPORT",
                                    "LIGHT", identifier);
                            //listenerId = auxLM.getId();
                            //oosLightConMe[listenerId] = new ObjectOutputStream(socket.getOutputStream());
                            break;

                        //request
                        case 1:

                            //add request to queue
                            requestQueue[auxLM.getId()] = auxLM.getTimestamp();

                            //update timestamp of sender
                            clock.catchUp(auxLM.getId(), auxLM.getTimestamp());

                            //send acknowledge
                            sendAcknowledge(auxLM.getId());

                            //TODO send acknowledge
                            Log.logMessage("Request received from: " + auxLM.getId(), "INFO", "LAMPORT", "LIGHT", identifier);
                            break;

                        //release
                        case 2:

                            //update timestamp of sender
                            clock.catchUp(auxLM.getId(), auxLM.getTimestamp());
                            requestQueue[auxLM.getId()] = -1;
                            Log.logMessage("Release received from: " + auxLM.getId(), "INFO", "LAMPORT", "LIGHT", identifier);

                            break;

                        //acknowledge
                        case 3:

                            //update timestamp of sender
                            clock.catchUp(auxLM.getId(), auxLM.getTimestamp());
                            Log.logMessage("Acknowledge received from: " + auxLM.getId(), "INFO", "LAMPORT", "LIGHT", identifier);
                            break;

                        default:
                    }


                } catch (IOException e) {
                    Log.logMessage("Can't read object, IO exception", "ERROR", "LAMPORT", "LIGHT", identifier);
                    e.printStackTrace();
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

        //TODO NOT USED
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
                    LightSocketListener auxListener = new LightSocketListener(auxSocket);
                    //TODO CUIDADO ESTA MIERDA NO PINTA BIEN
                    ObjectOutputStream auxOos = new ObjectOutputStream(auxSocket.getOutputStream());
                    LamportMessage auxLM = new LamportMessage(0, id, "handshake");
                    auxOos.writeObject(auxLM);
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
                        //TODO HANDSHAKE MIGHT NOT WORK MIERDA!
                        Socket auxSocket = new Socket(myAddress.getHostName(), lightPorts[i]);
                        lightsConnectedTo.add(auxSocket);
                        ObjectOutputStream auxOos = new ObjectOutputStream(auxSocket.getOutputStream());
                        auxOos.writeObject(new LamportMessage(clock.getClock(id), id, "handshake"));
                        ObjectInputStream auxOis = new ObjectInputStream(auxSocket.getInputStream());
                        LamportMessage auxLM = (LamportMessage) auxOis.readObject();

                        oosConnectedTo[auxLM.getId()] = auxOos;
                        connectionCount++;
                        connected = true;
                        Log.logMessage(identifier + " connected to lightweight: " + lightPorts[i],
                                "INFO", "LAMPORT", "LIGHT", identifier);

                    }catch (IOException | ClassNotFoundException e){
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
                lightListensHeavy = new LightListensHeavy(heavySocket);

                heavyOos = new ObjectOutputStream(heavySocket.getOutputStream());
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

    private class LightListensHeavy extends Thread{

        private Socket socket;
        private ObjectInputStream ois;

        public LightListensHeavy(Socket socket){

            this.socket = socket;
            try {
                ois = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.start();


        }

        @Override
        public synchronized void run(){

            while (true){

                try {

                    LamportLightHeavyMessage auxLLHM = (LamportLightHeavyMessage) ois.readObject();
                    opened = auxLLHM.isEnabled();
                    Log.logMessage("Heavy opened the BLACK GATE", "INFO", "LAMPORT", "LIGHT", identifier);

                } catch (IOException e) {
                    Log.logMessage("cant read incoming heavy message, IO exception", "ERROR",
                            "LAMPORT", "LIGHT", identifier);
                    e.printStackTrace();
                    break;
                } catch (ClassNotFoundException e) {
                    Log.logMessage("cant read incoming heavy message, class not found exception", "ERROR",
                            "LAMPORT", "LIGHT", identifier);
                }

            }



        }

    }

    private synchronized void printClock(){

        if(false){

            for (int i = 0; i < lightQuantity; i++){

                System.out.print("(" + clock.getClock(i) + ") ");
            }

            System.out.println();

        }

    }

    //Used for debugging
    private synchronized void printQueue(){

        System.out.println("QUEUE: " + requestQueue[0] + " - " + requestQueue[1] + " - " + requestQueue[2]);

    }

}
