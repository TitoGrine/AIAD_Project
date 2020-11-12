package vehicle;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import utils.Constants;
import utils.Data;
import vehicle.behaviour.SubscriptionBehaviour;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Vehicle extends Agent {
    protected int currentCapacity;  // in kWh.
    protected int maxCapacity;      // maximum amount in kWh
    protected int initCapacity;      // initial amount in kWh
    protected double currentLoad = 0;
    protected double priceToPay = 0;

    protected double chargingPrice;
    private AID service;
    protected SubscriptionBehaviour subscription;

    public void setCurrentLoad(int currentLoad) {
        this.currentLoad = currentLoad;
    }

    protected Vehicle(int currentCapacity, int maxCapacity) {
        this.currentCapacity = currentCapacity;
        this.maxCapacity = maxCapacity;
        this.initCapacity = currentCapacity;

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

    public double getPriceToPay() {
        return priceToPay;
    }

    public void setChargingPrice(double chargingPrice) {
        this.chargingPrice = chargingPrice;
    }

    public void setup(){
        ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
        msg.addReceiver(service);
        msg.setContent("I want to charge.");
        subscription = new SubscriptionBehaviour(this, msg);
        addBehaviour(subscription);
    }

    public void updateBattery(int newLoad) {
        this.currentLoad = newLoad;
        this.priceToPay += this.currentLoad * Constants.TICK_RATIO * chargingPrice;
        currentCapacity = Math.min(this.maxCapacity, (int) (this.currentLoad * Constants.TICK_RATIO) + this.currentCapacity);
        double battery_percentage = (double) this.currentCapacity / this.maxCapacity;

        if(battery_percentage > 0.2){
            double leave = Constants.EXIT_PROBABILITY + 0.45 * battery_percentage;

            if(Math.random() < leave){
                System.out.println("Leaving with " + String.format("%d", (int) (battery_percentage * 100)) + "% of battery.");
                Data.submitStat(Arrays.asList(String.valueOf(this.currentCapacity - this.initCapacity), String.format("%.3g", battery_percentage), String.valueOf(this.priceToPay)));
                subscription.cancel(service, false);
            }
        }
    }

    public abstract void addResponseBehaviour(ACLMessage msg);

}