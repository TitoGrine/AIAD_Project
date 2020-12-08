package grid.behaviour;

import grid.ChargingHub;
import sajas.core.Runtime;
import sajas.core.behaviours.TickerBehaviour;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utils.Constants;
import utils.Utilities;

public class TimerBehaviour extends TickerBehaviour {
    private ChargingHub chub;
    private ContainerController container;
    private Repast3Launcher launcher;

    public TimerBehaviour(ContainerController container, Repast3Launcher launcher, ChargingHub chub, long period) {
        super(chub, period);
        this.chub = chub;
        this.container = container;
        this.launcher = launcher;
    }

    @Override
    protected void onTick() {
        Utilities.printSystemMessage("round " + this.getTickCount());
        if(this.getTickCount() > Constants.CYCLE_TICKS){
            this.stop();
            try {
                container.kill();
                launcher.stop();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }
        chub.updateSystemStatus();
    }
}