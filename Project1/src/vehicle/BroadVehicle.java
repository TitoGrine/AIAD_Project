package vehicle;

import jade.lang.acl.ACLMessage;
import utils.Constants;

public class BroadVehicle extends SmartVehicle {
    public BroadVehicle() {
        this(10, 50, 0.1f, false);
    }

    public BroadVehicle(int currentCapacity, int maxCapacity, float altruistFactor, boolean chargeGrid) {
        super(currentCapacity, maxCapacity, altruistFactor, chargeGrid);
    }

    @Override
    public void updateBattery(double newLoad) {
        currentCapacity = Math.min(this.maxCapacity, this.currentLoad * Constants.tick_ratio + this.currentCapacity);
        this.currentLoad = newLoad;
    }

    @Override
    public void addResponseBehaviour(ACLMessage msg) {
        //TODO: Add response behaviour.
    }
}
