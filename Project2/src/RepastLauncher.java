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
    private double ONE_WAY_VEHICLE_DISTRIBUTION = Constants.VEHICLE_DISTRIBUTION[0];
    private double TWO_WAY_VEHICLE_DISTRIBUTION = Constants.VEHICLE_DISTRIBUTION[1];
    private double BROAD_VEHICLE_DISTRIBUTION = Constants.VEHICLE_DISTRIBUTION[2];
    private double MEAN_ALTRUISTIC_VALUE = Constants.MEAN_ALTRUISTIC_VALUE;
    private double ALTRUISTIC_STANDARD_DEVIATION = Constants.ALTRUISTIC_STANDARD_DEVIATION;
    private double MEAN_PERMISSION_VALUE = Constants.MEAN_PERMISSION_VALUE;
    private double PERMISSION_STANDARD_DEVIATION = Constants.PERMISSION_STANDARD_DEVIATION;
    private String SEASON = "SUMMER";

    private static boolean runInBatchMode = false;
    private Histogram vehiclePlot;
    private OpenSequenceGraph v2gPlot;
    private OpenSequenceGraph hubPlot;
    private ChargingHub chub;
    private ArrayList<StatusResponse> vehicles = new ArrayList<>();

    public double getONE_WAY_VEHICLE_DISTRIBUTION() {
        return ONE_WAY_VEHICLE_DISTRIBUTION;
    }

    public void setONE_WAY_VEHICLE_DISTRIBUTION(double ONE_WAY_VEHICLE_DISTRIBUTION) {
        this.ONE_WAY_VEHICLE_DISTRIBUTION = ONE_WAY_VEHICLE_DISTRIBUTION;
        Constants.VEHICLE_DISTRIBUTION[0] = this.ONE_WAY_VEHICLE_DISTRIBUTION;
    }

    public double getTWO_WAY_VEHICLE_DISTRIBUTION() {
        return TWO_WAY_VEHICLE_DISTRIBUTION;
    }

    public void setTWO_WAY_VEHICLE_DISTRIBUTION(double TWO_WAY_VEHICLE_DISTRIBUTION) {
        this.TWO_WAY_VEHICLE_DISTRIBUTION = TWO_WAY_VEHICLE_DISTRIBUTION;
        Constants.VEHICLE_DISTRIBUTION[1] = this.TWO_WAY_VEHICLE_DISTRIBUTION;
    }

    public double getBROAD_VEHICLE_DISTRIBUTION() {
        return BROAD_VEHICLE_DISTRIBUTION;
    }

    public void setBROAD_VEHICLE_DISTRIBUTION(double BROAD_VEHICLE_DISTRIBUTION) {
        this.BROAD_VEHICLE_DISTRIBUTION = BROAD_VEHICLE_DISTRIBUTION;
        Constants.VEHICLE_DISTRIBUTION[2] = this.BROAD_VEHICLE_DISTRIBUTION;
    }

    public double getMEAN_ALTRUISTIC_VALUE() {
        return MEAN_ALTRUISTIC_VALUE;
    }

    public void setMEAN_ALTRUISTIC_VALUE(double MEAN_ALTRUISTIC_VALUE) {
        this.MEAN_ALTRUISTIC_VALUE = MEAN_ALTRUISTIC_VALUE;
        Constants.MEAN_ALTRUISTIC_VALUE = this.MEAN_ALTRUISTIC_VALUE;
    }

    public double getALTRUISTIC_STANDARD_DEVIATION() {
        return ALTRUISTIC_STANDARD_DEVIATION;
    }

    public void setALTRUISTIC_STANDARD_DEVIATION(double ALTRUISTIC_STANDARD_DEVIATION) {
        this.ALTRUISTIC_STANDARD_DEVIATION = ALTRUISTIC_STANDARD_DEVIATION;
        Constants.ALTRUISTIC_STANDARD_DEVIATION = this.ALTRUISTIC_STANDARD_DEVIATION;
    }

    public double getMEAN_PERMISSION_VALUE() {
        return MEAN_PERMISSION_VALUE;
    }

    public void setMEAN_PERMISSION_VALUE(double MEAN_PERMISSION_VALUE) {
        this.MEAN_PERMISSION_VALUE = MEAN_PERMISSION_VALUE;
        Constants.MEAN_PERMISSION_VALUE = this.MEAN_PERMISSION_VALUE;
    }

    public double getPERMISSION_STANDARD_DEVIATION() {
        return PERMISSION_STANDARD_DEVIATION;
    }

    public void setPERMISSION_STANDARD_DEVIATION(double PERMISSION_STANDARD_DEVIATION) {
        this.PERMISSION_STANDARD_DEVIATION = PERMISSION_STANDARD_DEVIATION;
        Constants.PERMISSION_STANDARD_DEVIATION = this.PERMISSION_STANDARD_DEVIATION;
    }

    public String getSEASON() {
        return SEASON;
    }

    public void setSEASON(String season) {
        this.SEASON = season;
        switch (season) {
            case "WINTER":
                Constants.CURRENT_DEMAND = Constants.WINTER_GRID_DEMAND;
                Constants.MAX_AVAILABLE_LOAD = Constants.WINTER_MAX_AVAILABLE_LOAD;
                break;
            case "SPRING":
                Constants.CURRENT_DEMAND = Constants.SPRING_GRID_DEMAND;
                Constants.MAX_AVAILABLE_LOAD = Constants.SPRING_MAX_AVAILABLE_LOAD;
                break;
            case "SUMMER":
                Constants.CURRENT_DEMAND = Constants.SUMMER_GRID_DEMAND;
                Constants.MAX_AVAILABLE_LOAD = Constants.SUMMER_MAX_AVAILABLE_LOAD;
                break;
            case "AUTUMN":
                Constants.CURRENT_DEMAND = Constants.AUTUMN_GRID_DEMAND;
                Constants.MAX_AVAILABLE_LOAD = Constants.AUTUMN_MAX_AVAILABLE_LOAD;
                break;
        }
    }

    @Override
    public String[] getInitParam() {
        return new String[]{"ONE_WAY_VEHICLE_DISTRIBUTION", "TWO_WAY_VEHICLE_DISTRIBUTION", "BROAD_VEHICLE_DISTRIBUTION", "MEAN_ALTRUISTIC_VALUE", "ALTRUISTIC_STANDARD_DEVIATION", "MEAN_PERMISSION_VALUE", "PERMISSION_STANDARD_DEVIATION", "SEASON"};
    }

    @Override
    public String getName() {
        return "Vehicle to grid";
    }

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
        hubPlot.setAxisTitles("time", " ");
        hubPlot.addSequence("Average Battery (%)", new Sequence() {
            public double getSValue() {
                double sum = 0;

                for (StatusResponse vehicle : vehicles) {
                    sum += vehicle.getBatteryPercentage();
                }

                return vehicles.size() == 0 ? sum : sum / vehicles.size();
            }
        });
        hubPlot.addSequence("Average Charging Price (€)", new Sequence() {
            public double getSValue() {
                double sum = 0;

                for (StatusResponse vehicle : vehicles) {
                    sum += vehicle.getPriceToPay();
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

    public static void main(String[] args) {
        SimInit init = new SimInit();
        init.setNumRuns(1);
        init.loadModel(new RepastLauncher(), null, runInBatchMode);
    }
}