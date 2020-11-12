package grid;

import grid.behaviour.RequestStatusBehaviour;
import grid.behaviour.SubscriptionBehaviour;
import grid.behaviour.TimerBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionResponder;
import jade.wrapper.ContainerController;
import utils.Constants;
import javafx.util.Pair;
import vehicle.StatusResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Vector;

public class ChargingHub extends Agent {
    private int availableLoad; // in kWh
    //Grid simulator
    private int numStations;
    private int occupiedStations;
    private Map<AID, StatusResponse> systemStatus;

    private SubscriptionBehaviour chargingSubscription;
    private TimerBehaviour timerBehaviour;

    public ChargingHub(Runtime runtime, ContainerController container, int availableLoad, int numStations) {
        this.availableLoad = availableLoad;
        this.numStations = numStations;
        this.occupiedStations = 0;
        systemStatus = new HashMap<>();

        MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.CANCEL), MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE));
        this.chargingSubscription = new SubscriptionBehaviour(this, mt);
        this.timerBehaviour = new TimerBehaviour(runtime, container, this, Constants.TICK_FREQUENCY);
    }

    public void setup() {
        addBehaviour(chargingSubscription);
        addBehaviour(timerBehaviour);
    }

    public void updateVehicleStatus(AID vehicle, StatusResponse status) {
        systemStatus.put(vehicle, status);
    }

    public void updateSystemStatus() {
        systemStatus.clear();
        addBehaviour(new RequestStatusBehaviour(this, new ACLMessage(ACLMessage.REQUEST)));
    }

    private double calculatePriority(StatusResponse st, double totalMissingBattery) {
        double missingBattery = st.getMaxCapacity() - st.getCurrentCapacity();
        double missingBatteryPercent = missingBattery / st.getMaxCapacity();

        return (missingBatteryPercent + missingBattery) / totalMissingBattery;
    }

    public void distributeLoad() {
        Map<AID, Integer> loadDistribution = new HashMap<>();
        PriorityQueue<Pair<AID, Double>> priorityQueue = new PriorityQueue<>((a, b) -> {
            if(a.getValue() > b.getValue()) return -1;
            else if (a.getValue() < b.getValue()) return 1;
            return 0;
        });
        int totalMissingBattery = 0;
        double totalPriority = 0;
        double totalGivenByVehicles = 0;

        // 1st part: Calculate total missing battery
        for(StatusResponse status : systemStatus.values()) {
            totalMissingBattery += status.getMaxCapacity() - status.getCurrentCapacity();
        }

        if(totalMissingBattery == 0) {
            for(AID vehicle : systemStatus.keySet()) {
                loadDistribution.put(vehicle, 0);
            }
        } else {
            // 2nd part: Calculate priorities and total priority
            for (Map.Entry<AID, StatusResponse> entry : systemStatus.entrySet()) {
                double priority = calculatePriority(entry.getValue(), totalMissingBattery);
                totalPriority += priority;
                priorityQueue.add(new Pair<>(entry.getKey(), priority));
            }

            // 3rd part: iterate through all available vehicles and accumulate the amount each one is willing to give
            for (StatusResponse status : systemStatus.values()) {
                int fairShare = (status.getMaxCapacity() - status.getCurrentCapacity()) * this.availableLoad / totalMissingBattery;
                if (status.getAltruistFactor() != -1)
                    totalGivenByVehicles += fairShare * status.getAltruistFactor() / 2.0;
            }

            // 4th part: go through the priority queue and allocate more battery according to priority
            while (!priorityQueue.isEmpty()) {
                Pair<AID, Double> pair = priorityQueue.poll();
                StatusResponse status = systemStatus.get(pair.getKey());
                int fairShare = (status.getMaxCapacity() - status.getCurrentCapacity()) * this.availableLoad / totalMissingBattery;
                int allocatedLoad = fairShare + (int) (totalGivenByVehicles * pair.getValue() / totalPriority);
                loadDistribution.put(pair.getKey(), allocatedLoad);
            }
        }

        notifyVehicles(loadDistribution);
    }

    public void notifyVehicles(Map<AID, Integer> loadDistribution){
        Vector<SubscriptionResponder.Subscription> subscriptions = chargingSubscription.getSubscriptions();
        ACLMessage msg;

        try {
            for (SubscriptionResponder.Subscription subscription : subscriptions) {
                msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContentObject(loadDistribution.get(subscription.getMessage().getSender()));
                subscription.notify(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getOccupiedStations() {
        return occupiedStations;
    }

    public int getNumStations() {
        return numStations;
    }

    public Vector<SubscriptionResponder.Subscription> getChargingVehicles() {
        return chargingSubscription.getSubscriptions();
    }

    public void addVehicle() {
        occupiedStations++;
    }

    public void removeVehicle() {
        occupiedStations--;
    }
}


