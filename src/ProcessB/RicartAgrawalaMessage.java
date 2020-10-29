package ProcessB;

import java.io.Serializable;

public class RicartAgrawalaMessage implements Serializable {

    private final int clock;
    private final int id;
    private final int type; // 1:request    2:release   3:acknowledge   -1:heavyeight


    public RicartAgrawalaMessage(int clock, int id, String type){

        this.clock = clock;
        this.id = id;

        switch (type){

            case "handshake":
                this.type = 0;
                break;

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