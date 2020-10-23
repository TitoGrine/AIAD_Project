package grid.behaviour;

import grid.ChargingHub;
import jade.core.behaviours.WakerBehaviour;

public class NotifyBehaviour extends WakerBehaviour {
    private ChargingHub chub;

    public NotifyBehaviour(ChargingHub chub, long timeout) {
        super(chub, timeout);
        this.chub = chub;
    }

    public void onWake(){
        chub.notifySubscribers();
    }

}