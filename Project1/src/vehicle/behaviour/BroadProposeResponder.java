package vehicle.behaviour;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ProposeResponder;
import utils.Utilities;
import vehicle.BroadVehicle;
import vehicle.StatusResponse;

import java.io.IOException;

public class BroadProposeResponder extends ProposeResponder {
    BroadVehicle vehicle;
    ACLMessage statusRequest;
    String RESULT_NOTIFICATION_KEY;

    public BroadProposeResponder(BroadVehicle vehicle, ACLMessage statusRequest, String RESULT_NOTIFICATION_KEY) {
        super(vehicle, MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
        this.vehicle = vehicle;
        this.statusRequest = statusRequest;
        this.RESULT_NOTIFICATION_KEY = RESULT_NOTIFICATION_KEY;
    }

    @Override
    protected ACLMessage prepareResponse(ACLMessage propose) {
        ACLMessage reply = propose.createReply();
        //TODO: should we check if the sender is the actual leader?
        try {
            reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            reply.setContentObject(vehicle.getAltruistFactor());
        } catch (IOException e) {
            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
            reply.setContent("IOException was thrown");
            e.printStackTrace();
        }

        repl();
        return reply;
    }

    public void repl() {
        ACLMessage reply = statusRequest.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        try {
            reply.setContentObject(new StatusResponse(vehicle.getCurrentCapacity(), vehicle.getMaxCapacity(), vehicle.getAltruistFactor(), vehicle.allowsV2G()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(), "Sending reply to CHUB");
        vehicle.send(reply);
    }
}
