package vehicle.behaviour;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import vehicle.SmartVehicle;
import vehicle.StatusResponse;
import vehicle.Vehicle;

import java.io.IOException;

public class SmartStatusResponseBehaviour extends AchieveREResponder {
    private SmartVehicle vehicle;

    public SmartStatusResponseBehaviour(Vehicle vehicle, MessageTemplate msg) {
        super(vehicle, msg);
        this.vehicle = (SmartVehicle) vehicle;
    }

    public ACLMessage handleRequest(ACLMessage request){
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.AGREE);
        reply.setContent("Connected.");

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
