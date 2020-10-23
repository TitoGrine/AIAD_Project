package grid;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionResponder;
import vehicle.VehicleSubscription;

import java.util.Vector;

public class ChargingHub extends Agent {
    private int availableLoad; // in kWh
    //Grid simulator
    private int numStations;
    private int occupiedStations;
    private ChargingSubscription chargingSubscription;

    public ChargingHub(int availableLoad, int numStations) {
        this.availableLoad = availableLoad;
        this.numStations = numStations;
        this.occupiedStations = 0;
        this.chargingSubscription = new ChargingSubscription(this, MessageTemplate.MatchAll());
    }

    public void setup(){
        addBehaviour(chargingSubscription);
        addBehaviour(new NotifyBehaviour(this, 5000));
    }

    public void notifySubscribers(){
        Vector<SubscriptionResponder.Subscription> subscriptions = chargingSubscription.getSubscriptions();
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("give battery");
        for(SubscriptionResponder.Subscription subscription : subscriptions){
            subscription.notify(msg);
        }

    }

    public class NotifyBehaviour extends WakerBehaviour{

        public NotifyBehaviour(Agent a, long timeout) {
            super(a, timeout);
        }

        public void onWake(){
            notifySubscribers();
        }
    }

    public class ChargingSubscription extends SubscriptionResponder{

        public ChargingSubscription(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        public ACLMessage handleSubscription(ACLMessage subscription){
            ACLMessage reply = subscription.createReply();
            if(occupiedStations < numStations){
                occupiedStations++;
                reply.setPerformative(ACLMessage.AGREE);
                reply.setContent("resposta");

                createSubscription(subscription);
            }
            else{
                reply.setPerformative(ACLMessage.REFUSE);
                reply.setContent("no");
            }
            return reply;
        }


    }


}


