import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import vehicle.OneWayVehicle;

import java.util.TimerTask;
import java.util.UUID;

public class VehicleTrafficTask extends TimerTask {
    ContainerController mainContainer;

    public VehicleTrafficTask(ContainerController mainContainer){
        this.mainContainer = mainContainer;
    }

    @Override
    public void run() {
        AgentController ac1;
        String vehicleId = "vehicle_" + UUID.randomUUID().toString();
        Agent vehicle = new OneWayVehicle(30, 50);

        try {
            ac1 = mainContainer.acceptNewAgent(vehicleId, vehicle);
            ac1.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
