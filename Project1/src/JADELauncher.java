import grid.ChargingHub;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.tools.sniffer.AgentList;
import jade.tools.sniffer.Sniffer;
import jade.util.leap.ArrayList;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import vehicle.BroadVehicle;
import vehicle.OneWayVehicle;
import vehicle.TwoWayVehicle;

import java.util.Arrays;

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
            Agent chub =  new ChargingHub(10, 3);

            acHub = mainContainer.acceptNewAgent("CHub", chub);
            acHub.start();


            AgentController ac1;
            Agent onev = new OneWayVehicle(30, 50);
            ac1 = mainContainer.acceptNewAgent("onev", onev);
            ac1.start();


            AgentController ac2;
            ac2 = mainContainer.acceptNewAgent("twov", new TwoWayVehicle(50, 60, 0.8f, false));
            ac2.start();


            AgentController ac3 = mainContainer.acceptNewAgent("broadv", new BroadVehicle(10, 100, 0.1f, true));
            ac3.start();


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
