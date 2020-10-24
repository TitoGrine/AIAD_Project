package grid.behaviour;

import grid.ChargingHub;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;

import java.io.IOException;

public class RequestResponderBehaviour extends AchieveREResponder {
    private ChargingHub chub;

    public RequestResponderBehaviour(ChargingHub chub, MessageTemplate mt) {
        super(chub, mt);
        this.chub = chub;
    }

    public ACLMessage handleRequest(ACLMessage request){
        ACLMessage reply = request.createReply();
        double capacity = -1;
        try {
            capacity = (double) request.getContentObject();
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
        reply.setPerformative(ACLMessage.AGREE);
        reply.setContent(String.format("Got your battery! It is %f", capacity));

        System.out.println(chub.getLocalName() + " - handling request: " + request.getSender().getLocalName() + " has " + capacity + "kWh");

        return reply;
    }


    public ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response){
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        try {
            reply.setContentObject(chub.distributeLoad());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reply;
    }
}
