package vehicle;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import vehicle.behaviour.OneWayRequestBehaviour;

import java.io.IOException;

public abstract class Vehicle extends Agent {
    protected double currentCapacity; //in kWh.
    protected double maxCapacity; // maximum amount in kWh
    protected double currentLoad = 0;

    public void setCurrentLoad(double currentLoad) {
        this.currentLoad = currentLoad;
    }

    protected Vehicle(double currentCapacity, double maxCapacity) {
        this.currentCapacity = currentCapacity;
        this.maxCapacity = maxCapacity;
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
        msg.setContent("ola");
        addBehaviour(new VehicleSubscription(this, msg));
    }

    public abstract void updateBattery(double newLoad);

    public abstract void initiateRequest(ACLMessage msg);

}