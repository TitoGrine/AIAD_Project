package vehicle;

import jade.lang.acl.ACLMessage;

public class BroadVehicle extends SmartVehicle {
    public BroadVehicle(int currentCapacity, int maxCapacity, double altruistFactor, boolean chargeGrid) {
        super(currentCapacity, maxCapacity, altruistFactor, chargeGrid);
    }

    @Override
    public void addResponseBehaviour(ACLMessage msg) {
        //TODO: Add response behaviour.
    }
}
