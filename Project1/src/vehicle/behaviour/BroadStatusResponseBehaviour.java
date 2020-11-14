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
    private ACLMessage statusRequest;

    public BroadStatusResponseBehaviour(BroadVehicle vehicle) {
        super(vehicle, MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
        this.vehicle = vehicle;
    }

    public ACLMessage handleRequest(ACLMessage request){
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.AGREE);
        reply.setContent("Connected.");
        statusRequest = request;

        registerPrepareResultNotification(vehicle.startConsensusProposal(request, this.RESULT_NOTIFICATION_KEY));
        return reply;
    }

    public ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response){
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        try {
            reply.setContentObject(new StatusResponse(vehicle.getCurrentCapacity(), vehicle.getMaxCapacity(), vehicle.getAltruistFactor(), vehicle.allowsV2G()));
        } catch (IOException e) {
            e.printStackTrace();
        }


        Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(), "Sending reply with AF");
        return null;
    }

    public void replyToChub() {
        ACLMessage reply = statusRequest.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        try {
            reply.setContentObject(new StatusResponse(vehicle.getCurrentCapacity(), vehicle.getMaxCapacity(), vehicle.getAltruistFactor(), vehicle.allowsV2G()));
        } catch (IOException e) {
            e.printStackTrace();
        }


        this.getDataStore().put(RESULT_NOTIFICATION_KEY, reply);
    }
}
