package vehicle;

import jade.core.AID;

import java.io.Serializable;

public class BroadCarInfo implements Serializable {
    private AID aid;
    private double af;
    private double chargedPercent;

    public BroadCarInfo(AID aid, double af, double chargedPercent) {
        this.aid = aid;
        this.af = af;
        this.chargedPercent = chargedPercent;
    }

    public AID getAid() {
        return aid;
    }

    public double getAf() {
        return af;
    }

    public double getChargedPercent() {
        return chargedPercent;
    }

    @Override
    public String toString() {
        return "BroadCarInfo{" +
                "aid=" + aid +
                ", af=" + af +
                ", chargedPercent=" + chargedPercent +
                '}';
    }
}
