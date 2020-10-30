import grid.ChargingHub;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import utils.Constants;
import vehicle.OneWayVehicle;
import vehicle.TwoWayVehicle;

import java.util.Timer;

public class JADELauncher {

    public static void main(String[] args) {
        Runtime rt = Runtime.instance();

        Profile p1 = new ProfileImpl();

        ContainerController mainContainer = rt.createMainContainer(p1);

        try {
            AgentController acRMA;
            acRMA = mainContainer.acceptNewAgent("rma", new jade.tools.rma.rma());
            acRMA.start();

            AgentController acHub;
            Agent chub =  new ChargingHub(100, 3);

            acHub = mainContainer.acceptNewAgent("CHub", chub);
            acHub.start();

            new Timer().scheduleAtFixedRate(new VehicleTrafficTask(mainContainer), 0, Constants.TRAFFIC_FREQUENCY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
