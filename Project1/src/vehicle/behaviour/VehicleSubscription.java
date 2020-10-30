package vehicle.behaviour;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.SubscriptionInitiator;
import vehicle.Vehicle;

public class VehicleSubscription extends SubscriptionInitiator {
    private Vehicle vehicle;

    public VehicleSubscription(Vehicle vehicle, ACLMessage msg) {
        super(vehicle, msg);
        this.vehicle = vehicle;
    }

    public void handleAgree(ACLMessage msg){
        System.out.println(vehicle.getLocalName() + " - subscription agree: " + msg.getContent());
        vehicle.addResponseBehaviour(msg);
    }

    public void handleRefuse(ACLMessage msg){
        System.out.println(vehicle.getLocalName() + " - subscription refuse: " + msg.getContent());
    }

    public void handleInform(ACLMessage msg){
        System.out.println(vehicle.getLocalName() + " - subscription inform: " + msg.getContent().toString());
        try {
            vehicle.updateBattery((double) msg.getContentObject());
        } catch (UnreadableException e) {
            e.printStackTrace();
        }

        vehicle.addResponseBehaviour(msg);
    }
}
