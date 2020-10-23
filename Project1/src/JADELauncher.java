import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import vehicle.BroadVehicle;
import vehicle.OneWayVehicle;
import vehicle.TwoWayVehicle;

public class JADELauncher {

	public static void main(String[] args) {
		Runtime rt = Runtime.instance();

		Profile p1 = new ProfileImpl();
		//p1.setParameter(...);
		ContainerController mainContainer = rt.createMainContainer(p1);

		Profile p2 = new ProfileImpl();
		//p2.setParameter(...);
		ContainerController container = rt.createAgentContainer(p2);

		AgentController ac1;
		try {
			ac1 = container.acceptNewAgent("onev", new OneWayVehicle(30, 50));
			ac1.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

		AgentController ac2;
		try {
			ac2 = container.acceptNewAgent("twov", new TwoWayVehicle(50, 60, 0.8f, false));
			ac2.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

		AgentController ac4;
		try {
			ac4 = container.acceptNewAgent("broadv", new BroadVehicle(10, 100, 0.1f, true));
			ac4.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

		AgentController ac3;
		try {
			ac3 = mainContainer.acceptNewAgent("rma", new jade.tools.rma.rma());
			ac3.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}

}
