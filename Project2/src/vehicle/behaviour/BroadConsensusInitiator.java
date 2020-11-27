package vehicle.behaviour;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import sajas.proto.ContractNetInitiator;
import utils.Utilities;
import vehicle.BroadCarInfo;
import vehicle.BroadVehicle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class BroadConsensusInitiator extends ContractNetInitiator {
    BroadVehicle vehicle;
    DFAgentDescription[] agents;

    public BroadConsensusInitiator(BroadVehicle vehicle, DFAgentDescription[] agents) {
        super(vehicle, new ACLMessage(ACLMessage.PROPOSE));
        this.vehicle = vehicle;
        this.agents = agents;
    }

    @Override
    protected Vector prepareCfps(ACLMessage propose) {
        Vector<ACLMessage> msgs = new Vector<>();

        for(DFAgentDescription agent : agents) {
            if(agent.getName().equals(vehicle.getAID()))
                continue;

            propose = new ACLMessage(ACLMessage.CFP);
            propose.setContent("Requesting altruistic factor proposals.");
            propose.addReceiver(agent.getName());
            msgs.add(propose);
        }

        Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(), "sending call for proposals");
        return msgs;
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        Map<AID, BroadCarInfo> proposals = new HashMap<>();
        proposals.put(vehicle.getAID(), new BroadCarInfo(vehicle.getAID(), vehicle.getAltruistFactor(), (double) vehicle.getCurrentCapacity() / vehicle.getMaxCapacity()));

        Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(), "received all proposals!");

        for(Object response : responses) {
            try {
                proposals.put(((ACLMessage) response).getSender(), (BroadCarInfo) ((ACLMessage) response).getContentObject());
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        }

        Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(), "started adapting factors!");
        Map<AID, Double> counterProposals = vehicle.adaptFactors(proposals);
        Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(), "finished adapting factors!");


        for(Object response : responses) {
            ACLMessage reply = ((ACLMessage) response).createReply();
            double initAF = proposals.get(((ACLMessage) response).getSender()).getAf();
            double newAF = counterProposals.get(((ACLMessage) response).getSender());

            reply.setPerformative(initAF == newAF ? ACLMessage.ACCEPT_PROPOSAL : ACLMessage.REJECT_PROPOSAL);

            try {
                reply.setContentObject(newAF);
            } catch (IOException e) {
                e.printStackTrace();
            }

            acceptances.add(reply);
        }

        vehicle.replyToChub(counterProposals.get(vehicle.getAID()));
    }
}
