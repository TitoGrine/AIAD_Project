package vehicle.behaviour;

import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import vehicle.Vehicle;

import java.util.Vector;

public class OneWayRequestBehaviour extends AchieveREInitiator {
    private Vehicle vehicle;

    public OneWayRequestBehaviour(Vehicle vehicle, ACLMessage msg) {
        super(vehicle, msg);
        this.vehicle = vehicle;
    }

    public Vector<ACLMessage> prepareRequests(ACLMessage request){
        Vector<ACLMessage> requestVec = new Vector<>();
        requestVec.add(request);
        return requestVec;
    }

    public void handleAgree(ACLMessage msg){
        System.out.println(msg);
    }

    public void handleRefuse(ACLMessage msg){
        System.out.println(msg);
    }

    public void handleFailure(ACLMessage msg){
        System.out.println(msg);
    }

    public void handleInform(ACLMessage msg){
        System.out.println(msg);
    }
}
