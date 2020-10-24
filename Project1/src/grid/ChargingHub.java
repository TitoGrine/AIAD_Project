package grid;

import grid.behaviour.NotifyBehaviour;
import grid.behaviour.RequestResponderBehaviour;
import grid.behaviour.SubscriptionBehaviour;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import jade.proto.SubscriptionResponder;

import java.util.Vector;

public class ChargingHub extends Agent {
    private int availableLoad; // in kWh
    //Grid simulator
    private int numStations;
    private int occupiedStations;
    private SubscriptionBehaviour chargingSubscription;

    public ChargingHub() {
        this(10,10);
    }

    public ChargingHub(int availableLoad, int numStations) {
        this.availableLoad = availableLoad;
        this.numStations = numStations;
        this.occupiedStations = 0;
        this.chargingSubscription = new SubscriptionBehaviour(this, MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE));
    }

    public void setup(){
        addBehaviour(chargingSubscription);
        addBehaviour(new NotifyBehaviour(this, 5000));
        addBehaviour(new RequestResponderBehaviour(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
        addBehaviour(new TestBehaviour(this, MessageTemplate.MatchPerformative(ACLMessage.PROPOSE)));
    }

    public void notifySubscribers(){
        Vector<SubscriptionResponder.Subscription> subscriptions = chargingSubscription.getSubscriptions();
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("give battery");
        for(SubscriptionResponder.Subscription subscription : subscriptions){
            subscription.notify(msg);
        }

    }

    public int getOccupiedStations() {
        return occupiedStations;
    }

    public int getNumStations() {
        return numStations;
    }


    public void addVehicle(){
        occupiedStations++;
    }

    public void removeVehicle(){
        occupiedStations--;
    }

    class TestBehaviour extends AchieveREResponder {
        public TestBehaviour(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        @Override
        protected ACLMessage handleRequest(ACLMessage request) {
            notifySubscribers();

            return null;
        }
    }
}


