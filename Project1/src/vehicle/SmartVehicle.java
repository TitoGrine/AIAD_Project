package vehicle;

import vehicle.behaviour.Vehicle2GridBehaviour;

public abstract class SmartVehicle extends Vehicle {
    protected boolean chargeGrid;
    protected double altruistFactor;

    protected SmartVehicle(int currentCapacity, int maxCapacity, double altruistFactor, boolean chargeGrid) {
        super(currentCapacity, maxCapacity);

        this.chargeGrid = chargeGrid;
        this.altruistFactor = altruistFactor;

        if(chargeGrid)
            addBehaviour(new Vehicle2GridBehaviour(this));
    }

    public double getAltruistFactor() {
        return altruistFactor;
    }

    public boolean allowsV2G() {
        return chargeGrid;
    }
}
