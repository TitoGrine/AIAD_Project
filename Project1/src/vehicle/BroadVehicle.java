package vehicle;

import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import utils.Constants;
import utils.Utilities;
import vehicle.behaviour.BroadProposeInitiator;
import vehicle.behaviour.BroadProposeResponder;
import vehicle.behaviour.BroadStatusResponseBehaviour;
import vehicle.behaviour.TwoWayStatusResponseBehaviour;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BroadVehicle extends SmartVehicle {
    private BroadStatusResponseBehaviour responseBehaviour;
    private ACLMessage request;

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
    public double getAltruistFactor() {
        return super.getAltruistFactor();
    }

    @Override
    public void addResponseBehaviour(ACLMessage msg) {
        //TODO: Add response behaviour.
        responseBehaviour = new BroadStatusResponseBehaviour(this);
        addBehaviour(responseBehaviour);
    }

    public void startConsensusProposal(ACLMessage request) {
        DFAgentDescription[] agents = Utilities.getService(this, Constants.BROAD_SERVICE);
        Behaviour result;
        this.request = request;

        Utilities.printVehicleMessage(getLocalName(), getVehicleType(), "request received is: " + request);

        if(amILeader(agents)) {
            Utilities.printVehicleMessage(getLocalName(), getVehicleType(), "I am the leader!");
            result = new BroadProposeInitiator(this, agents);
            addBehaviour(result);
        } else {
            result = new BroadProposeResponder(this);
            addBehaviour(result);
        }
        //TODO: should the propose responder behaviour be added here?
    }

    private boolean amILeader(DFAgentDescription[] agents) {
        //The vehicle considers it is the leader
        for(int i = 0; i < agents.length; i++) {
            if(agents[i].getName().getLocalName().compareTo(this.getLocalName()) < 0)
                //There is an agent with a name that lexicographically precedes its own name
                return false;
            //Since there can not be duplicate agent names, there is no need for a tie breaker
        }

        return true;
    }

    public void startConsensusNegotiation(ArrayList<Double> result) {
        //TODO: add AID -> AF association
        //TODO: add contract net behaviour
        result.add(getAltruistFactor());
        Utilities.printVehicleMessage(getLocalName(), getVehicleType(), "the negotiation tuple is " + result);
        this.replyToChub();
    }

    public void replyToChub() {
        responseBehaviour.replyToChub(this.request);
    }

    @Override
    public int getVehicleType() {
        return Constants.BROAD_VEHICLE;
    }
}
