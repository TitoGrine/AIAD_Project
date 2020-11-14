package vehicle.behaviour;

import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ProposeInitiator;
import utils.Constants;
import utils.Utilities;
import vehicle.BroadVehicle;
import vehicle.StatusResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public class BroadProposeInitiator extends ProposeInitiator {
    BroadVehicle vehicle;
    DFAgentDescription[] agents;
    ACLMessage statusRequest;
    String RESULT_NOTIFICATION_KEY;

    public BroadProposeInitiator(BroadVehicle vehicle, ACLMessage request, DFAgentDescription[] agents, String RESULT_NOTIFICATION_KEY) {
        super(vehicle, new ACLMessage(ACLMessage.PROPOSE));
        this.vehicle = vehicle;
        this.agents = agents;
        this.statusRequest = request;
        this.RESULT_NOTIFICATION_KEY = RESULT_NOTIFICATION_KEY;
    }

    @Override
    protected Vector prepareInitiations(ACLMessage propose) {
        Vector<ACLMessage> msgs = new Vector<>();

        for(DFAgentDescription agent : agents) {
            if(agent.getName().equals(vehicle.getAID())){
                continue;
            }
            Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(), "preparing propose for " + agent.getName().getLocalName());
            propose = new ACLMessage(ACLMessage.PROPOSE);
            propose.setContent("Proposing a start of consensus. Send me your AF's");
            propose.addReceiver(agent.getName());
            msgs.add(propose);
        }

        Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(), "Sending proposes");
        return msgs;
    }

    @Override
    protected void handleAllResponses(Vector responses) {
        ArrayList<Double> result = new ArrayList<>();

        for(Object response : responses) {
            try {
                result.add((Double) ((ACLMessage) response).getContentObject());
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        }

        vehicle.startConsensusNegotiation(result);
    }

    @Override
    public int onEnd() {
        ACLMessage reply = statusRequest.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        try {
            reply.setContentObject(new StatusResponse(vehicle.getCurrentCapacity(), vehicle.getMaxCapacity(), vehicle.getAltruistFactor(), vehicle.allowsV2G()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.getDataStore().put(RESULT_NOTIFICATION_KEY, reply);

        return 0;
    }
}
