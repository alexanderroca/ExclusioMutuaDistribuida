package ProcessB;

public class LightWeight {

    private int myID;
    private String name = "";

    public LightWeight(String name) {
        this.name = name;
    }

    public void lightWeightProcess () {
        myID = -1; //TODO: Mirar myID

        while(true){
            waitHeavyWeight();
            requestCS();
            for (int i=0; i<10; i++){
                System.out.println("Sóc el procés lightweight %s" +  myID);
                espera1Segon();
            }
            releaseCS();
            notifyHeavyWeight();
        }
    }

    private void  waitHeavyWeight(){

    }

    private void requestCS(){

    }

    private void espera1Segon(){

    }

    private void  releaseCS(){

    }

    private void notifyHeavyWeight(){

    }

}
