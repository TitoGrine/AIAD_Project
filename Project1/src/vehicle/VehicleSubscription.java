package vehicle;

import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;

public class VehicleSubscription extends SubscriptionInitiator {
    private Vehicle vehicle;

    public VehicleSubscription(Vehicle vehicle, ACLMessage msg) {
        super(vehicle, msg);
        this.vehicle = vehicle;
    }

    public void handleAgree(ACLMessage msg){
        System.out.println(vehicle.getLocalName() + " - subscription agree: " + msg.getContent());
    }

    public void handleRefuse(ACLMessage msg){
        System.out.println(vehicle.getLocalName() + " - subscription refuse: " + msg.getContent());
    }

    public void handleInform(ACLMessage msg){
        System.out.println(vehicle.getLocalName() + " - subscription inform: " + msg.getContent());
        vehicle.initiateRequest(msg);
    }

}
