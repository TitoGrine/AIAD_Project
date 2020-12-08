import grid.ChargingHub;
import jade.core.Profile;
import jade.core.ProfileImpl;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.AgentController;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.analysis.*;
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
    private OpenSequenceGraph v2gPlot;
    private OpenSequenceGraph hubPlot;
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
        buildV2GPlot();
        buildHubPlot();
    }

    private void buildHubPlot() {
        if (hubPlot != null) hubPlot.dispose();
        hubPlot = new OpenSequenceGraph("Charging Hub metrics", this);
        hubPlot.setAxisTitles("time", "%");
        hubPlot.addSequence("Average Battery (%)", new Sequence() {
            public double getSValue() {
                double sum = 0;

                for(StatusResponse vehicle : vehicles) {
                    sum += vehicle.getBatteryPercentage();
                }

                return vehicles.size() == 0 ? sum : sum / vehicles.size();
            }
        });
        hubPlot.display();
    }

    private void buildV2GPlot() {
        if (v2gPlot != null) v2gPlot.dispose();
        v2gPlot = new OpenSequenceGraph("Vehicle to Grid metrics", this);
        v2gPlot.setAxisTitles("time", "energy");
        v2gPlot.addSequence("Grid demand (kWh)", new Sequence() {
            public double getSValue() {
                return chub.getGridLoad();
            }
        });
        v2gPlot.addSequence("Peak threshold (kWh)", new Sequence() {
            public double getSValue() {
                return 0.8 * Constants.MAX_AVAILABLE_LOAD;
            }
        });
        v2gPlot.addSequence("Suppressed demand (kWh)", new Sequence() {
            public double getSValue() {
                return chub.getSharedLoad();
            }
        });
        v2gPlot.display();
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
                chub.setPlots(v2gPlot, hubPlot);
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