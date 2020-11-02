package vehicle;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import utils.Constants;
import vehicle.behaviour.SubscriptionBehaviour;

public abstract class Vehicle extends Agent {
    protected double currentCapacity; //in kWh.
    protected double maxCapacity; // maximum amount in kWh
    protected double currentLoad = 0;

    protected Vehicle(double currentCapacity, double maxCapacity) {
        this.currentCapacity = currentCapacity;
        this.maxCapacity = maxCapacity;
    }

    public double getCurrentCapacity() {
        return currentCapacity;
    }

    public double getMaxCapacity() {
        return maxCapacity;
    }

    public void setCurrentLoad(double currentLoad) {
        this.currentLoad = currentLoad;
    }

    public void setCurrentCapacity(double currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public void setMaxCapacity(double maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public void setup(){
        ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
        msg.addReceiver(new AID("CHub", false));
        msg.setContent("I want to charge.");
        addBehaviour(new SubscriptionBehaviour(this, msg));
    }

    public void updateBattery(double newLoad) {
        this.currentLoad = newLoad;
        currentCapacity = Math.min(this.maxCapacity, this.currentLoad + this.currentCapacity);
    }

    public abstract void addResponseBehaviour(ACLMessage msg);

}