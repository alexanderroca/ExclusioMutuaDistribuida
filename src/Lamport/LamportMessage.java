package Lamport;

import java.io.Serializable;

public class LamportMessage implements Serializable {

    private final int clock;
    private final int id;


    public LamportMessage(int clock, int id){

        this.clock = clock;
        this.id = id;
        
    }

}
