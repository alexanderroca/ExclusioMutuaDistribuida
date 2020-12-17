package Lamport;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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

    private ArrayList<ObjectOutputStream> lightOosList;
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
        lightOosList = new ArrayList<>();
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
                    lightOosList.add(new ObjectOutputStream(auxSocket.getOutputStream()));
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
                        LamportLightHeavyMessage auxLM = (LamportLightHeavyMessage) ois.readObject();
                        Log.logMessage("message from light received", "INFO", "LAMPORT", "HEAVY");
                        answersFromLightweight++;
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


    //SKELETON
    public synchronized void startLamportHeavy(){

        waitForLightsToConnect();

        while(true){


            System.out.println("ITERATION");
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
            answersFromLightweight = 0;

            //Give access token to other heavyweight
            token = true;
            sendTokenToHeavyweight();

        }

    }

    //HEAVYWEIGHT

    private void listenHeavyweight(){

        token = true;

    }


    private void sendTokenToHeavyweight(){



    }




    //LIGHTWEIGHT

    private void listenLightweight(){

        try {
            TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    //Green lights lightweights
    private void sendActionToLightweight(Socket lightConnSocket, int id){

        boolean send = false;
        while(!send){

            try {
                    //TODO change creation of outputstream for array of outputstreams
                    //ObjectOutputStream oos = new ObjectOutputStream(lightConnSocket.getOutputStream());
                    LamportLightHeavyMessage auxLHM = new LamportLightHeavyMessage(true);
                    lightOosList.get(id).writeObject(auxLHM);
                    send = true;
                    Log.logMessage("gate opening message sent",  "INFO",
                            "LAMPORT", "HEAVY");

            } catch (IOException e) {
                Log.logMessage("couldn't send message to lightweight: " + id, "ERROR", "LAMPORT",
                        "HEAVY");
            }

        }

    }


}
