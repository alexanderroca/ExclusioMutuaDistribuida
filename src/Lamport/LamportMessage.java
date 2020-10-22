package Lamport;

import java.io.Serializable;

public class LamportMessage implements Serializable {

    private final int clock;
    private final int id;
    private final int type; // 1:request    2:release   3:acknowledge


    public LamportMessage(int clock, int id, String type){

        this.clock = clock;
        this.id = id;

        switch (type){

            case "request":
                this.type = 1;
                break;

            case "acknowledge":
                this.type = 3;
                break;

            case "release":
                this.type = 2;
                break;

            default:
                this.type = -1;
        }


    }


    public int getTimestamp() {
        return clock;
    }


    public int getId() {
        return id;
    }


    public int getType() {
        return type;
    }


}
