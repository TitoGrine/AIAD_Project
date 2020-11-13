package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Data {

    private static final String data_folder_path = "./" + Constants.DATA_FOLDER;
    private static final String vehicle_data_file_path = "./" + Constants.DATA_FOLDER + "/" + Constants.VEHICLE_STATS + Constants.FILE_EXTENSION;
    private static final String grid_data_file_path = "./" + Constants.DATA_FOLDER + "/" + Constants.GRID_STATS + Constants.FILE_EXTENSION;

    private static void createVehicleStatsFile(){
        File folder = new File(data_folder_path);

        folder.mkdir();

        File vehicle_stats = new File(vehicle_data_file_path);

        try {
            FileWriter writer = new FileWriter(vehicle_stats);

            writer.append("Battery_delta");
            writer.append(",");
            writer.append("Battery_percentage");
            writer.append(",");
            writer.append("Price");
            writer.append("\n");

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createGridStatsFile(){
        File folder = new File(data_folder_path);

        folder.mkdir();

        File grid_stats = new File(grid_data_file_path);

        try {
            FileWriter writer = new FileWriter(grid_stats);

            writer.append("Peak Load");
            writer.append(",");
            writer.append("Peak Reduction");
            writer.append("\n");

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createFiles(){
        createVehicleStatsFile();
        createGridStatsFile();
    }

    public static void submitVehicleStat(List<String> data){
        try {
            FileWriter writer = new FileWriter(vehicle_data_file_path, true);

            writer.append(String.join(",", data));
            writer.append('\n');

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void submitGridStat(String peakLoad, String peakReduction){
        try {
            FileWriter writer = new FileWriter(grid_data_file_path, true);

            writer.append(peakLoad);
            writer.append(",");
            writer.append(peakReduction);
            writer.append('\n');

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
