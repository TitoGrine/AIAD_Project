package grid.behaviour;

import grid.ChargingHub;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import utils.Constants;

public class NotifyBehaviour extends TickerBehaviour {
    private ChargingHub chub;

    public NotifyBehaviour(ChargingHub chub, long period) {
        super(chub, period);
        this.chub = chub;
    }

    @Override
    protected void onTick() {
        if(this.getTickCount() > Constants.cycle_ticks)
            this.stop();

        chub.notifySubscribers();
    }
}