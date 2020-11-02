package vehicle;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import vehicle.behaviour.SmartStatusResponseBehaviour;

public class TwoWayVehicle extends SmartVehicle {
    public TwoWayVehicle(int currentCapacity, int maxCapacity, float personality, boolean chargeGrid) {
        super(currentCapacity, maxCapacity, personality, chargeGrid);
    }

    @Override
    public void addResponseBehaviour(ACLMessage msg) {
        addBehaviour(new SmartStatusResponseBehaviour(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
    }
}
