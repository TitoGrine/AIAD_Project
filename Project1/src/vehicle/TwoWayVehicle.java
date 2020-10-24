package vehicle;

import jade.lang.acl.ACLMessage;
import utils.Constants;
import vehicle.behaviour.OneWayRequestBehaviour;

import java.io.IOException;

public class TwoWayVehicle extends SmartVehicle {
    public TwoWayVehicle() {
        this(10, 50, 0.1f, false);
    }

    public TwoWayVehicle(int currentCapacity, int maxCapacity, float altruistFactor, boolean chargeGrid) {
        super(currentCapacity, maxCapacity, altruistFactor, chargeGrid);
    }

    @Override
    public void updateBattery(double newLoad) {
        currentCapacity = Math.min(this.maxCapacity, this.currentLoad * Constants.tick_ratio + this.currentCapacity);
        this.currentLoad = newLoad;
    }

    @Override
    public void initiateRequest(ACLMessage msg) {
        ACLMessage request = msg.createReply();
        request.setPerformative(ACLMessage.REQUEST);
        try {
            request.setContentObject(this.currentCapacity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        addBehaviour(new OneWayRequestBehaviour(this, request));
    }
}
