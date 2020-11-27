package grid.behaviour;

import grid.ChargingHub;
import sajas.core.Runtime;
import sajas.core.behaviours.TickerBehaviour;
import sajas.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utils.Constants;
import utils.Utilities;

public class TimerBehaviour extends TickerBehaviour {
    private ChargingHub chub;
    private ContainerController container;
    private Runtime runtime;

    public TimerBehaviour(Runtime runtime, ContainerController container, ChargingHub chub, long period) {
        super(chub, period);
        this.chub = chub;
        this.container = container;
        this.runtime = runtime;
    }

    @Override
    protected void onTick() {
        Utilities.printSystemMessage("round " + this.getTickCount());
        if(this.getTickCount() > Constants.CYCLE_TICKS){
            this.stop();
            try {
                container.kill();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }
        chub.updateSystemStatus();
    }
}