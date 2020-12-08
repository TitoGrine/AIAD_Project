package grid;

import utils.Constants;

public class Grid {
    private int peakLoad;

    public int getCurrentLoad(int hours, int minutes) {
        return Constants.CURRENT_DEMAND[hours % 24] + (int) ((minutes / 60.0) * (Constants.CURRENT_DEMAND[(hours + 1) % 24] - Constants.CURRENT_DEMAND[hours % 24]));
    }

    public int getLoad(int hours, int minutes) {
        // Interpolates (linearly) the current demand
        int currentLoad =  Constants.CURRENT_DEMAND[hours % 24] + (int) ((minutes / 60.0) * (Constants.CURRENT_DEMAND[(hours + 1) % 24] - Constants.CURRENT_DEMAND[hours % 24]));

        peakLoad = Math.max(0 , currentLoad - (int) (Constants.MAX_AVAILABLE_LOAD * 0.8));

        return Constants.MAX_AVAILABLE_LOAD - currentLoad;
    }

    public int getPeakLoad() { return peakLoad; }
}
