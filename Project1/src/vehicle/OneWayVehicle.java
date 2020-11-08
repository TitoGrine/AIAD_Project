package vehicle;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import vehicle.behaviour.OneWayStatusResponseBehaviour;

public class OneWayVehicle extends Vehicle {
    public OneWayVehicle(int currentCapacity, int maxCapacity) {
        super(currentCapacity,maxCapacity);
    }

    @Override
    public void addResponseBehaviour(ACLMessage msg) {
        addBehaviour(new OneWayStatusResponseBehaviour(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
    }
}
