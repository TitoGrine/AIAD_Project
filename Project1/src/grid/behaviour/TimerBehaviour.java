package grid.behaviour;

import grid.ChargingHub;
import jade.core.behaviours.TickerBehaviour;
import utils.Constants;

public class TimerBehaviour extends TickerBehaviour {
    private ChargingHub chub;

    public TimerBehaviour(ChargingHub chub, long period) {
        super(chub, period);
        this.chub = chub;
    }

    @Override
    protected void onTick() {
        if(this.getTickCount() > Constants.CYCLE_TICKS)
            this.stop();

        chub.updateSystemStatus();
    }
}