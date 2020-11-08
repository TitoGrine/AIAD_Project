package vehicle;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import utils.Constants;
import vehicle.behaviour.SubscriptionBehaviour;

public abstract class Vehicle extends Agent {
    protected int currentCapacity;  // in kWh.
    protected int maxCapacity;      // maximum amount in kWh
    protected double currentLoad = 0;
    private AID service;
    protected SubscriptionBehaviour subscription;

    public void setCurrentLoad(int currentLoad) {
        this.currentLoad = currentLoad;
    }

    protected Vehicle(int currentCapacity, int maxCapacity) {
        this.currentCapacity = currentCapacity;
        this.maxCapacity = maxCapacity;

        service = new AID("Charging_Hub", false);
    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public void setup(){
        ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
        msg.addReceiver(service);
        msg.setContent("I want to charge.");
        subscription = new SubscriptionBehaviour(this, msg);
        addBehaviour(subscription);
    }

    public void updateBattery(int newLoad) {
        double battery_percentage = (double) this.currentCapacity / this.maxCapacity;
        this.currentLoad = newLoad;
        currentCapacity = Math.min(this.maxCapacity, (int) (this.currentLoad * Constants.TICK_RATIO) + this.currentCapacity);

        if(battery_percentage > 0.2){
            double leave = Constants.EXIT_PROBABILITY + 0.45 * battery_percentage;

            if(Math.random() < leave){
                System.out.println("Leaving with " + battery_percentage + "% of battery.");
                subscription.cancel(service, false);
            }
        }
    }

    public abstract void addResponseBehaviour(ACLMessage msg);

}