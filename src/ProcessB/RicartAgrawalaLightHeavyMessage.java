package ProcessB;

import java.io.Serializable;

public class RicartAgrawalaLightHeavyMessage implements Serializable {

    private boolean enable;

    public RicartAgrawalaLightHeavyMessage(boolean enable){

        this.enable = enable;

    }

    public boolean isEnabled(){
        return enable;
    }

}
