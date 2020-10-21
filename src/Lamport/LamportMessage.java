package Lamport;

import java.io.Serializable;

public class LamportMessage implements Serializable {

    private final int clock;
    private final int id;
    private final int type; // 1:request    2:release   3:acknowledge


    public LamportMessage(int clock, int id, int type){

        this.clock = clock;
        this.id = id;
        this.type = type;

    }


    public int getClock() {
        return clock;
    }


    public int getId() {
        return id;
    }


    public int getType() {
        return type;
    }


}
