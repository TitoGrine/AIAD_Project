package grid;

import grid.behaviour.RequestStatusBehaviour;
import grid.behaviour.SubscriptionBehaviour;
import grid.behaviour.TimerBehaviour;
import grid.behaviour.Vehicle2GridBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javafx.util.Pair;
import sajas.core.Agent;
import sajas.domain.DFService;
import sajas.proto.SubscriptionResponder;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.RectNetworkItem;
import uchicago.src.sim.network.DefaultDrawableNode;
import uchicago.src.sim.network.DefaultEdge;
import uchicago.src.sim.network.EdgeFactory;
import utils.Constants;
import utils.Data;
import utils.Edge;
import utils.Utilities;
import vehicle.StatusResponse;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

public class ChargingHub extends Agent {
    private int availableLoad; // in kWh
    private int numStations;
    private int occupiedStations;
    private double localTime = Constants.START_TIME;
    private Map<AID, StatusResponse> systemStatus;
    private Grid grid = new Grid();
    private double chargingPrice = Constants.CHARGING_PRICE;
    private double sharedLoad = 0;

    private ArrayList<StatusResponse> dataList;
    private OpenSequenceGraph v2gPlot;
    private OpenSequenceGraph chubPlot;

    private static List<DefaultDrawableNode> agents;
    private Object callable;
    private Method updateCall;

    private SubscriptionBehaviour chargingSubscription;
    private TimerBehaviour timerBehaviour;

