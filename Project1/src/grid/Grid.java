package grid;

public class Grid {
    public float getLoad(int t) {
        return (float) (-Math.pow(t,2) + 555);
    }
}
