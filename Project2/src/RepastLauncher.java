import grid.ChargingHub;
import jade.core.Profile;
import jade.core.ProfileImpl;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.AgentController;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.analysis.BinDataSource;
import uchicago.src.sim.analysis.Histogram;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import utils.Constants;
import utils.Data;
import vehicle.StatusResponse;

import java.util.ArrayList;
import java.util.Timer;

public class RepastLauncher extends Repast3Launcher {
    private static boolean runInBatchMode = false;
    private Histogram vehiclePlot;
    private OpenSequenceGraph gridDemandPlot;
    private ChargingHub chub;
    private ArrayList<StatusResponse> vehicles = new ArrayList<>();

    @Override
    public void begin() {
        if (!runInBatchMode) {
            buildPlots();
            buildSchedule();
        }
        super.begin();
    }

    private void buildSchedule() {
        getSchedule().scheduleActionAtInterval(10, vehiclePlot, "step", Schedule.LAST);
    }

    private void buildPlots() {
        buildHistogram();
        buildSequenceGraph();
    }

    private void buildSequenceGraph() {
        // graph
        if (gridDemandPlot != null) gridDemandPlot.dispose();
        gridDemandPlot = new OpenSequenceGraph("Peak demand", this);
        gridDemandPlot.setAxisTitles("time", "demand");
        gridDemandPlot.addSequence("Grid demand (kWh)", new Sequence() {
            public double getSValue() {
                return chub.getGridLoad();
            }
        });
        gridDemandPlot.addSequence("Max demand (kWh)", new Sequence() {
            private double maxDemand = 0;

            public double getSValue() {
                double currentDemand = chub.getGridLoad();
                if (currentDemand > maxDemand)
                    maxDemand = currentDemand;

                return maxDemand;
            }
        });
        gridDemandPlot.display();
    }

    private void buildHistogram() {
        if (vehiclePlot != null) vehiclePlot.dispose();
        vehiclePlot = new Histogram("Agent Type Distribution", new double[]{-0.5, 0.0, 1.0, 2.0, 3.0}, this);
        // 0 - OneWay - 1); 1 - TwoWay - 2); 2 - Broad - 3
        vehiclePlot.setYRange(0, Constants.CHARGING_STATIONS);

        BinDataSource source = new BinDataSource() {
            public double getBinValue(Object o) {
                StatusResponse status = (StatusResponse) o;
                return status.getType();
            }
        };

        vehiclePlot.createHistogramItem("Vehicle Types", vehicles, source);
        vehiclePlot.display();
    }

    @Override
    protected void launchJADE() {
        Runtime rt = Runtime.instance();

        Profile p1 = new ProfileImpl();

        ContainerController mainContainer = rt.createMainContainer(p1);

        Data.createFiles();

        try {
            AgentController acHub;
            chub = new ChargingHub(mainContainer, Constants.CHARGING_STATIONS);
            if (!runInBatchMode) {
                chub.setDataList(vehicles);
                chub.setPlot(gridDemandPlot);
            }
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

    public static void main(String[] args) {
        SimInit init = new SimInit();
        init.setNumRuns(1);
        init.loadModel(new RepastLauncher(), null, runInBatchMode);
    }
}