package grid.behaviour;

import grid.ChargingHub;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

public class RequestResponderBehaviour extends AchieveREResponder {
    private ChargingHub chub;

    public RequestResponderBehaviour(ChargingHub chub, MessageTemplate mt) {
        super(chub, mt);
        this.chub = chub;
    }

    public ACLMessage handleRequest(ACLMessage request){
        System.out.println("request:" + request);
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.AGREE);
        reply.setContent("aaaaaaaaaaa");
        return reply;
    }


    public ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response){
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        reply.setContent("bbbbbbbbbbbbbb");
        return reply;
    }
}
