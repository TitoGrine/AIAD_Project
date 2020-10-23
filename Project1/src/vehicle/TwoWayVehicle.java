package vehicle;

public class TwoWayVehicle extends SmartVehicle {
    public TwoWayVehicle(int currentCapacity, int maxCapacity, float altruistFactor, boolean chargeGrid) {
        super(currentCapacity, maxCapacity, altruistFactor, chargeGrid);
    }

    @Override
    protected void setup() {
        System.out.println("I'm a Two Way Car!");
    }
}
