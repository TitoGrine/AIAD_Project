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

    public BroadProposeInitiator(BroadVehicle vehicle, DFAgentDescription[] agents) {
        super(vehicle, new ACLMessage(ACLMessage.PROPOSE));
        this.vehicle = vehicle;
        this.agents = agents;
    }

    @Override
    protected Vector prepareInitiations(ACLMessage propose) {
        Vector<ACLMessage> msgs = new Vector<>();

        for(DFAgentDescription agent : agents) {
            if(agent.getName().equals(vehicle.getAID())){
                continue;
            }

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
}
