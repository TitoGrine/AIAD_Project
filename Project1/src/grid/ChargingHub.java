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
import vehicle.StatusResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ChargingHub extends Agent {
    private int availableLoad; // in kWh
    private int numStations;
    private int occupiedStations;
    private double localTime  = Constants.START_TIME;
    private Map<AID, StatusResponse> systemStatus;
    private Grid grid = new Grid();

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
        this.localTime += Constants.TICK_RATIO % 24;
        this.availableLoad = grid.getLoad((int) this.localTime);
        System.out.println(String.format("Local time is %02d:%02d", ((int) this.localTime), (int) ((this.localTime - (int) this.localTime) * 60)));
        System.out.println("Available load is of " + availableLoad);
        systemStatus.clear();
        addBehaviour(new RequestStatusBehaviour(this, new ACLMessage(ACLMessage.REQUEST)));
    }

    public void distributeLoad() {
        Map<AID, Integer> loadDistribution = new HashMap<>();

        int totalNeededCapacity = 0;

        for(AID vehicle : systemStatus.keySet())
            totalNeededCapacity += systemStatus.get(vehicle).getMaxCapacity() - systemStatus.get(vehicle).getCurrentCapacity();

        for (AID vehicle : systemStatus.keySet())
            loadDistribution.put(vehicle, totalNeededCapacity == 0 ? 0 : (int) Math.floor(availableLoad * ((systemStatus.get(vehicle).getMaxCapacity() - systemStatus.get(vehicle).getCurrentCapacity()) / (double) totalNeededCapacity)));

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


