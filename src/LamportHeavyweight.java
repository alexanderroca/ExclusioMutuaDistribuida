import java.security.SecureRandom;

public class LamportHeavyweight {

    private boolean token = true;
    private int numLightweights;
    private int answersFromLightweight;


    public LamportHeavyweight(int numLightweights){

        this.numLightweights = numLightweights;


    }

    public void startLamportHeavy(){

        while(true){

            //Waiting for token between heavyweights
            while(!token){
                listenHeavyweight();
            }

            //Sending messages to lightweights
            for (int i = 0; i < numLightweights; i++){
                sendActionToLightweight();
            }

            //Waiting acknowledgments
            while(answersFromLightweight < numLightweights){
                listenLightweight();
            }

            //Give access token to other heavyweight
            token = false;
            sendTokenToHeavyweight();

        }

    }


    //HEAVYWEIGHT

    private void listenHeavyweight(){



    }

    private void sendTokenToHeavyweight(){



    }




    //LIGHTWEIGHT

    private void listenLightweight(){



    }

    private void sendActionToLightweight(){


    }




}
