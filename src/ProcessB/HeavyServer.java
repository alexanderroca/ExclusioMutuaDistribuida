package ProcessB;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class HeavyServer implements Runnable{

    private String name;
    private int token;
    private int PORT;
    private ServerSocket serverSocket;

    public HeavyServer(String name, int port) {

        this.name = name;
        PORT = port;

        try {

            serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenClients(){

        while (true) {
            try {

                Socket petition = serverSocket.accept();

                ObjectInputStream ois = new ObjectInputStream(petition.getInputStream());

                String message = (String) ois.readObject();
                System.out.println(message);

                new HeavyWeight(petition, message, name).start();

            } catch (SocketTimeoutException s) {
                System.out.println("Socket timed out!");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }   //while

    }

    @Override
    public void run() {
        listenClients();
    }
}
