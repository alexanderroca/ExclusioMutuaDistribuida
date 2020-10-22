package Lamport;

import java.io.Serializable;

public class LamportHeavyMessage implements Serializable {

    private boolean enable;

    public LamportHeavyMessage(boolean enable){

        this.enable = enable;

    }

}
