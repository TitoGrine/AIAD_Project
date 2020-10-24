package vehicle;

public class TwoWayVehicle extends SmartVehicle {
    public TwoWayVehicle() {
        this(10, 50, 0.1f, false);
    }

    public TwoWayVehicle(int currentCapacity, int maxCapacity, float altruistFactor, boolean chargeGrid) {
        super(currentCapacity, maxCapacity, altruistFactor, chargeGrid);
    }
}
