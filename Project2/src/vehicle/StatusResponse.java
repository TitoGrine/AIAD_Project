package vehicle;

import utils.Constants;

import java.io.Serializable;

public class StatusResponse implements Serializable {
    int currentCapacity;
    int maxCapacity;
    int vehicleType;
    double priceToPay;
    double altruistFactor;
    boolean allowsV2G = false;

    public StatusResponse(int currentCapacity, int maxCapacity, int vehicleType, double priceToPay, double altruistFactor, boolean allowsV2G) {
        this.currentCapacity = currentCapacity;
        this.maxCapacity = maxCapacity;
        this.vehicleType = vehicleType;
        this.priceToPay = priceToPay;
        this.altruistFactor = altruistFactor;
        this.allowsV2G = allowsV2G;
    }

    public StatusResponse(int currentCapacity, int maxCapacity, int vehicleType, double priceToPay,  double altruistFactor) {
        this.currentCapacity = currentCapacity;
        this.maxCapacity = maxCapacity;
        this.vehicleType = vehicleType;
        this.priceToPay = priceToPay;
        this.altruistFactor = altruistFactor;
    }

    public StatusResponse(int currentCapacity, int maxCapacity, int vehicleType, double priceToPay) {
        this(currentCapacity, maxCapacity, vehicleType, priceToPay, Constants.NO_FACTOR);
    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getType() {
        return vehicleType;
    }

    public double getPriceToPay() { return priceToPay; }

    public double getAltruistFactor() {
        return altruistFactor;
    }

    public boolean allowsV2G() {
        return allowsV2G;
    }

    public Double getBatteryPercentage() {
        return (double) currentCapacity / maxCapacity * 100;
    }

    @Override
    public String toString() {
        String result = "\n  Vehicle Status: \n";
        result += "     · Current Capacity = " + currentCapacity + "\n";
        result += "     · Max Capacity = " + maxCapacity + "\n";
        result += "     · Altruistic Factor = " + altruistFactor;

        return result;
    }
}
