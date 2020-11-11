package grid.behaviour;

import grid.ChargingHub;
import jade.core.Runtime;
import jade.core.behaviours.TickerBehaviour;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import sun.misc.Signal;
import utils.Constants;

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
        if(this.getTickCount() > Constants.CYCLE_TICKS){
            this.stop();
            try {
                container.kill();
                runtime.shutDown();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }

        chub.updateSystemStatus();
    }
}