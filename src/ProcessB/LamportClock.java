package ProcessB;

public class LamportClock {
    int c;

    public LamportClock(){
        c = 1;
    }

    public int getValue(){
        return c;
    }
    public void tick(){ //on internal events
        c += 1;
    }
    public void sendAction(){   //include c in message
        c += 1;
    }
    public void receiveAction(int src, int sentValue){
        c = Math.max(c, sentValue) + 1;
    }
}
