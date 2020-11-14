package vehicle;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import javafx.util.Pair;
import utils.Constants;
import utils.Utilities;
import vehicle.behaviour.BroadConsensusInitiator;
import vehicle.behaviour.BroadConsensusResponder;
import vehicle.behaviour.BroadStatusResponseBehaviour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        if(amILeader(agents)) {
            Utilities.printVehicleMessage(getLocalName(), getVehicleType(), "I am the leader!");
            result = new BroadConsensusInitiator(this, agents);
            addBehaviour(result);
        } else {
            result = new BroadConsensusResponder(this);
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

    public void replyToChub() {
        responseBehaviour.replyToChub(this.request, this.altruistFactor);
    }

    public void replyToChub(double altruistFactor) {
        responseBehaviour.replyToChub(this.request, altruistFactor);
    }

    // Pair<AF, ChargedPercent>
    private double calculateAvg(Map<AID, BroadCarInfo> proposals) {
        double sum = 0;

        for(BroadCarInfo info : proposals.values()) {
            sum += info.getAf();
        }

        return sum / proposals.size();
    }

    public Map<AID, Double> adaptFactors(Map<AID, BroadCarInfo> proposals){
        double avgAF = calculateAvg(proposals);
        Map<AID, Double> result = new HashMap<>();
        ArrayList<BroadCarInfo> list = new ArrayList<>(proposals.values());
        list.sort((a, b) -> {
            if(a.getChargedPercent() == b.getChargedPercent())
                return 0;
            return a.getChargedPercent() < b.getChargedPercent() ? -1 : 1;
        });

        Utilities.printVehicleMessage(getLocalName(), getVehicleType(), "Ordered list of proposals: " + list.toString());

        double mid = list.size() / 2;
        //TODO: check mid value and act accordingly
        if(list.size() % 2 == 0) {
            firstHalfDistribution(list.subList(0, (int) mid), avgAF / 2.0, result);
            secondHalfDistribution(list.subList((int) mid, list.size()), avgAF / 2.0, result);
        } else {
            // If list.size is 1, then firsHalf will go from 0 to 0 and secondHalf from 1 to 1 which means it remains unchanged
            firstHalfDistribution(list.subList(0, (int) mid), avgAF / 2.0, result);
            secondHalfDistribution(list.subList((int) mid + 1, list.size()), avgAF / 2.0, result);
            // The exact middle elem remains unchanged
            BroadCarInfo midElem = list.get((int) mid);
            result.put(midElem.getAid(), midElem.getAf());
        }

        return result;
    }

    private void firstHalfDistribution(List<BroadCarInfo> firstHalf, double halfAvgAF, Map<AID, Double> result) {
        double totalMissingPercent = 0;

        for(BroadCarInfo info : firstHalf) {
            totalMissingPercent += 1 - info.getChargedPercent();
        }

        for(BroadCarInfo info : firstHalf) {
            double delta = halfAvgAF * (1 - info.getChargedPercent()) / totalMissingPercent;
            result.put(info.getAid(), Double.max(info.getAf() - delta, 0.0));
        }
    }

    private void secondHalfDistribution(List<BroadCarInfo> secondHalf, double halfAvgAF, Map<AID, Double> result) {
        double totalChargedPercent = 0;

        for(BroadCarInfo info : secondHalf) {
            totalChargedPercent += info.getChargedPercent();
        }

        for(BroadCarInfo info : secondHalf) {
            double delta = halfAvgAF * info.getChargedPercent() / totalChargedPercent;
            result.put(info.getAid(), Double.min(info.getAf() + delta, 1.0));
        }
    }

    @Override
    public int getVehicleType() {
        return Constants.BROAD_VEHICLE;
    }
}
