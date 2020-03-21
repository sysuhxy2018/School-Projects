public class DataClip {
    private String wavPath, rgbPath;
    private int lowerBound, upperBound;
    private double rate;

    public DataClip(String wavPath, String rgbPath, int lowerBound, int upperBound, double rate) {
        this.wavPath = wavPath;
        this.rgbPath = rgbPath;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.rate = rate;
    }

    public String getWavPath() {
        return wavPath;
    }
    public String getRgbPath() {
        return rgbPath;
    }
    public int getLowerBound() {
        return lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public double getMatchRate() { return rate; }

    public void setMatchRate(double newRate) {
        this.rate = newRate;
    }

    @Override
    public String toString() {
        return wavPath;
    }
}
