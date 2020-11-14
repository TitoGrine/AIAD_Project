package vehicle.behaviour;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import utils.Utilities;
import vehicle.BroadVehicle;
import vehicle.StatusResponse;

import java.io.IOException;

public class BroadStatusResponseBehaviour extends AchieveREResponder {
    private BroadVehicle vehicle;

    public BroadStatusResponseBehaviour(BroadVehicle vehicle) {
        super(vehicle, MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
        this.vehicle = vehicle;
    }

    public ACLMessage handleRequest(ACLMessage request){
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.AGREE);
        reply.setContent("Connected.");

        vehicle.startConsensusProposal(request);
        return reply;
    }

    public void replyToChub(ACLMessage request) {
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        try {
            reply.setContentObject(new StatusResponse(vehicle.getCurrentCapacity(), vehicle.getMaxCapacity(), vehicle.getAltruistFactor(), vehicle.allowsV2G()));
        } catch (IOException e) {
            e.printStackTrace();
        }


        vehicle.send(reply);
    }
}
