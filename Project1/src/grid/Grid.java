package grid;

import utils.Constants;

public class Grid {
    public int getLoad(int t) {
        return Constants.MAX_AVAILABLE_LOAD - Constants.CURRENT_DEMAND[t];
    }
}
