package vehicle.behaviour;

import grid.Vehicle2GridConditions;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
import utils.Constants;
import utils.Utilities;
import vehicle.SmartVehicle;

import java.io.IOException;

public class Vehicle2GridBehaviour extends ContractNetResponder {
    SmartVehicle vehicle;

    public Vehicle2GridBehaviour(SmartVehicle a) {
        super(a, MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CFP), MessageTemplate.MatchContent("Requesting vehicle proposal to charge grid.")));
        vehicle = a;
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) {
        double batteryPercentage = vehicle.getCurrentCapacity() / (double) vehicle.getMaxCapacity();
        ACLMessage reply = cfp.createReply();

        Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(), "received call for proposals of V2G");

        if(batteryPercentage > 0.85){
            int proposedLoad = (int) ((vehicle.getCurrentCapacity() - (0.75 - (0.2 * (vehicle.getAltruistFactor() - 0.5))) * vehicle.getMaxCapacity()) / Constants.TICK_RATIO);

            reply.setPerformative(ACLMessage.PROPOSE);
            try {
                reply.setContentObject(proposedLoad);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            reply.setPerformative(ACLMessage.REFUSE);
            reply.setContent("Not enough battery to charge grid.");
        }

        return reply;
    }

    @Override
    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(), "received V2G rejection: " + reject.getContent());
        super.reset();
    }

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept)  {
        ACLMessage reply = accept.createReply();

        try {
            Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(), "received V2G confirmation: \n" + accept.getContentObject());
        } catch (UnreadableException e) {
            e.printStackTrace();
        }

        try {
            Vehicle2GridConditions chargingConditions = (Vehicle2GridConditions) accept.getContentObject();
            int chargedLoad = vehicle.chargeGrid(chargingConditions.getSharedLoad(), chargingConditions.getDiscountPrice());
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContentObject(chargedLoad);
        } catch (UnreadableException | IOException e) {
            e.printStackTrace();
            reply.setPerformative(ACLMessage.FAILURE);
            reply.setContent("Failed to charge grid.");
        }

        return reply;
    }
}
