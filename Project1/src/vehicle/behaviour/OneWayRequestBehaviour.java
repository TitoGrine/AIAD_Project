package vehicle.behaviour;

import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import vehicle.Vehicle;

public class OneWayRequestBehaviour extends AchieveREInitiator {
    private Vehicle vehicle;

    public OneWayRequestBehaviour(Vehicle vehicle, ACLMessage msg) {
        super(vehicle, msg);
        this.vehicle = vehicle;
    }

    public void handleAgree(ACLMessage msg){
        System.out.println(vehicle.getLocalName() + " - received agree: " + msg.getContent());
    }

    public void handleRefuse(ACLMessage msg){
        System.out.println(vehicle.getLocalName() + " - received refuse: " + msg.getContent());
    }

    public void handleFailure(ACLMessage msg){
        System.out.println(vehicle.getLocalName() + " - received failure: " + msg.getContent());
    }

    public void handleInform(ACLMessage msg){
        System.out.println(vehicle.getLocalName() + " - received inform: " + msg.getContent());
    }
}
