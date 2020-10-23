package vehicle;

import jade.core.Agent;

public abstract class Vehicle extends Agent {
    protected int currentCapacity; //in %.
    protected int maxCapacity; // maximum amount in kWh

    protected Vehicle(int currentCapacity, int maxCapacity) {
        this.currentCapacity = currentCapacity;
        this.maxCapacity = maxCapacity;
    }

    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}