package vehicle.behaviour;

import grid.Vehicle2GridConditions;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
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

    public Vehicle2GridBehaviour(Agent a) {
        super(a, MessageTemplate.MatchPerformative(ACLMessage.CFP));
        vehicle = (SmartVehicle) a;
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
        double batteryPercentage = vehicle.getCurrentCapacity() / (double) vehicle.getMaxCapacity();
        ACLMessage reply = cfp.createReply();

        Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(), "received call for proposals of V2G");

        if(batteryPercentage > 0.9){
            int proposedLoad = (int) ((vehicle.getCurrentCapacity() - (0.8 - (0.2 * (vehicle.getAltruistFactor() - 0.5))) * vehicle.getMaxCapacity()) / Constants.TICK_RATIO);

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
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
        ACLMessage reply = accept.createReply();

        try {
            Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(), "received V2G confirmation: \n" + accept.getContentObject());
        } catch (UnreadableException e) {
            e.printStackTrace();
        }

        try {
            Vehicle2GridConditions chargingConditions = (Vehicle2GridConditions) accept.getContentObject();
            vehicle.chargeGrid(chargingConditions.getSharedLoad(), chargingConditions.getDiscountPrice());
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent("Charged grid.");
        } catch (UnreadableException e) {
            e.printStackTrace();
            reply.setPerformative(ACLMessage.FAILURE);
            reply.setContent("Failed to charge grid.");
        }

        return reply;
    }
}
