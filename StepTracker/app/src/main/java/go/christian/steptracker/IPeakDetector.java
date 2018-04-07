package go.christian.steptracker;

public interface IPeakDetector {
    /** Takes in a new data point and returns true if a new step is detected **/
    boolean addData(float value);
}
