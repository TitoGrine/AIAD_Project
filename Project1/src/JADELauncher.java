import grid.ChargingHub;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import utils.Constants;
import utils.Data;
import vehicle.TwoWayVehicle;

import java.util.Timer;

public class JADELauncher {

    public static void main(String[] args) {
        Runtime rt = Runtime.instance();

        Profile p1 = new ProfileImpl();

        ContainerController mainContainer = rt.createMainContainer(p1);

        Data.createFiles();

        try {
            AgentController acRMA;
            acRMA = mainContainer.acceptNewAgent("rma", new jade.tools.rma.rma());
            acRMA.start();

            AgentController acHub;
            Agent chub =  new ChargingHub(rt, mainContainer, Constants.CHARGING_STATIONS);

            acHub = mainContainer.acceptNewAgent("Charging_Hub", chub);
            acHub.start();

            //new Timer().scheduleAtFixedRate(new VehicleTrafficTask(mainContainer), 0, Constants.TRAFFIC_FREQUENCY);

            Agent v1 = new TwoWayVehicle(10, 100, 0.0, false);
            AgentController acV1 = mainContainer.acceptNewAgent("Vehicle 1", v1);

            Agent v2 = new TwoWayVehicle(90, 100, 0.1, true);
            AgentController acV2 = mainContainer.acceptNewAgent("Vehicle 2", v2);

            Agent v3 = new TwoWayVehicle(95, 100, 0.1, true);
            AgentController acV3 = mainContainer.acceptNewAgent("Vehicle 3", v3);

            acV1.start();
            acV2.start();
            acV3.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}