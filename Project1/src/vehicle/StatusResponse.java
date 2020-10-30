package vehicle;

import utils.Constants;

import java.io.Serializable;

public class StatusResponse implements Serializable {
    double currentCapacity;
    double maxCapacity;
    float altruistFactor;

    public StatusResponse(double currentCapacity, double maxCapacity, float altruistFactor){
        this.currentCapacity = currentCapacity;
        this.maxCapacity = maxCapacity;
        this.altruistFactor = altruistFactor;
    }

    public StatusResponse(double currentCapacity, double maxCapacity){
        this(currentCapacity, maxCapacity, Constants.NO_FACTOR);
    }

    public double getCurrentCapacity() {
        return currentCapacity;
    }

    public double getMaxCapacity() {
        return maxCapacity;
    }

    public float getAltruistFactor() {
        return altruistFactor;
    }

    @Override
    public String toString() {
        String result = "Vehicle Status: \n";
        result += " · Current Capacity = " + currentCapacity + "\n";
        result += " · Max Capacity = " + maxCapacity + "\n";
        result += " · Altruistic Factor = " + altruistFactor + "\n";

        return result;
    }
}
