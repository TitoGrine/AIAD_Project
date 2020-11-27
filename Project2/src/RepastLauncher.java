import grid.ChargingHub;
import sajas.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.AgentController;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.engine.SimInit;
import utils.Constants;
import utils.Data;

import java.util.Timer;

public class RepastLauncher extends Repast3Launcher {

    public static void main(String[] args) {
        SimInit init = new SimInit();
        init.setNumRuns(1);
        init.loadModel(new RepastLauncher(), null, false);
    }

    @Override
    protected void launchJADE() {
        Runtime rt = Runtime.instance();

        Profile p1 = new ProfileImpl();

        ContainerController mainContainer = rt.createMainContainer(p1);

        Data.createFiles();

        try {
            AgentController acHub;
            Agent chub =  new ChargingHub(rt, mainContainer, Constants.CHARGING_STATIONS);
            acHub = mainContainer.acceptNewAgent("Charging_Hub", chub);
            acHub.start();

            new Timer().scheduleAtFixedRate(new VehicleTrafficTask(mainContainer), 0, Constants.TRAFFIC_FREQUENCY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] getInitParam() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Vehicle to grid";
    }
}