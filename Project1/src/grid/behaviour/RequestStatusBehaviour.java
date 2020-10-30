package grid.behaviour;

import grid.ChargingHub;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;
import jade.proto.SubscriptionResponder;
import vehicle.StatusResponse;

import java.util.Vector;

public class RequestStatusBehaviour extends AchieveREInitiator {
    private ChargingHub chub;

    public RequestStatusBehaviour(ChargingHub chub, ACLMessage msg) {
        super(chub, msg);
        this.chub = chub;
    }

    @Override
    protected Vector prepareRequests(ACLMessage request) {
        Vector<ACLMessage> msgs = new Vector<>();

        for(SubscriptionResponder.Subscription subscription : chub.getChargingVehicles()){
            request = new ACLMessage((ACLMessage.REQUEST));
            request.setContent("Request car status.");
            request.addReceiver(subscription.getMessage().getSender());

            msgs.add(request);
        }

        return msgs;
    }

    public void handleAgree(ACLMessage msg){
        try {
            System.out.println(chub.getLocalName() + " - received agree: " + msg.getContentObject());
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
    }

    public void handleRefuse(ACLMessage msg){
        System.out.println(chub.getLocalName() + " - received refuse: " + msg.getContent());
    }

    public void handleFailure(ACLMessage msg){
        System.out.println(chub.getLocalName() + " - received failure: " + msg.getContent());
    }

    public void handleInform(ACLMessage msg){
        System.out.println(chub.getLocalName() + " - received failure: " + msg.getContent());

    }

    @Override
    protected void handleAllResponses(Vector resultNotifications) {
        try {
            for(Object response : resultNotifications) {
                    chub.updateVehicleStatus(((ACLMessage) response).getSender(), (StatusResponse) ((ACLMessage) response).getContentObject());
            }
        } catch (UnreadableException e) {
            e.printStackTrace();
        }

        chub.distributeLoad();
    }
}