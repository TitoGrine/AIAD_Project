package vehicle;

public class BroadVehicle extends SmartVehicle {
    protected BroadVehicle(int currentCapacity, int maxCapacity, float altruistFactor, boolean chargeGrid) {
        super(currentCapacity, maxCapacity, altruistFactor, chargeGrid);
    }

    @Override
    protected void setup() {
        System.out.println("I'm a Broad Car!!");
    }
}
