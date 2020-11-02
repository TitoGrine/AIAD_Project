package vehicle;

public abstract class SmartVehicle extends Vehicle {
    protected boolean chargeGrid;
    protected float personality;
    protected final int expectedChargingTime = 1;

    protected SmartVehicle(int currentCapacity, int maxCapacity, float personality, boolean chargeGrid) {
        super(currentCapacity, maxCapacity);

        this.chargeGrid = chargeGrid;
        this.personality = personality;
    }

    public float getAltruistFactor() {
        double missingBattery = (maxCapacity - currentCapacity) / maxCapacity; // Goes from 0 to 1
        float energyNeeds = (float) (missingBattery / expectedChargingTime);
        float fa = (0.75f * energyNeeds + 0.25f * personality) + 0.5f;

        return fa;
    }
}
