package vehicle.behaviour;

import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ProposeInitiator;
import utils.Constants;
import utils.Utilities;
import vehicle.BroadVehicle;

import java.util.ArrayList;
import java.util.Vector;

public class BroadProposeInitiator extends ProposeInitiator {
    BroadVehicle vehicle;

    public BroadProposeInitiator(BroadVehicle a, ACLMessage msg) {
        super(a, msg);
        vehicle = a;
    }

    @Override
    protected Vector prepareInitiations(ACLMessage propose) {
        Vector<ACLMessage> msgs = new Vector<>();

        DFAgentDescription[] vehicles = Utilities.getService(vehicle, Constants.BROAD_SERVICE);
        for(int i = 0; i < vehicles.length; i++) {
            propose = new ACLMessage(ACLMessage.PROPOSE);
            propose.setContent("Proposing a start of consensus. Send me your FA's");
            propose.addReceiver(vehicles[i].getName());
            msgs.add(propose);
        }

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
}
