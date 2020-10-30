package vehicle.behaviour;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import vehicle.StatusResponse;
import vehicle.Vehicle;

import java.io.IOException;

public class OneWayStatusResponseBehaviour extends AchieveREResponder {
    private Vehicle vehicle;

    public OneWayStatusResponseBehaviour(Vehicle vehicle, MessageTemplate msg) {
        super(vehicle, msg);
        this.vehicle = vehicle;
    }

    public ACLMessage handleRequest(ACLMessage request){
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.AGREE);

        try {
            reply.setContentObject(new StatusResponse(vehicle.getCurrentCapacity(), vehicle.getMaxCapacity()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(vehicle.getLocalName() + " - Received a request for status.");

        return reply;
    }

/*    public ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response){
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        try {
            reply.setContentObject(chub.distributeLoad());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reply;
    }*/
}
