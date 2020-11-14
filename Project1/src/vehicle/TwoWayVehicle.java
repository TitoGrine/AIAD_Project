package vehicle;

import jade.lang.acl.ACLMessage;
import utils.Constants;
import vehicle.behaviour.TwoWayStatusResponseBehaviour;

public class TwoWayVehicle extends SmartVehicle {
    public TwoWayVehicle(int currentCapacity, int maxCapacity, double altruistFactor, boolean chargeGrid) {
        super(currentCapacity, maxCapacity, altruistFactor, chargeGrid);
    }

    @Override
    public void addResponseBehaviour(ACLMessage msg) {
        addBehaviour(new TwoWayStatusResponseBehaviour(this));
    }

    @Override
    public int getVehicleType() {
        return Constants.TWOWAY_VEHICLE;
    }
}
