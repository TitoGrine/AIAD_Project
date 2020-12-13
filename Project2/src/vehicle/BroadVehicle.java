package vehicle;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import javafx.util.Pair;
import sajas.core.behaviours.Behaviour;
import sajas.domain.DFService;
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
    private boolean registered;

    public BroadVehicle(int currentCapacity, int maxCapacity, double altruistFactor, boolean chargeGrid) {
        super(currentCapacity, maxCapacity, altruistFactor, chargeGrid);
        registered = false;
    }

    @Override
    public void setup() {
        super.setup();
        responseBehaviour = new BroadStatusResponseBehaviour(this);
        addBehaviour(responseBehaviour);
        addBehaviour(new BroadConsensusResponder(this));
    }

    @Override
    protected void takeDown() {
        try {
            if(registered)
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
    public int getVehicleType() {
        return Constants.BROAD_VEHICLE;
    }

    @Override
    public void chargeBattery(int newLoad) {
        super.chargeBattery(newLoad);

        if(!registered) {
            Utilities.registerService(this, Constants.BROAD_SERVICE);
            registered = true;
        }
    }

    public boolean startConsensusProposal(ACLMessage request) {
        this.request = request;

        if(!registered) {
            agreeAndReplyToChub();
            return false;
        }

        DFAgentDescription[] agents = Utilities.getService(this, Constants.BROAD_SERVICE);
        Behaviour result;

        if (agents.length <= 1) {
            Utilities.printVehicleMessage(getLocalName(), getVehicleType(), "there are no other broad vehicles on the system. Canceling consensus");
            agreeAndReplyToChub();
            return false;
        }
        if (amILeader(agents)) {
            Utilities.printVehicleMessage(getLocalName(), getVehicleType(), "I am the leader!");
            result = new BroadConsensusInitiator(this, agents);
            addBehaviour(result);
        }

        return true;
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

    public void agreeAndReplyToChub() {
        responseBehaviour.agreeToChub(this.request);
        responseBehaviour.replyToChub(this.request, this.altruistFactor);
    }

    public void replyToChub() {
        responseBehaviour.replyToChub(this.request, this.altruistFactor);
    }

    public void replyToChub(double altruistFactor) {
        responseBehaviour.replyToChub(this.request, altruistFactor);
    }

    public Map<AID, Double> adaptFactors(Map<AID, BroadCarInfo> proposals){
        Pair<Double, Double> avgMeasures = calculateAvg(proposals);
        Map<AID, Double> result = new HashMap<>();
        ArrayList<BroadCarInfo> list = new ArrayList<>(proposals.values());
        list.sort((a, b) -> {
            if(a.getChargedPercent() == b.getChargedPercent())
                return 0;
            return a.getChargedPercent() < b.getChargedPercent() ? -1 : 1;
        });

        int mid = findMid(list, avgMeasures.getValue());
        if(mid != -1) {
            firstHalfDistribution(list.subList(0, mid), avgMeasures.getKey() / 2.0, result);
            secondHalfDistribution(list.subList(mid, list.size()), avgMeasures.getKey() / 2.0, result);
        } else {
            for(BroadCarInfo elem : list) {
                result.put(elem.getAid(), elem.getAf());
            }
        }

        return result;
    }

    private Pair<Double, Double> calculateAvg(Map<AID, BroadCarInfo> proposals) {
        double sumAF = 0;
        double sumPercent = 0;

        for(BroadCarInfo info : proposals.values()) {
            sumAF += info.getAf();
            sumPercent += info.getChargedPercent();
        }

        double avgAF = proposals.size() == 0 ? 0 : sumAF / proposals.size();
        double avgPercent = proposals.size() == 0 ? 0 : sumPercent / proposals.size();

        return new Pair<>(avgAF, avgPercent);
    }

    private int findMid(List<BroadCarInfo> list, Double avg) {
        for (int i = 0; i < list.size(); i++) {
            if (avg < list.get(i).getChargedPercent()){
                return i;
            }
        }

        return -1;
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
}
