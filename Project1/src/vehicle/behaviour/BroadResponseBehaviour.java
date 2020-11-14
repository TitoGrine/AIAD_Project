package vehicle.behaviour;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import vehicle.BroadVehicle;
import vehicle.StatusResponse;

import java.io.IOException;

public class BroadResponseBehaviour extends AchieveREResponder {
    private BroadVehicle vehicle;

    public BroadResponseBehaviour(BroadVehicle vehicle, MessageTemplate msg) {
        super(vehicle, msg);
        this.vehicle = vehicle;
    }

    public ACLMessage handleRequest(ACLMessage request){
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.AGREE);
        try {
            reply.setContentObject(vehicle.getAltruistFactor());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return reply;
    }

    public ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response){
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        try {
            reply.setContentObject(new StatusResponse(vehicle.getCurrentCapacity(), vehicle.getMaxCapacity(), vehicle.getAltruistFactor()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return reply;
    }
}
