package utils;

import jade.core.AID;

import java.util.Random;

public class Utilities {

    private static double normalDistribution(double mean, double deviation) {
        Random rand = new Random(System.currentTimeMillis());

        return Math.max(0.0, Math.min(1.0, rand.nextGaussian() * deviation + mean));
    }

    public static int randomNumber(int lower, int upper) {
        Random rand = new Random(System.currentTimeMillis());

        return rand.nextInt((upper - lower) + 1) + lower;
    }

    public static int randomNumber(double lower, double upper) {
        Random rand = new Random(System.currentTimeMillis());

        return rand.nextInt((int) (upper - lower) + 1) + (int) lower;
    }

    public static int randomVehicleType() {
        double rand = Math.random();

        if (rand < Constants.VEHICLE_DISTRIBUTION[Constants.ONEWAY_VEHICLE])
            return Constants.ONEWAY_VEHICLE;
        else if (rand < Constants.VEHICLE_DISTRIBUTION[Constants.ONEWAY_VEHICLE] + Constants.VEHICLE_DISTRIBUTION[Constants.TWOWAY_VEHICLE])
            return Constants.TWOWAY_VEHICLE;
        else
            return Constants.BROAD_VEHICLE;
    }

    public static double randomAltruisticFactor() {
        return normalDistribution(Constants.MEAN_ALTRUISTIC_VALUE, Constants.ALTRUISTIC_STANDARD_DEVIATION);
    }

    public static boolean chargeGridPermission() {
        return normalDistribution(Constants.MEAN_PERMISSION_VALUE, Constants.PERMISSION_STANDARD_DEVIATION) < 0.5;
    }

    public static void printTime(final int hours, int minutes) {
        System.out.println(Constants.WHITE_BOLD_BRIGHT + String.format("\n ⌚ Local Time: %02d:%02d", hours, minutes) + Constants.RESET);
    }

    public static void printChargingHubMessage(final String message) {
        System.out.println(Constants.PURPLE_BOLD + "\n ⚡ Charging Hub - " + Constants.RESET + message);
    }

    public static void printVehicleMessage(final String id, final int type, final String message) {
        switch (type) {
            case Constants.ONEWAY_VEHICLE:
                System.out.println(Constants.CYAN_BOLD + "\n ✇ One Way Vehicle " + id + " - " + Constants.RESET + message);
                break;

            case Constants.TWOWAY_VEHICLE:
                System.out.println(Constants.BLUE_BOLD + "\n ✇ Two Way Vehicle " + id + " - " + Constants.RESET + message);
                break;

            case Constants.BROAD_VEHICLE:
                System.out.println(Constants.GREEN_BOLD + "\n ✇ Broad Vehicle " + id + " - " + Constants.RESET + message);
                break;
        }
    }

    public static void printSystemMessage(final String message) {
        System.out.println(Constants.YELLOW_BOLD + "\n ⚠ System - " + Constants.RESET + message);
    }
}
