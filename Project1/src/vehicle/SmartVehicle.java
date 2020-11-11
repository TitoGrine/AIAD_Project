package vehicle;

public abstract class SmartVehicle extends Vehicle {
    protected boolean chargeGrid;
    protected double altruist_factor;

    protected SmartVehicle(int currentCapacity, int maxCapacity, double altruist_factor, boolean chargeGrid) {
        super(currentCapacity, maxCapacity);

        this.chargeGrid = chargeGrid;
        this.altruist_factor = altruist_factor;
    }

    public double getAltruistFactor() {
        return altruist_factor;
    }
}
