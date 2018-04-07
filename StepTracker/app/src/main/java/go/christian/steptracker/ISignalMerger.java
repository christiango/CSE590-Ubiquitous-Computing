package go.christian.steptracker;

public interface ISignalMerger {
    /**
     * Takes in raw x, y, and z sensor data and merges it into a single value
     */
    float addData(float [] values);
}
