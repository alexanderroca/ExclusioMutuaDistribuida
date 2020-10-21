package Lamport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class LamportHeavyweight extends Thread{

    private boolean token = true;
    private int numLightweights;
    private int answersFromLightweight;
    private boolean debug = true;
    private ArrayList<LamportLightweight> lamportLightweights;
    private int port;
    private int[] lightPorts;
    private HeavySocketServer heavySocketServer;
    private InetAddress heavyAddress;
    private InetAddress lightAddress;


    private ArrayList<Socket> lightsConMe;


    public LamportHeavyweight(int numLightweights, boolean debug, int port, int[] lightPorts, InetAddress heavyAddress, InetAddress lightAddress){

        this.numLightweights = numLightweights;
        this.debug = debug;
        lamportLightweights = new ArrayList<>();
        this.port = port;
        this.lightPorts = lightPorts;
        this.heavyAddress = heavyAddress;
        this.lightAddress = lightAddress;
        lightsConMe = new ArrayList<>();

    }


    @Override
    public void run(){

        //connect to the other heavy process
        heavyConnectHeavy();
        try {
            heavySocketServer = new HeavySocketServer(port);
            Log.logMessage("Heavy socket server created", "INFO", "LAMPORT", "HEAVY");
        } catch (IOException e) {
            e.printStackTrace();
            Log.logMessage("cant create heavy socket server", "ERROR", "LAMPORT", "HEAVY");
        }

        invokeLightweights();
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
        public void run(){

            while(serverStatus){

                try {

                    Socket auxSocket = serverSocket.accept();
                    lightsConMe.add(auxSocket);
                    Log.logMessage("Lightweight with port: " + auxSocket.toString() + " has connected to me",
                            "INFO", "LAMPORT", "HEAVY");

                } catch (IOException e) {
                    Log.logMessage("Error when accepting incoming light connection",
                            "ERROR", "LAMPORT", "HEAVY");
                    e.printStackTrace();
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

        public HeavyListensLight(Socket socket){

            this.socket = socket;

        }

        @Override
        public void run(){

            //listen socket input

        }

    }


    public void startLamportHeavy(){

        while(true){

            //Waiting for token between heavyweights
            while(!token){
                listenHeavyweight();
            }

            //green light lightweights
            for (LamportLightweight lightweight : lamportLightweights){
                sendActionToLightweight(lightweight);
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


    //HEAVYWEIGHT

    private void listenHeavyweight(){

        token = debug ? false : true;

    }


    private void sendTokenToHeavyweight(){



    }




    //LIGHTWEIGHT

    private void listenLightweight(){



    }


    private void sendActionToLightweight(LamportLightweight lightweight){


    }


}
