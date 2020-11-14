package vehicle.behaviour;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ProposeResponder;
import vehicle.BroadVehicle;

import java.io.IOException;

public class BroadProposeResponder extends ProposeResponder {
    BroadVehicle vehicle;

    public BroadProposeResponder(BroadVehicle vehicle) {
        super(vehicle, MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
        this.vehicle = vehicle;
    }

    @Override
    protected ACLMessage prepareResponse(ACLMessage propose) {
        ACLMessage reply = propose.createReply();
        //TODO: should we check if the sender is the actual leader?
        try {
            reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            reply.setContentObject(vehicle.getAltruistFactor());
        } catch (IOException e) {
            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
            reply.setContent("IOException was thrown");
            e.printStackTrace();
        }

        vehicle.replyToChub();
        return reply;
    }
}
