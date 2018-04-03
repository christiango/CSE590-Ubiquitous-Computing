package go.christian.steptracker;

/**
 * A simple peak detector that just checks if the signal ever passes a threshold
 */
public class ThresholdPeakDetector {
    /**
     * If the acceleration value is above this threshold, consider it a peak
     */
    private final float PEAK_THRESHOLD = 13;

    /**
     * If the acceleration value drops below this value, we can say the
     * acceleration has dropped below the threshold
     */
    private final float BASELINE = 10;

    /**
     * A variable
     */
    private boolean _aboveThreshold = false;

    /**
     * Adds a new data point and returns true of a peak was detected
     */
    public boolean addData(float value) {
        if (!_aboveThreshold && value > PEAK_THRESHOLD) {
            _aboveThreshold = true;
            return true;
        }

        if (value < BASELINE) {
            _aboveThreshold = false;
        }

        return false;
    }
}
