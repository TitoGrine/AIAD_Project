package vehicle;

public abstract class SmartVehicle extends Vehicle {
    protected boolean chargeGrid;
    protected float altruistFactor;

    protected SmartVehicle(int currentCapacity, int maxCapacity, float altruistFactor, boolean chargeGrid) {
        super(currentCapacity, maxCapacity);

        this.chargeGrid = chargeGrid;
        this.altruistFactor = altruistFactor;
    }
}
