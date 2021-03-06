package vehicle.behaviour;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.proto.AchieveREResponder;
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
        if(this.vehicle.isLeaving()){
            reply.setPerformative(ACLMessage.REFUSE);
            reply.setContent("Leaving the charging hub");
            this.vehicle.leaveHub();
            return reply;
        }

        if(vehicle.startConsensusProposal(request)) {
            reply.setPerformative(ACLMessage.AGREE);
            reply.setContent("Connected.");
            return reply;
        }

        return null;
    }

    @Override
    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
        return null;
    }

    public void replyToChub(ACLMessage request, double altruistFactor) {
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        try {
            reply.setContentObject(new StatusResponse(vehicle.getCurrentCapacity(), vehicle.getMaxCapacity(), vehicle.getVehicleType(), vehicle.getPriceToPay(), altruistFactor, vehicle.allowsV2G()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        vehicle.send(reply);
    }

    public void agreeToChub(ACLMessage request) {
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.AGREE);
        reply.setContent("Connected.");

        vehicle.send(reply);
    }
}
