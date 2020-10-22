package Lamport;

import java.io.Serializable;

public class LamportLightHeavyMessage implements Serializable {

    private boolean enable;

    public LamportLightHeavyMessage(boolean enable){

        this.enable = enable;

    }

    public boolean isEnabled(){
        return enable;
    }

}
