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
import utils.Constants;
import utils.Data;
import utils.Utilities;
import vehicle.StatusResponse;

import java.io.IOException;
import java.util.*;

public class ChargingHub extends Agent {
    private int availableLoad; // in kWh
    private int numStations;
    private int occupiedStations;
    private double localTime = Constants.START_TIME;
    private Map<AID, StatusResponse> systemStatus;
    private ArrayList<StatusResponse> dataList;
    private OpenSequenceGraph plot;
    private Grid grid = new Grid();
    private double chargingPrice = Constants.CHARGING_PRICE;

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
                distributeLoad();
                plotStep();
            }
        } else {
            distributeLoad();
            plotStep();
        }
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
        return grid.getLoad((int) this.localTime, (int) ((this.localTime - (int) this.localTime) * 60));
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

    public void setPlot(OpenSequenceGraph plot) {
        this.plot = plot;
    }

    public void plotStep() {
        if (plot != null)
            plot.step();
    }

    public void closeSubscription(AID sender) {
        Vector subs = this.chargingSubscription.getSubscriptions(sender);
        for (Object sub : subs) {
            ((SubscriptionResponder.Subscription) sub).close();
        }
        removeVehicle();
    }
}


