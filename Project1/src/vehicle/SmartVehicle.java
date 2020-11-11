package vehicle;

public abstract class SmartVehicle extends Vehicle {
    protected boolean chargeGrid;
    protected double altruistFactor;

    protected SmartVehicle(int currentCapacity, int maxCapacity, double altruistFactor, boolean chargeGrid) {
        super(currentCapacity, maxCapacity);

        this.chargeGrid = chargeGrid;
        this.altruistFactor = altruistFactor;
    }

    public double getAltruistFactor() {
        return altruistFactor;
    }
}
