package samples.testbed.ewrapper;

public class StockProperty {
    double open;
    double high;
    double low;
    double close;

    public StockProperty (double open, double high, double low, double close) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
    }

    public double getRandomCalculation() {
        return (open - close);
    }
}
