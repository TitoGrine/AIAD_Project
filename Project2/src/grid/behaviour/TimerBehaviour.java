package grid.behaviour;

import grid.ChargingHub;
import jade.wrapper.ControllerException;
import sajas.core.behaviours.TickerBehaviour;
import utils.Constants;
import utils.Utilities;

public class TimerBehaviour extends TickerBehaviour {
    private ChargingHub chub;

    public TimerBehaviour(ChargingHub chub, long period) {
        super(chub, period);
        this.chub = chub;
    }

    @Override
    protected void onTick() {
        Utilities.printSystemMessage("round " + this.getTickCount());
        if(this.getTickCount() > Constants.CYCLE_TICKS){
            this.stop();
            chub.stopTask();
            try {
                chub.getContainerController().getPlatformController().kill();
            } catch (ControllerException e) {
                e.printStackTrace();
            }
        }
        chub.updateSystemStatus();
    }
}