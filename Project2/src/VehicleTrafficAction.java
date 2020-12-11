import sajas.core.Agent;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.AgentController;
import sajas.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import uchicago.src.sim.engine.BasicAction;
import utils.Constants;
import utils.Utilities;
import vehicle.BroadVehicle;
import vehicle.OneWayVehicle;
import vehicle.TwoWayVehicle;

import java.util.TimerTask;
import java.util.UUID;

public class VehicleTrafficAction extends BasicAction {
    ContainerController mainContainer;

    public VehicleTrafficAction(ContainerController mainContainer) {
        this.mainContainer = mainContainer;
    }

    public Agent getRandomVehicle() {
        int type = Utilities.randomVehicleType();
        int maxCapacity = Utilities.randomNumber(Constants.CAPACITY_DISTRIBUTION[Constants.MIN], Constants.CAPACITY_DISTRIBUTION[Constants.MAX]);
        int currentCapacity = Utilities.randomNumber(0.05 * maxCapacity, 0.95 * maxCapacity);

        switch (type) {
            case Constants.ONEWAY_VEHICLE:
                return new OneWayVehicle(currentCapacity, maxCapacity);

            case Constants.TWOWAY_VEHICLE:
                return new TwoWayVehicle(currentCapacity, maxCapacity, Utilities.randomAltruisticFactor(), Utilities.chargeGridPermission());

            case Constants.BROAD_VEHICLE:
                return new BroadVehicle(currentCapacity, maxCapacity, Utilities.randomAltruisticFactor(), Utilities.chargeGridPermission());

            default:
                return null;
        }
    }

    public void addNewVehicle() throws StaleProxyException {
        AgentController agent;
        String vehicleID = "vehicle_" + UUID.randomUUID().toString();
        Agent vehicle = getRandomVehicle();

        agent = mainContainer.acceptNewAgent(vehicleID, vehicle);
        agent.start();
    }

    public void execute() {
        int numberNewCars = Utilities.randomNumber(0, Constants.CAR_TRAFFIC);

        Utilities.printSystemMessage(numberNewCars + " vehicles attempted to connect to the charging hub.");

        try {
            while (numberNewCars > 0) {
                addNewVehicle();
                numberNewCars--;
            }
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
