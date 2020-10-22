package Lamport;

public class DirectClock {

    public int[] clock;
    int myId;

    public DirectClock(int numProc, int id){
        myId = id;
        clock = new int[numProc];

        for (int i = 0; i < numProc; i++){
            clock[i] = 1;
        }

    }

    public int getClock(int i){
        return clock[i];
    }

    public void tick(){
        clock[myId]++;
    }

    public void catchUp(int idSender, int clockSender){

        clock[idSender] = clockSender > clock[idSender] ? clockSender : clock[idSender];
        clock[myId] = (clock[myId] > clockSender ? clock[myId] : clockSender) + 1;

    }


}
