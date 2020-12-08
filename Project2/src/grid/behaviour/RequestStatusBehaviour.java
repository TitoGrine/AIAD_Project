package grid.behaviour;

import grid.ChargingHub;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import sajas.proto.AchieveREInitiator;
import sajas.proto.SubscriptionResponder;
import utils.Constants;
import utils.Utilities;
import vehicle.StatusResponse;

import java.util.Date;
import java.util.Vector;

public class RequestStatusBehaviour extends AchieveREInitiator {
    private ChargingHub chub;
    private int sentRequests;

    public RequestStatusBehaviour(ChargingHub chub, ACLMessage msg) {
        super(chub, msg);
        this.chub = chub;
    }

    @Override
    protected Vector prepareRequests(ACLMessage request) {
        Vector<ACLMessage> msgs = new Vector<>();

        for (SubscriptionResponder.Subscription subscription : chub.getChargingVehicles()) {
            request = new ACLMessage((ACLMessage.REQUEST));
            request.setContent("Requesting car status.");
            request.addReceiver(subscription.getMessage().getSender());
            // For timeout purposes; sets the timeout as 100 ms
            request.setReplyByDate(new Date(new Date().getTime() + Constants.TIMEOUT));

            msgs.add(request);
        }

        sentRequests = chub.getChargingVehicles().size();

        return msgs;
    }

    public void handleAgree(ACLMessage msg) {
        Utilities.printChargingHubMessage("received agree: " + msg.getContent());
    }

    public void handleRefuse(ACLMessage msg) {
        Utilities.printChargingHubMessage("received refuse: " + msg.getContent());
    }

    public void handleFailure(ACLMessage msg) {
        Utilities.printChargingHubMessage("received failure from " + msg.getSender() + ": " + msg.getContent());
        chub.closeSubscription(msg.getSender());
    }

    public void handleInform(ACLMessage msg) {
        try {
            Utilities.printChargingHubMessage("received inform from " + msg.getSender().getLocalName() + ":\n" + msg.getContentObject());
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleAllResultNotifications(Vector resultNotifications) {
        if(sentRequests != resultNotifications.size())
            Utilities.printChargingHubMessage("chub timeout!");

        try {
            for (Object response : resultNotifications) {
                chub.updateVehicleStatus(((ACLMessage) response).getSender(), (StatusResponse) ((ACLMessage) response).getContentObject());
            }
        } catch (UnreadableException e) {
            e.printStackTrace();
        }

        chub.analyzeSystem();
    }
}
