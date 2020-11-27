package grid;

import java.io.Serializable;

public class Vehicle2GridConditions implements Serializable {
    double discountPrice;
    int sharedLoad;

    public Vehicle2GridConditions(double discountPrice, int sharedLoad) {
        this.discountPrice = discountPrice;
        this.sharedLoad = sharedLoad;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public int getSharedLoad() {
        return sharedLoad;
    }

    @Override
    public String toString() {
        String result = "\n  Vehicle to Grid Conditions: \n";
        result += "     · Discount price = " + discountPrice + "\n";
        result += "     · Shared load = " + sharedLoad;

        return result;
    }
}