    public ChargingHub(ContainerController container, int numStations) {
        this.numStations = numStations;
        this.occupiedStations = 0;
        systemStatus = new HashMap<>();

        MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.CANCEL), MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE));
        this.chargingSubscription = new SubscriptionBehaviour(this, mt);
        this.timerBehaviour = new TimerBehaviour(container, this, Constants.TICK_FREQUENCY);
    }

    public void setup() {
        Utilities.registerService(this, Constants.CHUB_SERVICE);
        addBehaviour(chargingSubscription);
        addBehaviour(timerBehaviour);
    }

    @Override
    public void takeDown() {
        try {
            DFService.deregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateVehicleStatus(AID vehicle, StatusResponse status) {
        systemStatus.put(vehicle, status);
    }

    public void updateSystemStatus() {
        this.localTime += Constants.TICK_RATIO % 24;
        this.availableLoad = grid.getLoad((int) this.localTime, (int) ((this.localTime - (int) this.localTime) * 60));
        Utilities.printTime(((int) this.localTime), (int) ((this.localTime - (int) this.localTime) * 60));
        systemStatus.clear();
        addBehaviour(new RequestStatusBehaviour(this, new ACLMessage(ACLMessage.REQUEST)));
    }

    public void removeVehicleFromSystemStatus(AID vehicle) {
        systemStatus.remove(vehicle);
    }

    private double calculatePriority(StatusResponse st, double totalMissingBattery) {
        double missingBattery = st.getMaxCapacity() - st.getCurrentCapacity();
        double missingBatteryPercent = missingBattery / st.getMaxCapacity();

        return (missingBatteryPercent * missingBattery) / totalMissingBattery;
    }

    public void analyzeSystem() {
        if (dataList != null) {
            dataList.clear();
            dataList.addAll(systemStatus.values());
        }

        if(agents != null) {
            updateNetwork(systemStatus);
        }

        if (grid.getPeakLoad() > 0) {
            Utilities.printSystemMessage("starting a V2G round");
            List<AID> vehiclesForV2G = new ArrayList<>();
            StatusResponse status;

            for (AID vehicle : systemStatus.keySet()) {
                status = systemStatus.get(vehicle);

                if (status.allowsV2G())
                    vehiclesForV2G.add(vehicle);
            }

            if (vehiclesForV2G.size() > 0)
                addBehaviour(new Vehicle2GridBehaviour(this, grid.getPeakLoad(), vehiclesForV2G));
            else {
                addGridDataPoint(grid.getPeakLoad(), 0);
                this.sharedLoad = 0;
                plotStep();
                distributeLoad();
            }
        } else {
            this.sharedLoad = 0;
            plotStep();
            distributeLoad();
        }
    }

    private void updateNetwork(Map<AID, StatusResponse> vehicle_agents) {
        agents.clear();

        DefaultDrawableNode chargingHub = generateNode("Charging Hub", Color.yellow, Constants.DISPLAY_WIDTH / 6 - 5, Constants.DISPLAY_HEIGHT / 2 - 10, 10, 20);

        agents.add(chargingHub);

        double widthSize = Constants.DISPLAY_WIDTH / Constants.CHARGING_STATIONS;
        double heightSize = Constants.DISPLAY_HEIGHT * 0.7 / Constants.CHARGING_STATIONS;
        double heightInterval = Constants.DISPLAY_HEIGHT * 0.2 / Constants.CHARGING_STATIONS;
        int pos = 1;

        for(AID key : vehicle_agents.keySet()){
            StatusResponse response = vehicle_agents.get(key);

            DefaultDrawableNode chargePort = generateNode("Charging Port " + pos, Color.yellow,  Constants.DISPLAY_WIDTH / 3, pos * (heightSize + heightInterval) + heightInterval / 2, (int) heightSize, (int) heightSize);
            DefaultDrawableNode vehicle = generateNode("Vehicle " + key, getVehicleColor(response.getType()), 1.35 * Constants.DISPLAY_WIDTH / 3, pos * (heightSize + heightInterval) + heightInterval / 2, (int) widthSize, (int) heightSize);

            agents.add(chargePort);
            agents.add(vehicle);

            addEdge(chargingHub, chargePort, Color.yellow);
            addEdge(chargePort, vehicle, Color.yellow);

            pos++;
        }

        while(pos <= Constants.CHARGING_STATIONS){
            DefaultDrawableNode chargePort = generateNode("Charging Port " + pos, Color.LIGHT_GRAY,  Constants.DISPLAY_WIDTH / 3, pos * (heightSize + heightInterval) + heightInterval / 2, (int) heightSize, (int) heightSize);
            addEdge(chargingHub, chargePort, Color.gray);
            agents.add(chargePort);

            pos++;
        }

        drawRoad();

        try {
            updateCall.invoke(callable);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void drawRoad() {
        double heightSize = Constants.DISPLAY_HEIGHT * 0.8 / 6;
        double heightInterval = Constants.DISPLAY_HEIGHT * 0.6 / 6;

        agents.add(generateNode("", Color.DARK_GRAY, Constants.DISPLAY_WIDTH * 0.8, 0, (int) (Constants.DISPLAY_WIDTH * 0.01), Constants.DISPLAY_HEIGHT));
        agents.add(generateNode("", Color.WHITE, Constants.DISPLAY_WIDTH * 0.9, (int) (heightSize + heightInterval) - heightInterval, (int) (Constants.DISPLAY_WIDTH * 0.02), (int) heightSize));
        agents.add(generateNode("", Color.WHITE, Constants.DISPLAY_WIDTH * 0.9, (int) (2 * (heightSize + heightInterval) - heightInterval), (int) (Constants.DISPLAY_WIDTH * 0.02), (int) heightSize));
        agents.add(generateNode("", Color.WHITE, Constants.DISPLAY_WIDTH * 0.9, (int) (3 * (heightSize + heightInterval) - heightInterval), (int) (Constants.DISPLAY_WIDTH * 0.02), (int) heightSize));
        agents.add(generateNode("", Color.WHITE, Constants.DISPLAY_WIDTH * 0.9, (int) (4 * (heightSize + heightInterval) - heightInterval), (int) (Constants.DISPLAY_WIDTH * 0.02), (int) heightSize));
    }

    private void addEdge(DefaultDrawableNode startNode, DefaultDrawableNode targetNode, Color color){
        EdgeFactory.createDrawableEdge(startNode, targetNode);
        Edge edge = new Edge(startNode, targetNode);
        edge.setColor(color);
        startNode.addOutEdge(edge);
    }

    private Color getVehicleColor(int type){
        switch (type) {
            case Constants.ONEWAY_VEHICLE:
                return Color.cyan;
                case Constants.TWOWAY_VEHICLE:
                    return Color.blue;
                    case Constants.BROAD_VEHICLE:
                        return Color.green;
            default:
                return Color.white;
        }
    }

    private DefaultDrawableNode generateNode(String label, Color color, double x, double y, int width, int height) {
        RectNetworkItem rect = new RectNetworkItem(x,y);
        rect.allowResizing(false);
        rect.setHeight(height);
        rect.setWidth(width);

        DefaultDrawableNode node = new DefaultDrawableNode(label, rect);
        node.setColor(color);

        return node;
    }

    public void distributeLoad() {
        Map<AID, Double> loadDistribution = new HashMap<>();
        PriorityQueue<Pair<AID, Double>> priorityQueue = new PriorityQueue<>((a, b) -> {
            if (a.getValue() > b.getValue()) return -1;
            else if (a.getValue() < b.getValue()) return 1;
            return 0;
        });
        double totalPriority = 0;
        double totalGivenByVehicles = 0;

        // 1st part: Calculate total missing battery
        int totalMissingBattery = getTotalMissingBattery();

        if (totalMissingBattery == 0) {
            for (AID vehicle : systemStatus.keySet()) {
                loadDistribution.put(vehicle, 0.0);
            }
        } else {
            // 2nd part: Calculate priorities and total priority
            for (Map.Entry<AID, StatusResponse> entry : systemStatus.entrySet()) {
                StatusResponse status = entry.getValue();

                double priority = calculatePriority(status, totalMissingBattery);
                totalPriority += priority;
                priorityQueue.add(new Pair<>(entry.getKey(), priority));

                double fairShare = this.availableLoad / (double) this.systemStatus.size();
                double given = 0;
                if (status.getAltruistFactor() != -1) {
                    given = fairShare * status.getAltruistFactor() / 2.0;
                    totalGivenByVehicles += given;
                }
                // Temporarily saves the amount that is assured to each car
                loadDistribution.put(entry.getKey(), fairShare - given);
            }

            // 4th part: go through the priority queue and allocate more battery according to priority
            int totalLoad = distributeRemainingLoad(loadDistribution, priorityQueue, totalPriority, totalGivenByVehicles);

            Utilities.printChargingHubMessage("available load: " + this.availableLoad);
            Utilities.printChargingHubMessage("total allocated load: " + totalLoad);
        }

        notifyVehicles(loadDistribution, chargingSubscription.getSubscriptions());
    }

    private int distributeRemainingLoad(Map<AID, Double> loadDistribution, PriorityQueue<Pair<AID, Double>> priorityQueue, double totalPriority, double totalGivenByVehicles) {
        int totalLoad = 0;

        while (!priorityQueue.isEmpty()) {
            Pair<AID, Double> pair = priorityQueue.poll();
            double allocatedLoad = loadDistribution.get(pair.getKey()) + (totalGivenByVehicles * pair.getValue() / totalPriority);
            totalLoad += allocatedLoad;
            loadDistribution.put(pair.getKey(), allocatedLoad);
        }
        return totalLoad;
    }

    private int getTotalMissingBattery() {
        int totalMissingBattery = 0;

        for (StatusResponse status : systemStatus.values()) {
            totalMissingBattery += status.getMaxCapacity() - status.getCurrentCapacity();
        }
        return totalMissingBattery;
    }

    public void notifyVehicles(Map<AID, Double> loadDistribution, Vector<SubscriptionResponder.Subscription> subscriptions) {
        ACLMessage msg;

        if (loadDistribution.size() > 0) {
            try {
                for (SubscriptionResponder.Subscription subscription : subscriptions) {
                    if (systemStatus.containsKey(subscription.getMessage().getSender())) {
                        msg = new ACLMessage(ACLMessage.INFORM);
                        msg.setContentObject(new ChargingConditions(loadDistribution.get(subscription.getMessage().getSender()).intValue()));
                        subscription.notify(msg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addGridDataPoint(int peakLoad, int totalSharedLoad) {
        Data.submitGridStat(String.valueOf(peakLoad), String.valueOf((int) (100 * totalSharedLoad / peakLoad)));
    }

    public void setSharedLoad(double sharedLoad) {
        this.sharedLoad = sharedLoad;
    }

    public double getSharedLoad() {
        return sharedLoad;
    }

    public int getOccupiedStations() {
        return occupiedStations;
    }

    public int getNumStations() {
        return numStations;
    }

    public Vector<SubscriptionResponder.Subscription> getChargingVehicles() {
        return chargingSubscription.getSubscriptions();
    }

    public void addVehicle() {
        occupiedStations++;
    }

    public void removeVehicle() {
        occupiedStations--;
    }

    public double getGridLoad() {
        return grid.getCurrentLoad((int) this.localTime, (int) ((this.localTime - (int) this.localTime) * 60));
    }

    public double getChargingPrice() {
        return chargingPrice;
    }

    public double getDiscountPrice() {
        return chargingPrice * 0.5;
    }

    public void setDataList(ArrayList<StatusResponse> dataList) {
        this.dataList = dataList;
    }

    public void setPlots(OpenSequenceGraph v2gPlot, OpenSequenceGraph chubPlot) {
        this.v2gPlot = v2gPlot;
        this.chubPlot = chubPlot;
    }

    public void plotStep() {
        if (v2gPlot != null)
            v2gPlot.step();
        if (chubPlot != null)
            chubPlot.step();
    }

    public void closeSubscription(AID sender) {
        Vector subs = this.chargingSubscription.getSubscriptions(sender);
        for (Object sub : subs) {
            ((SubscriptionResponder.Subscription) sub).close();
        }
        removeVehicle();
    }

    public void setAgents(List<DefaultDrawableNode> agents) {
        ChargingHub.agents = agents;
    }

    public void setUpdateCall(Object callable, Method updateCall) {
        this.callable = callable;
        this.updateCall = updateCall;
    }
}


