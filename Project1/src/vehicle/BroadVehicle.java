package vehicle;

import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.Constants;
import vehicle.behaviour.SmartStatusResponseBehaviour;

public class BroadVehicle extends SmartVehicle {
    public BroadVehicle(int currentCapacity, int maxCapacity, double altruistFactor, boolean chargeGrid) {
        super(currentCapacity, maxCapacity, altruistFactor, chargeGrid);
    }

    private void registerService() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(this.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(Constants.BROAD_SERVICE);
        sd.setName(this.getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setup() {
        super.setup();
        registerService();
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addResponseBehaviour(ACLMessage msg) {
        addBehaviour(new SmartStatusResponseBehaviour(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
        //TODO: Add response behaviour.
    }

    @Override
    public int getVehicleType() {
        return Constants.BROAD_VEHICLE;
    }
}
