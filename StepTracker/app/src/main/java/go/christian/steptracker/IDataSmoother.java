package go.christian.steptracker;

public interface IDataSmoother {
    /**
     * Takes in a data point and returns a smoothed version of that data point in an effort to remove
     * noise from the signal.
     * @param value The raw data point
     * @return The smoothed data point to use
     */
    float addData(float value);
}
