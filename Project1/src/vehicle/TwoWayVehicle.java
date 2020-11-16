package vehicle;

import utils.Constants;
import vehicle.behaviour.TwoWayStatusResponseBehaviour;

public class TwoWayVehicle extends SmartVehicle {
    public TwoWayVehicle(int currentCapacity, int maxCapacity, double altruistFactor, boolean chargeGrid) {
        super(currentCapacity, maxCapacity, altruistFactor, chargeGrid);
    }

    @Override
    public void setup() {
        super.setup();
        addBehaviour(new TwoWayStatusResponseBehaviour(this));
    }

    @Override
    public int getVehicleType() {
        return Constants.TWOWAY_VEHICLE;
    }
}
