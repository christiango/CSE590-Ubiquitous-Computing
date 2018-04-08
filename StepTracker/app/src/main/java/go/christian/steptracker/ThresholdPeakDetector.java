package go.christian.steptracker;

/**
 * A simple peak detector that just checks if the signal ever passes a threshold
 */
public class ThresholdPeakDetector implements IPeakDetector {
    /**
     * If the acceleration value is above this threshold, consider it a peak
     */
    private final float PEAK_THRESHOLD = 1.5f;

    /**
     * If the acceleration value drops below this value, we can say the
     * acceleration has dropped below the threshold
     */
    private final float BASELINE = 0;

    private final int MIN_TICKS_ABOVE_THRESHOLD = 1;

    private final int MAX_TICKS_ABOVE_THRESHOLD = 3;

    /**
     * A variable that states if the step tracker is currently above the threshold
     */
    private boolean _aboveThreshold = false;

    /**
     * A number tracking the number of ticks the signal had that were above the threshold.
     * This is another means of noise reduction and an effort to avoid counting steps while
     * the phone is being put into the pocket of the user.
     */
    private int _numberOfTicksAboveThreshold = 0;


    /**
     * Adds a new data point and returns true of a peak was detected
     */
    public boolean addData(float value) {
        boolean result = false;
        if (!_aboveThreshold && value > PEAK_THRESHOLD) {
            _aboveThreshold = true;
            _numberOfTicksAboveThreshold += 1;
        }

        if (value < BASELINE) {
            if (_numberOfTicksAboveThreshold >= MIN_TICKS_ABOVE_THRESHOLD && _numberOfTicksAboveThreshold <= MAX_TICKS_ABOVE_THRESHOLD) {
                result = true;
            }

            _numberOfTicksAboveThreshold = 0;
            _aboveThreshold = false;
        }

        return result;
    }
}
