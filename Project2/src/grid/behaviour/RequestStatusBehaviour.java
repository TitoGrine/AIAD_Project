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
    private boolean distributionDone = false;

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
            if (Constants.TIMEOUTS_ON)
                request.setReplyByDate(new Date(new Date().getTime() + Constants.CHUB_TIMEOUT));

            msgs.add(request);
        }

        sentRequests = chub.getChargingVehicles().size();
        Utilities.printChargingHubMessage("requesting status to " + sentRequests + " vehicles...");

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
    protected void handleAllResponses(Vector responses) {
        if (sentRequests == responses.size()) return;

        // In case of timeout we handle the existing result notifications
        Utilities.printChargingHubMessage("chub timeout. Only got " + responses.size() + " responses!");
        handleAllResultNotifications((Vector) getDataStore().get(ALL_RESULT_NOTIFICATIONS_KEY));
        distributionDone = true;
    }

    @Override
    protected void handleAllResultNotifications(Vector resultNotifications) {
        if (distributionDone) return;

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
