package ProcessB;

public class HeavyWeight {

    private String name;
    private final int NUM_LIGHTWEIGHTS = 2; //ProcessB.HeavyWeight for Ricart & Agrawala produce 2 threads
    private int token;
    private LightWeight[] process;

    public HeavyWeight(String name) {
        this.name = name;
        heavyWeightProcess();
    }

    public void heavyWeightProcess(){

        int answersfromLightweigth = 0;
        token = 0;

        while(true){

            while(token != 0) listenHeavyweight();  //TODO: mirar token

            for (int i=0; i<NUM_LIGHTWEIGHTS; i++)
                sendActionToLightweight();

            while(answersfromLightweigth < NUM_LIGHTWEIGHTS)
                listenLightweight();

            token = 0;
            sendTokenToHeavyweight();
        }   //while
    }

    private void listenHeavyweight(){

    }

    private void sendActionToLightweight(){

    }

    private void  listenLightweight(){

    }

    private void sendTokenToHeavyweight(){

    }
}
