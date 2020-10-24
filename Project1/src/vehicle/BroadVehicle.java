package vehicle;

public class BroadVehicle extends SmartVehicle {
    public BroadVehicle() {
        this(10, 50, 0.1f, false);
    }

    public BroadVehicle(int currentCapacity, int maxCapacity, float altruistFactor, boolean chargeGrid) {
        super(currentCapacity, maxCapacity, altruistFactor, chargeGrid);
    }
}
