package vehicle;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import vehicle.behaviour.OneWayRequestBehaviour;

import java.io.IOException;

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

    public void setup(){
        ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
        msg.addReceiver(new AID("CHub", false));
        msg.setContent("ola");
        addBehaviour(new VehicleSubscription(this, msg));
    }

    public void initiateRequest(ACLMessage msg) {
        ACLMessage request = msg.createReply();
        request.setPerformative(ACLMessage.REQUEST);
        try {
            request.setContentObject(this.currentCapacity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        addBehaviour(new OneWayRequestBehaviour(this, request));
    }

}