package vehicle.behaviour;

import grid.ChargingConditions;
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
        try {
            vehicle.setChargingPrice(((ChargingConditions) msg.getContentObject()).getChargingPrice());
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
        vehicle.addResponseBehaviour(msg);
    }

    public void handleRefuse(ACLMessage msg){
        System.out.println(vehicle.getLocalName() + " - subscription refuse: " + msg.getContent());

        vehicle.doDelete();
    }

    public void handleInform(ACLMessage msg){
        try {
            ChargingConditions response = (ChargingConditions) msg.getContentObject();
                  

            if(response.isChargingTerminated()){
                System.out.println("Leaving the charging hub!");
                vehicle.doDelete();
                return;
            }

            System.out.println(vehicle.getLocalName() + " - subscription inform: " + response.getGivenLoad());
            vehicle.updateBattery(response.getGivenLoad());
        } catch (UnreadableException e) {
            e.printStackTrace();
        }

        vehicle.addResponseBehaviour(msg);
    }
}
