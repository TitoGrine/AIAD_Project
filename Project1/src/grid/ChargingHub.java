package grid;

import jade.core.Agent;

public class ChargingHub extends Agent {
    private int availableLoad; // in kWh
    //Grid simulator
    private int numStations;

    public ChargingHub(int availableLoad, int numStations) {
        this.availableLoad = availableLoad;
        this.numStations = numStations;
    }
}

//x u
