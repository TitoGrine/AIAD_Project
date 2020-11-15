package vehicle;

import utils.Constants;
import vehicle.behaviour.OneWayStatusResponseBehaviour;

public class OneWayVehicle extends Vehicle {
    public OneWayVehicle(int currentCapacity, int maxCapacity) {
        super(currentCapacity,maxCapacity);
    }

    @Override
    public void setup() {
        super.setup();
        addBehaviour(new OneWayStatusResponseBehaviour(this));
    }

    @Override
    public int getVehicleType() {
        return Constants.ONEWAY_VEHICLE;
    }
}
