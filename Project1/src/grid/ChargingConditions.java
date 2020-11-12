package grid;

import java.io.Serializable;

public class ChargingConditions implements Serializable {
    double chargingPrice;

    public double getChargingPrice() {
        return chargingPrice;
    }

    public ChargingConditions(double chargingPrice) {
        this.chargingPrice = chargingPrice;
    }
}
