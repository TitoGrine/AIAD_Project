package utils;

import java.util.Random;

public class Utilities {

    private static double normalDistribution(double mean, double deviation){
        Random rand = new Random(System.currentTimeMillis());

        return Math.max(0.0, Math.min(1.0, rand.nextGaussian() * deviation + mean));
    }

    public static int randomNumber(int lower, int upper){
        Random rand = new Random(System.currentTimeMillis());

        return rand.nextInt((upper - lower) + 1) + lower;
    }

    public static int randomNumber(double lower, double upper){
        Random rand = new Random(System.currentTimeMillis());

        return rand.nextInt((int) (upper - lower) + 1) + (int) lower;
    }

    public static int randomVehicleType(){
        double rand = Math.random();

        if(rand < Constants.VEHICLE_DISTRIBUTION[Constants.ONEWAY_VEHICLE])
            return Constants.ONEWAY_VEHICLE;
        else if(rand < Constants.VEHICLE_DISTRIBUTION[Constants.ONEWAY_VEHICLE] + Constants.VEHICLE_DISTRIBUTION[Constants.TWOWAY_VEHICLE])
            return Constants.TWOWAY_VEHICLE;
        else
            return Constants.BROAD_VEHICLE;
    }

    public static double randomAltruisticFactor(){
        return normalDistribution(Constants.MEAN_ALTRUISTIC_VALUE, Constants.ALTRUISTIC_STANDARD_DEVIATION);
    }

    public static boolean chargeGridPermission(){
        return normalDistribution(Constants.MEAN_PERMISSION_VALUE, Constants.PERMISSION_STANDARD_DEVIATION) < 0.5;
    }

    public static void printEventMessage(final String message, String color){
        System.out.println(Constants.WHITE_BOLD + message + Constants.RESET);
    }
}
