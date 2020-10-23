package Lamport;

public class DirectClock {

    public volatile int[] clock;
    private int myId;

    public DirectClock(int numProc, int id){
        myId = id;
        clock = new int[numProc];

        for (int i = 0; i < numProc; i++){
            clock[i] = 1;
        }

    }

    public synchronized int getClock(int i){
        return clock[i];
    }

    public synchronized void tick(){
        clock[myId]++;
    }

    public synchronized void catchUp(int idSender, int clockSender){
        clock[idSender] = Math.max(clockSender, clock[idSender]);
        clock[myId] = (Math.max(clock[myId], clockSender)) + 1;

    }


}
