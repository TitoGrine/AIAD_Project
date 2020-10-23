package vehicle;

import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;

import java.io.IOException;

public class VehicleSubscription extends SubscriptionInitiator {
    private Vehicle vehicle;

    public VehicleSubscription(Vehicle vehicle, ACLMessage msg) {
        super(vehicle, msg);
        this.vehicle = vehicle;
    }

    public void handleAgree(ACLMessage msg){
        System.out.println("received agree");
    }

    public void handleRefuse(ACLMessage msg){
        System.out.println("received refuse");
    }

    public void handleInform(ACLMessage msg){
        try {
            vehicle.initiateRequest(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("received inform");
    }

}
