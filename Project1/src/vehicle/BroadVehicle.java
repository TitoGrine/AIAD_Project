package vehicle;

import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import utils.Constants;
import utils.Utilities;
import vehicle.behaviour.BroadProposeInitiator;
import vehicle.behaviour.BroadStatusResponseBehaviour;
import vehicle.behaviour.TwoWayStatusResponseBehaviour;

import java.util.ArrayList;

public class BroadVehicle extends SmartVehicle {
    public BroadVehicle(int currentCapacity, int maxCapacity, double altruistFactor, boolean chargeGrid) {
        super(currentCapacity, maxCapacity, altruistFactor, chargeGrid);
    }

    @Override
    public void setup() {
        super.setup();
        Utilities.registerService(this, Constants.BROAD_SERVICE);
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addResponseBehaviour(ACLMessage msg) {
        //TODO: Add response behaviour.
        addBehaviour(new BroadStatusResponseBehaviour(this));
    }

    @Override
    public int getVehicleType() {
        return Constants.BROAD_VEHICLE;
    }

    public void startConsensusProposal() {
        DFAgentDescription[] agents = Utilities.getService(this, Constants.BROAD_SERVICE);
        if(amILeader(agents))
            addBehaviour(new BroadProposeInitiator(this));
        //TODO: should the propose responder behaviour be added here?
    }

    private boolean amILeader(DFAgentDescription[] agents) {
        //The vehicle considers it is the leader
        for(int i = 0; i < agents.length; i++) {
            if(agents[i].getName().getLocalName().compareTo(this.getLocalName()) < 0)
                //There is an agent with a name that lexicographically precedes its own name
                return false;
            else if(agents[i].getName().getLocalName().compareTo(this.getLocalName()) == 0)
                //Needing a tie breaker
                return false;
        }

        return true;
    }

    public void startConsensusNegotiation(ArrayList<Double> result) {
        //TODO: add contract net behaviour
        for(Double af : result) {
            Utilities.printVehicleMessage(getLocalName(), getVehicleType(), "got an AF of " + af);
        }
    }
}
