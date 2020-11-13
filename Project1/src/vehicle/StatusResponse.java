package vehicle;

import utils.Constants;

import java.io.Serializable;

public class StatusResponse implements Serializable {
    int currentCapacity;
    int maxCapacity;
    double altruistFactor;

    public StatusResponse(int currentCapacity, int maxCapacity, double altruistFactor){
        this.currentCapacity = currentCapacity;
        this.maxCapacity = maxCapacity;
        this.altruistFactor = altruistFactor;
    }

    public StatusResponse(int currentCapacity, int maxCapacity){
        this(currentCapacity, maxCapacity, Constants.NO_FACTOR);
    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public double getAltruistFactor() {
        return altruistFactor;
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
