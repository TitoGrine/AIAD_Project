package vehicle;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;

public class VehicleSubscription extends SubscriptionInitiator {

    public VehicleSubscription(Agent a, ACLMessage msg) {
        super(a, msg);
    }

    public void handleAgree(ACLMessage msg){
        System.out.println(msg);
    }

    public void handleRefuse(ACLMessage msg){
        System.out.println(msg);
    }

    public void handleInform(ACLMessage msg){
        System.out.println(msg);
    }

}
