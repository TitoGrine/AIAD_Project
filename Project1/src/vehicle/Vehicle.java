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
    private AID service;
    protected SubscriptionBehaviour subscription;

    public void setCurrentLoad(double currentLoad) {
        this.currentLoad = currentLoad;
    }

    protected Vehicle(double currentCapacity, double maxCapacity) {
        this.currentCapacity = currentCapacity;
        this.maxCapacity = maxCapacity;

        service = new AID("CHub", false);
    }

    public double getCurrentCapacity() {
        return currentCapacity;
    }

    public double getMaxCapacity() {
        return maxCapacity;
    }

    public void setCurrentCapacity(double currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public void setMaxCapacity(double maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public void setup(){
        ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
        msg.addReceiver(service);
        msg.setContent("I want to charge.");
        subscription = new SubscriptionBehaviour(this, msg);
        addBehaviour(subscription);
    }

    public void updateBattery(double newLoad) {
        this.currentLoad = newLoad;
        currentCapacity = Math.min(this.maxCapacity, this.currentLoad * Constants.tick_ratio + this.currentCapacity);

        if(this.currentCapacity / this.maxCapacity > 0.2){
            double leave = Constants.EXIT_PROBABILITY + 0.8 * (this.currentCapacity / this.maxCapacity);

            if(Math.random() < leave){
                subscription.cancel(service, true);
//                this.doDelete();
            }
        }
    }

    public abstract void addResponseBehaviour(ACLMessage msg);

}