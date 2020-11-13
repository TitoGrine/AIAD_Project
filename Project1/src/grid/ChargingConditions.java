package grid;

import java.io.Serializable;

public class ChargingConditions implements Serializable {
    boolean terminateCharging = false;
    double chargingPrice = 0;
    int givenLoad = 0;

    public double getChargingPrice() {
        return chargingPrice;
    }

    public int getGivenLoad() { return givenLoad; }

    public boolean isChargingTerminated() {
        return terminateCharging;
    }

    public ChargingConditions(double chargingPrice) {
        this.chargingPrice = chargingPrice;
    }

    public ChargingConditions(int givenLoad) {
        this.givenLoad = givenLoad;
    }

    public ChargingConditions(double chargingPrice, int givenLoad) {
        this.chargingPrice = chargingPrice;
        this.givenLoad = givenLoad;
    }

    public ChargingConditions(boolean terminateCharging) {
        this.terminateCharging = terminateCharging;
    }
}
