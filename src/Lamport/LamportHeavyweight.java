package Lamport;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class LamportHeavyweight extends Thread{

    private boolean token = true;
    private int numLightweights;
    private int answersFromLightweight;
    private boolean debug = true;
    private int port;
    private int[] lightPorts;
    private HeavySocketServer heavySocketServer;
    private InetAddress heavyAddress;
    private InetAddress lightAddress;


    private ArrayList<Socket> lightsConMe;
    private ArrayList<HeavyListensLight> toLightListeners;

    private volatile int lightsConnected;

    public LamportHeavyweight(int numLightweights, boolean debug, int port, int[] lightPorts, InetAddress heavyAddress, InetAddress lightAddress){

        lightsConnected = 0;

        this.numLightweights = numLightweights;
        this.debug = debug;
        this.port = port;
        this.lightPorts = lightPorts;
        this.heavyAddress = heavyAddress;
        this.lightAddress = lightAddress;
        lightsConMe = new ArrayList<>();
        toLightListeners = new ArrayList<>();

    }


    @Override
    public synchronized void run(){

        //connect to the other heavy process
        heavyConnectHeavy();
        try {
            heavySocketServer = new HeavySocketServer(port);
            Log.logMessage("Heavy socket server created", "INFO", "LAMPORT", "HEAVY");
        } catch (IOException e) {
            e.printStackTrace();
            Log.logMessage("cant create heavy socket server", "ERROR", "LAMPORT", "HEAVY");
        }

        //invokeLightweights();
        startLamportHeavy();

    }


    private void heavyConnectHeavy(){


    }


    private class HeavySocketServer extends Thread{

        private int port;
        private volatile boolean serverStatus;
        private ServerSocket serverSocket;

        public HeavySocketServer(int port) throws IOException {

            this.port = port;
            serverStatus = true;
            serverSocket = new ServerSocket(port);
            this.start();
        }

        @Override
        public synchronized void run(){

            while(serverStatus){

                try {

                    Socket auxSocket = serverSocket.accept();
                    HeavyListensLight auxHeavyListensLight = new HeavyListensLight(auxSocket);
                    toLightListeners.add(auxHeavyListensLight);
                    lightsConMe.add(auxSocket);
                    lightsConnected++;
                    Log.logMessage("Lightweight with port: " + auxSocket.toString() + " has connected to me, num connected: " + lightsConMe.size(),
                            "INFO", "LAMPORT", "HEAVY");

                } catch (IOException e) {

                    Log.logMessage("Error when accepting incoming light connection",
                            "ERROR", "LAMPORT", "HEAVY");

                }

            }

        }


        //Turns off the server
        public void shutDownServer(){

            serverStatus = false;

        }

    }


    private class HeavyListensLight extends Thread{

        private Socket socket;
        private ObjectInputStream ois;

        public HeavyListensLight(Socket socket){

            this.socket = socket;
            this.start();

        }

        @Override
        public void run(){

            try {

                ois = new ObjectInputStream(socket.getInputStream());

                //listen socket input
                while(true){

                    try {
                        LamportMessage auxLM = (LamportMessage) ois.readObject();
                        Log.logMessage("message from: " + auxLM.getId() + " received", "INFO", "LAMPORT", "HEAVY");
                    } catch (IOException e) {
                        Log.logMessage("error reading object from lightweight, IO exception", "ERROR", "LAMPORT",
                                "HEAVY");
                    } catch (ClassNotFoundException e) {
                        Log.logMessage("error reading object class not found exception",
                                "ERROR", "LAMPORT", "HEAVY");
                    }

                }

            } catch (IOException e) {
                Log.logMessage("error when gathering input stream from socket connection from light",
                        "ERROR", "LAMPORT", "HEAVY");
            }

        }

    }
    public synchronized void waitForLightsToConnect(){
         while (lightsConnected < numLightweights);


    }


    public synchronized void startLamportHeavy(){

        while(true){

            waitForLightsToConnect();

            //Waiting for token between heavyweights
            while(!token){
                listenHeavyweight();
            }

            //green light lightweights
            for (int i = 0; i < lightsConMe.size(); i++){

                Socket auxSocketLight = lightsConMe.get(i);
                sendActionToLightweight(auxSocketLight, i);

            }

            //Waiting for all lightweights to finish
            while(answersFromLightweight < numLightweights){
                listenLightweight();
            }

            //Give access token to other heavyweight
            token = debug ? false : true;
            sendTokenToHeavyweight();

        }

    }

    //Previous code, thread invocation
    /*
    private void invokeLightweights(){

        Log.logMessage("Invoking lightweights", "INFO", "LAMPORT", "HEAVY");

        try{

            for(int i = 0; i < numLightweights; i++){

                LamportLightweight instance = new LamportLightweight(lightPorts, numLightweights, lightPorts[i],
                        heavyAddress, lightAddress, port, "lightLamport_" + String.valueOf(i), i);
                lamportLightweights.add(instance);
                instance.start();

            }

        }catch (IOException e){
            Log.logMessage("Cant create lightLamport", "ERROR", "LAMPORT", "HEAVY");
        }

    }
    */

    //HEAVYWEIGHT

    private void listenHeavyweight(){

        token = true;

    }


    private void sendTokenToHeavyweight(){



    }




    //LIGHTWEIGHT

    private void listenLightweight(){



    }

    //Green lights lightweights
    private void sendActionToLightweight(Socket lightConnSocket, int id){

        boolean send = false;
        while(!send){

            try {

                    ObjectOutputStream oos = new ObjectOutputStream(lightConnSocket.getOutputStream());
                    LamportLightHeavyMessage auxLHM = new LamportLightHeavyMessage(true);
                    oos.writeObject(auxLHM);
                    send = true;
                    Log.logMessage("gate opening message sent to " + id , "INFO",
                            "LAMPORT", "HEAVY");

            } catch (IOException e) {
                Log.logMessage("couldn't send message to lightweight: " + id, "ERROR", "LAMPORT",
                        "HEAVY");
            }

        }

    }


}
