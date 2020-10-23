package vehicle;

public class OneWayVehicle extends Vehicle {
    public OneWayVehicle(int currentCapacity, int maxCapacity) {
        super(currentCapacity,maxCapacity);
    }

    @Override
    protected void setup() {
        System.out.println("I'm a One Way Car!!");
    }
}
