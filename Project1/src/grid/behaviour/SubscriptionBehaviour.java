package grid.behaviour;

import grid.ChargingHub;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionResponder;

public class SubscriptionBehaviour extends SubscriptionResponder {
    private ChargingHub chub;

    public SubscriptionBehaviour(ChargingHub chub, MessageTemplate mt) {
        super(chub, mt);
        this.chub = chub;
    }

    public ACLMessage handleSubscription(ACLMessage subscription){
        ACLMessage reply = subscription.createReply();
        if(chub.getOccupiedStations() < chub.getNumStations()){
            chub.addVehicle();
            reply.setContent("Charging allowed.");
            reply.setPerformative(ACLMessage.AGREE);

            createSubscription(subscription);
        }
        else{
            reply.setPerformative(ACLMessage.REFUSE);
            reply.setContent("Charging not allowed. There isn't enough charging stations.");
        }
        return reply;
    }

    @Override
    protected ACLMessage handleCancel(ACLMessage cancel) throws FailureException {
        chub.removeVehicle();

        System.out.println("Vehicle left the charging hub!");

        return super.handleCancel(cancel);
    }
}