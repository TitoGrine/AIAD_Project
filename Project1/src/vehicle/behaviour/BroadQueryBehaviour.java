package vehicle.behaviour;

import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;
import utils.Constants;
import utils.Utilities;
import vehicle.BroadVehicle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class BroadQueryBehaviour extends AchieveREInitiator {
    private ArrayList<DFAgentDescription> broadList;
    private BroadVehicle vehicle;

    public BroadQueryBehaviour(BroadVehicle a, ACLMessage msg) {
        super(a, msg);
        vehicle = a;

        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(Constants.BROAD_SERVICE);
        dfd.addServices(sd);

        try {
            broadList = new ArrayList<>(Arrays.asList(DFService.search(a, dfd)));
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Vector prepareRequests(ACLMessage request) {
        Vector<ACLMessage> msgs = new Vector<>();

        for (DFAgentDescription agent : broadList) {
            request = new ACLMessage(ACLMessage.QUERY_REF);
            request.setContent("Starting consensus. What is you proposal?");
            request.addReceiver(agent.getName());

            msgs.add(request);
        }

        return msgs;
    }

    public void handleAgree(ACLMessage msg) {
        Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(), "received agree: " + msg.getContent());
    }

    public void handleRefuse(ACLMessage msg) {
        Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(),"received refuse: " + msg.getContent());
    }

    public void handleFailure(ACLMessage msg) {
        Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(),"received failure: " + msg.getContent());
    }

    public void handleInform(ACLMessage msg) {
        try {
            Utilities.printVehicleMessage(vehicle.getLocalName(), vehicle.getVehicleType(),"received inform from " + msg.getSender().getLocalName() + ":\n" + msg.getContentObject());
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleAllResponses(Vector responses) {
        super.handleAllResponses(responses);
    }

    @Override
    protected void handleAllResultNotifications(Vector resultNotifications) {
    }
}
