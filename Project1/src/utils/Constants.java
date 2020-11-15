package utils;

public class Constants {
    public final static int MIN = 0;
    public final static int MAX = 1;

    public final static int ONEWAY_VEHICLE = 0;
    public final static int TWOWAY_VEHICLE = 1;
    public final static int BROAD_VEHICLE  = 2;

    public final static double NO_FACTOR = -1.0;
    public final static String BROAD_SERVICE = "broad-service";
    public final static String CHUB_SERVICE = "chub-service";

    // Grid simulation
    public final static int[] SPRING_GRID_DEMAND = {65, 58, 57, 57, 62, 57, 58, 49, 72, 81, 187, 165, 28, 100, 111, 187, 57, 21, 26, 21, 57, 58, 57, 58};
    public final static int[] SUMMER_GRID_DEMAND = {56, 57, 58, 58, 58, 69, 21, 41, 21, 20, 115, 151, 34, 24, 21, 30, 31, 22, 23, 21, 59, 57, 60, 60};
    public final static int[] AUTUMN_GRID_DEMAND = {59, 61, 59, 59, 60, 58, 58, 149, 267, 157, 140, 304, 283, 210, 83, 284, 68, 37, 37, 74, 61, 61, 70, 62};
    public final static int[] WINTER_GRID_DEMAND = {61, 58, 59, 58, 59, 69, 60, 22, 182, 25, 25, 103, 111, 34, 58, 370, 387, 390, 22, 58, 70, 61, 62, 61};

    public final static int SPRING_MAX_AVAILABLE_LOAD = 200;
    public final static int SUMMER_MAX_AVAILABLE_LOAD = 175;
    public final static int AUTUMN_MAX_AVAILABLE_LOAD = 350;
    public final static int WINTER_MAX_AVAILABLE_LOAD = 400;

    // Statistics related
    public final static String DATA_FOLDER = "stats";
    public final static String VEHICLE_STATS = "vehicle_stats";
    public final static String GRID_STATS = "grid_stats";
    public final static String FILE_EXTENSION = ".csv";

    // System characteristics
    public final static double START_TIME = 0.0;                            // Time of day when the simulation starts
    public final static long TICK_FREQUENCY = 500;                          // How many (real time) milliseconds each tick is worth
    public final static int CYCLE_TICKS = 95;                               // How many ticks are done in one simulation
    public final static double TICK_RATIO = 0.25;                           // How many minutes (simulation time) each tick is worth (0.25 is a 15 minutes)
    public final static int TRAFFIC_FREQUENCY = 300;                        // Frequency to which vehicles can enter the system in (realtime) milliseconds
    public final static int CAR_TRAFFIC = 5;                                // Maximum number of vehicles that can attempt to charge at each interval
    public final static double EXIT_PROBABILITY = 0.05;                     // Base probability for a car to leave
    public final static double EXIT_FACTOR = 0.1;                           // Exit probability factor of battery
    public final static int CHARGING_STATIONS = 15;                         // Number of charging stations the charging hub has. Recommended between [5, 15]
    public final static int[] CAPACITY_DISTRIBUTION = {30, 100};            // Range of maximum capacities of vehicles in kWh
    public final static double CHARGING_PRICE = 1.0;                        // Price to charge vehicle per kWh

    public final static double[] VEHICLE_DISTRIBUTION = {0.5, 0.3, 0.2};    // Relative frequency of each type of vehicle
    public final static double MEAN_ALTRUISTIC_VALUE = 0.8;                 // Mean value of altruistic factor of normal distribution
    public final static double ALTRUISTIC_STANDARD_DEVIATION = 0.2;         // Standard deviation of altruistic factor normal distribution
    public final static double MEAN_PERMISSION_VALUE = 0.1;                 // Mean value of normal distribution for permission to charge grid (lower mean means higher probability of acceptance)
    public final static double PERMISSION_STANDARD_DEVIATION = 0.5;         // Standard deviation of normal distribution for permission to charge grid

    public final static int[] CURRENT_DEMAND = SPRING_GRID_DEMAND;
    public final static int MAX_AVAILABLE_LOAD = SPRING_MAX_AVAILABLE_LOAD;

    // Colors

    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    // Bold
    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
    public static final String WHITE_BOLD = "\033[1;37m";  // WHITE

    // Bold High Intensity
    public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
    public static final String RED_BOLD_BRIGHT = "\033[1;91m";   // RED
    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
    public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
    public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
    public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";  // CYAN
    public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE


}
