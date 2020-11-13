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

public class JADELauncher {
    public static void main(String[] args) {
        Runtime rt = Runtime.instance();

        Profile p1 = new ProfileImpl();
        //p1.setParameter(...);
        ContainerController mainContainer = rt.createMainContainer(p1);

//        Profile p2 = new ProfileImpl();
        //p2.setParameter(...);

        try {
            AgentController acRMA;
            acRMA = mainContainer.acceptNewAgent("rma", new jade.tools.rma.rma());
            acRMA.start();


            AgentController acHub;
            Agent chub =  new ChargingHub(rt, mainContainer, Constants.AVAILABLE_LOAD, Constants.CHARGING_STATIONS);

            acHub = mainContainer.acceptNewAgent("Charging_Hub", chub);
            acHub.start();


            AgentController ac1;
//            Agent onev = new OneWayVehicle(0, 50);
//            ac1 = mainContainer.acceptNewAgent("onev", onev);
////            ac1 = mainContainer.acceptNewAgent("twov0", new TwoWayVehicle(70, 100, 1.0f, false));
//            ac1.start();


            AgentController ac2;
            ac2 = mainContainer.acceptNewAgent("twov1", new TwoWayVehicle(0, 100, 0.0, false));
            ac2.start();

            AgentController ac3;
            ac3 = mainContainer.acceptNewAgent("twov2", new TwoWayVehicle(0, 100, 0.3, false));
            ac3.start();

            AgentController ac4;
            ac4 = mainContainer.acceptNewAgent("twov3", new TwoWayVehicle(0, 100, 0.6, false));
            ac4.start();

            AgentController ac5;
            ac5 = mainContainer.acceptNewAgent("twov4", new TwoWayVehicle(0, 100, 1.0, false));
            ac5.start();


            /*AgentController ac3 = mainContainer.acceptNewAgent("broadv", new BroadVehicle(10, 100, 0.1f, true));
            ac3.start();*/


//            AgentController acSniffer;
//            Sniffer sniffer = new jade.tools.sniffer.Sniffer();
//            acSniffer = mainContainer.acceptNewAgent("sniffer", sniffer);
//            acSniffer.start();
//			ArrayList agentList = new ArrayList();
//			agentList.add(new jade.tools.sniffer.Agent("onev"));
//			agentList.add(new jade.tools.sniffer.Agent("twov"));
//			agentList.add(new jade.tools.sniffer.Agent("broadv"));

//            sniffer.sniffMsg(agentList, sniffer.SNIFF_ON);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}