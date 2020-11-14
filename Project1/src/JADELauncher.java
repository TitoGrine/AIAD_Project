import grid.ChargingHub;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import utils.Constants;
import utils.Data;
import vehicle.BroadVehicle;
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

            AgentController vh1;
            vh1 = mainContainer.acceptNewAgent("vh1", new BroadVehicle(0, 100, 0, false));
            vh1.start();

            AgentController vh2;
            vh2 = mainContainer.acceptNewAgent("vh2", new BroadVehicle(0, 100, 0.25, false));
            vh2.start();

            AgentController vh3;
            vh3 = mainContainer.acceptNewAgent("vh3", new BroadVehicle(0, 100, 0.5, false));
            vh3.start();

            AgentController vh4;
            vh4 = mainContainer.acceptNewAgent("vh4", new BroadVehicle(0, 100, 0.75, false));
            vh4.start();

            AgentController vh5;
            vh5 = mainContainer.acceptNewAgent("vh5", new BroadVehicle(0, 100, 1, false));
            vh5.start();

//            new Timer().scheduleAtFixedRate(new VehicleTrafficTask(mainContainer), 0, Constants.TRAFFIC_FREQUENCY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}