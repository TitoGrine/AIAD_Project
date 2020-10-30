package grid;

import grid.behaviour.RequestStatusBehaviour;
import grid.behaviour.SubscriptionBehaviour;
import grid.behaviour.TimerBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionResponder;
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

        MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.CANCEL), MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE));
        this.chargingSubscription = new SubscriptionBehaviour(this, mt);
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

        double totalNeededCapacity = 0.0;

        for(AID vehicle : systemStatus.keySet())
            totalNeededCapacity += systemStatus.get(vehicle).getMaxCapacity() - systemStatus.get(vehicle).getCurrentCapacity();

        for (AID vehicle : systemStatus.keySet())
            loadDistribution.put(vehicle, totalNeededCapacity == 0 ? 0 : availableLoad * ((systemStatus.get(vehicle).getMaxCapacity() - systemStatus.get(vehicle).getCurrentCapacity()) / totalNeededCapacity));

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


