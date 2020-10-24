package vehicle;

public class OneWayVehicle extends Vehicle {
    public OneWayVehicle() {
        this(100,150);
    }

    public OneWayVehicle(int currentCapacity, int maxCapacity) {
        super(currentCapacity,maxCapacity);
    }
}
