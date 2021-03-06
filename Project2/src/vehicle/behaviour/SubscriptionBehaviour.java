package vehicle.behaviour;

import grid.ChargingConditions;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import sajas.proto.SubscriptionInitiator;
import utils.Utilities;
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
    }

    public void handleRefuse(ACLMessage msg){
        Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(), "subscription refuse: " + msg.getContent());

        vehicle.doDelete();
    }

    public void handleInform(ACLMessage msg){
        try {
            ChargingConditions response = (ChargingConditions) msg.getContentObject();
                  

            if(response.isChargingTerminated()){
                vehicle.exit();
                return;
            }

            Utilities.printVehicleMessage(vehicle.getLocalName(),vehicle.getVehicleType(), "subscription inform: " + response.getGivenLoad());
            vehicle.chargeBattery(response.getGivenLoad());
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
    }
}
