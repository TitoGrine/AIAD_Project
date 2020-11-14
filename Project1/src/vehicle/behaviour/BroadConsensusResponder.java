package vehicle.behaviour;

import grid.Vehicle2GridConditions;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
import jade.proto.ProposeResponder;
import javafx.util.Pair;
import utils.Utilities;
import vehicle.BroadCarInfo;
import vehicle.BroadVehicle;

import java.io.IOException;

public class BroadConsensusResponder extends ContractNetResponder {
    BroadVehicle vehicle;

    public BroadConsensusResponder(BroadVehicle vehicle) {
        super(vehicle, MessageTemplate.MatchPerformative(ACLMessage.CFP));
        this.vehicle = vehicle;
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) {
        ACLMessage reply = cfp.createReply();
        //TODO: should we check if the sender is the actual leader?

        Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(), "received call for proposals of broad consensus");

        try {
            reply.setPerformative(ACLMessage.PROPOSE);
            reply.setContentObject(new BroadCarInfo(vehicle.getAID(), vehicle.getAltruistFactor(), (double) vehicle.getCurrentCapacity() / vehicle.getMaxCapacity()));
        } catch (IOException e) {
            reply.setPerformative(ACLMessage.REFUSE);
            reply.setContent("Exception occurred.");
            e.printStackTrace();
        }

        return reply;
    }

    @Override
    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(), "altruistic factor proposal was rejected.");
        double tempAltruistFactor;

        try {
             tempAltruistFactor = (double) reject.getContentObject();
        } catch (UnreadableException e) {
            tempAltruistFactor = vehicle.getAltruistFactor();
        }

        vehicle.replyToChub(tempAltruistFactor);

        super.reset();
    }

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept)  {
        Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(), "altruistic factor proposal was accepted.");
        vehicle.replyToChub();

        super.reset();
        return null;
    }
}
