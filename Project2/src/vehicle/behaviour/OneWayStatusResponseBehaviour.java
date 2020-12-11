package vehicle.behaviour;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.proto.AchieveREResponder;
import vehicle.StatusResponse;
import vehicle.Vehicle;

import java.io.IOException;

public class OneWayStatusResponseBehaviour extends AchieveREResponder {
    private Vehicle vehicle;

    public OneWayStatusResponseBehaviour(Vehicle vehicle) {
        super(vehicle, MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
        this.vehicle = vehicle;
    }

    public ACLMessage handleRequest(ACLMessage request){
        ACLMessage reply = request.createReply();
        if(this.vehicle.isLeaving()){
            reply.setPerformative(ACLMessage.REFUSE);
            reply.setContent("Leaving the charging hub");
            this.vehicle.leaveHub();
        } else{
            reply.setPerformative(ACLMessage.AGREE);
            reply.setContent("Connected.");
        }

        return reply;
    }

    public ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response){
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        try {
            reply.setContentObject(new StatusResponse(vehicle.getCurrentCapacity(), vehicle.getMaxCapacity(), vehicle.getVehicleType(), vehicle.getPriceToPay()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return reply;
    }
}
