import java.net.InetAddress;
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


    public LamportHeavyweight(int numLightweights, boolean debug, int port, int[] lightPorts, InetAddress heavyAddress, InetAddress lightAdress){

        this.numLightweights = numLightweights;
        this.debug = debug;
        lamportLightweights = new ArrayList<>();
        this.port = port;
        this.lightPorts = lightPorts;
        this.heavyAddress = heavyAddress;
        this.lightAddress = lightAdress;

    }


    @Override
    public void run(){

        //connect to the other heavy process
        heavyConnectHeavy();
        heavySocketServer = new HeavySocketServer(port);
        invokeLightweights();
        startLamportHeavy();

    }

    private void heavyConnectHeavy(){


    }


    private class HeavySocketServer extends Thread{

        private int port;
        private volatile boolean serverStatus;

        public HeavySocketServer(int port){

            this.port = port;
            serverStatus = true;
            this.start();
        }

        @Override
        public void run(){

            while(serverStatus){



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

        System.out.println("LAMPORT: Invoking lightweights");

        for(int i = 0; i < numLightweights; i++){

            LamportLightweight instance = new LamportLightweight(lightPorts, numLightweights, lightPorts[i], heavyAddress, lightAddress);
            lamportLightweights.add(instance);
            instance.start();

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
