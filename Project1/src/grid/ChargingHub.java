package grid;

import grid.behaviour.RequestStatusBehaviour;
import grid.behaviour.SubscriptionBehaviour;
import grid.behaviour.TimerBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionResponder;
import utils.Constants;
import vehicle.StatusResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ChargingHub extends Agent {
    private double availableLoad; // in kWh
    //Grid simulator
    private int numStations;
    private int occupiedStations;
    private Map<AID, StatusResponse> systemStatus;

    private SubscriptionBehaviour chargingSubscription;

    public ChargingHub(double availableLoad, int numStations) {
        this.availableLoad = availableLoad;
        this.numStations = numStations;
        this.occupiedStations = 0;
        systemStatus = new HashMap<>();
        this.chargingSubscription = new SubscriptionBehaviour(this, MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE));
    }

    public void setup() {
        addBehaviour(chargingSubscription);
        addBehaviour(new TimerBehaviour(this, 5000));
    }

    public void updateVehicleStatus(AID vehicle, StatusResponse status) {
        systemStatus.put(vehicle, status);
    }

    public void updateSystemStatus() {
        systemStatus.clear();
        addBehaviour(new RequestStatusBehaviour(this, new ACLMessage(ACLMessage.REQUEST)));
    }

    public void distributeLoad() {
        Map<AID, Double> loadDistribution = new HashMap<>();
        double totalAllocatedLoad = 0;
        int notFullyChargedStations = occupiedStations;
        int smartVehiclesConnected = 0;
        double accAltruistFactor = 0;

        // 1st part: check if avgAF is above 1.0
        for(StatusResponse status : systemStatus.values()) {
            if(status.getMaxCapacity() == status.getCurrentCapacity())
                notFullyChargedStations--;
            else if (status.getAltruistFactor() != Constants.NO_FACTOR) {
                smartVehiclesConnected++;
                accAltruistFactor += status.getAltruistFactor();
            }
        }

        double avgAltruistFactor = accAltruistFactor / smartVehiclesConnected;
        double avgOffset = Math.max(0, avgAltruistFactor - 1.0); //Either 0 or the amount the avgAF is above 1.0
        double fairShare = availableLoad / notFullyChargedStations;

        System.out.format("\n AVG ALTRUIST FACTOR: %f\n", avgAltruistFactor);
        System.out.format("\n AVG ALTRUIST FACTOR OFFSET: %f\n", avgOffset);
        System.out.format("\n FAIR SHARE: %f\n", fairShare);

        accAltruistFactor = 0;
        // 2nd part: calculate the allocated load to each non charged vehicle
        for(AID vehicle : systemStatus.keySet()) {
            StatusResponse status = systemStatus.get(vehicle);
            double af = status.getAltruistFactor();
            if(af != Constants.NO_FACTOR) {
                af = status.getAltruistFactor() - avgOffset;
                accAltruistFactor += af;
            }

            double missingLoad = status.getMaxCapacity() - status.getCurrentCapacity();
            double allocatedLoad = Math.abs(af) * fairShare;
            double load = Math.min(missingLoad, allocatedLoad);

            loadDistribution.put(vehicle, load);
            totalAllocatedLoad += load;
        }

        avgAltruistFactor = accAltruistFactor / smartVehiclesConnected;

        System.out.format("\n ACTUAL AVG ALTRUIST FACTOR : %f\n", avgAltruistFactor);
        System.out.format("\n\n TOTAL LOAD : %f\n\n", totalAllocatedLoad);

        notifyVehicles(loadDistribution);
    }

    public void notifyVehicles(Map<AID, Double> loadDistribution){
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


