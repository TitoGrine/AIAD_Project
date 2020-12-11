package vehicle;

import jade.core.AID;
import sajas.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import utils.Constants;
import utils.Data;
import utils.Utilities;
import vehicle.behaviour.SubscriptionBehaviour;

import java.util.Arrays;

public abstract class Vehicle extends Agent {
    protected int currentCapacity;   // in kWh.
    protected int maxCapacity;       // maximum amount in kWh
    protected int initCapacity;      // initial amount in kWh
    protected double priceToPay = 0;

    protected double chargingPrice;
    private AID service;
    protected SubscriptionBehaviour subscription;

    private void updatePriceToPay(double load, double price, boolean toGrid) {
        this.priceToPay += load * Constants.TICK_RATIO * price * (toGrid ? -1 : 1);
    }

    private int updateCapacity(double load, boolean toGrid) {
        int chargedLoad = (int) (load * Constants.TICK_RATIO);
        currentCapacity = Math.max(0, Math.min(this.maxCapacity, chargedLoad * (toGrid ? -1 : 1) + this.currentCapacity));

        return chargedLoad;
    }

    protected Vehicle(int currentCapacity, int maxCapacity) {
        this.currentCapacity = currentCapacity;
        this.maxCapacity = maxCapacity;
        this.initCapacity = currentCapacity;
    }

    public AID getChub() {
        return service;
    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setChargingPrice(double chargingPrice) {
        this.chargingPrice = chargingPrice;
    }

    public double getPriceToPay() {
        return priceToPay;
    }

    public void setup() {
        DFAgentDescription[] chubs = new DFAgentDescription[0];
        while (chubs.length <= 0) {
            chubs = Utilities.getService(this, Constants.CHUB_SERVICE);
        }
        service = chubs[0].getName();

        ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
        msg.addReceiver(service);
        msg.setContent("Requesting charge.");
        subscription = new SubscriptionBehaviour(this, msg);
        addBehaviour(subscription);
    }

    public void chargeBattery(int newLoad) {
        this.updatePriceToPay(newLoad, chargingPrice, false);
        this.updateCapacity(newLoad, false);

    }

    public boolean isLeaving() {
        double battery_percentage = (double) this.currentCapacity / this.maxCapacity;

        if (battery_percentage > 0.2) {
            double leave = Constants.EXIT_PROBABILITY + Constants.EXIT_FACTOR * battery_percentage;

            if (Math.random() < leave) {
                return true;
            }
        }

        return false;
    }

    public void leaveHub() {
        double battery_percentage = (double) this.currentCapacity / this.maxCapacity;
        Data.submitVehicleStat(Arrays.asList(String.valueOf(this.getVehicleType()), String.valueOf(this.currentCapacity - this.initCapacity), String.format("%.3g", battery_percentage), String.valueOf(this.priceToPay)));
        subscription.cancel(service, false);

    }

    public int chargeGrid(int sharedLoad, double discountPrice) {
        this.updatePriceToPay(sharedLoad, discountPrice, true);
        return this.updateCapacity(sharedLoad, true);
    }

    public void exit() {
        Utilities.printSystemMessage("vehicle " + Constants.RED_BOLD + this.getLocalName() + Constants.RESET + " left the charging hub.");
        this.doDelete();
    }

    public abstract int getVehicleType();

}