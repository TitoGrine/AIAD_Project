package utils;

public class Constants {
    public final static int MIN = 0;
    public final static int MAX = 1;

    public final static int ONEWAY_VEHICLE = 0;
    public final static int TWOWAY_VEHICLE = 1;
    public final static int BROAD_VEHICLE  = 2;

    public final static double NO_FACTOR = -1.0;
    public final static int ALLOW_DISCONNECT = -200;

    // System characteristics
    public final static long TICK_FREQUENCY = 5000;                         // How many (real time) milliseconds each tick is worth
    public final static int CYCLE_TICKS = 2;                               // How many ticks are done in one simulation
    public final static double TICK_RATIO = 0.25;                           // How many minutes (simulation time) each tick is worth (0.25 is a 15 minutes)
    public final static int TRAFFIC_FREQUENCY = 3000;                       // Frequency to which vehicles can enter the system in seconds
    public final static int CAR_TRAFFIC = 3;                                // Maximum number of vehicles that can attempt to charge at each interval
    public final static double EXIT_PROBABILITY = 0.05;                     // Base probability for a car to leave
    public final static int AVAILABLE_LOAD = 200;                           // Available load the charging hub can output
    public final static int CHARGING_STATIONS = 10;                         // Number of charging stations the charging hub has
    public final static double[] VEHICLE_DISTRIBUTION = {0.4, 0.6, 0.0};    // Relative frequency of each type of vehicle
    public final static int[] CAPACITY_DISTRIBUTION = {30, 100};            // Range of maximum capacities of vehicles in kWh
    public final static double MEAN_ALTRUISTIC_VALUE = 0.8;                 // Mean value of altruistic factor of normal distribution
    public final static double ALTRUISTIC_STANDARD_DEVIATION = 0.2;         // Standard deviation of altruistic factor normal distribution
    public final static double MEAN_PERMISSION_VALUE = 0.5;                 // Mean value of normal distribution for permission to charge grid (lower mean means higher probability of acceptance)
    public final static double PERMISSION_STANDARD_DEVIATION = 0.5;         // Standard deviation of normal distribution for permission to charge grid
    public final static double CHARGING_PRICE = 1.0;                        // Price to charge vehicle per kWh
}
