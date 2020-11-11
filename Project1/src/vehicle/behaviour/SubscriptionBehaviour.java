package vehicle.behaviour;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.SubscriptionInitiator;
import utils.Constants;
import vehicle.Vehicle;

public class SubscriptionBehaviour extends SubscriptionInitiator {
    private Vehicle vehicle;

    public SubscriptionBehaviour(Vehicle vehicle, ACLMessage msg) {
        super(vehicle, msg);
        this.vehicle = vehicle;
    }

    public void handleAgree(ACLMessage msg){
        System.out.println(vehicle.getLocalName() + " - subscription agree: " + msg.getContent());
        vehicle.addResponseBehaviour(msg);
    }

    public void handleRefuse(ACLMessage msg){
        System.out.println(vehicle.getLocalName() + " - subscription refuse: " + msg.getContent());

        vehicle.doDelete();
    }

    public void handleInform(ACLMessage msg){
        try {
            int response = (int) msg.getContentObject();
                  

            if(response == Constants.ALLOW_DISCONNECT){
                System.out.println("Leaving the charging hub!");
                vehicle.doDelete();
                return;
            }

            System.out.println(vehicle.getLocalName() + " - subscription inform: " + response);
            vehicle.updateBattery(response);
            System.out.println("Now I need to pay the bill for the: " + response + " I received");
            vehicle.payBill(response);
        } catch (UnreadableException e) {
            e.printStackTrace();
        }

        vehicle.addResponseBehaviour(msg);
    }
}
