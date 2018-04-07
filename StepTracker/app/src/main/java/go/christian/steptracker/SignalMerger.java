package go.christian.steptracker;

// Responsible for merging the 3 accelerometer values into 1 value
public class SignalMerger implements ISignalMerger {
  public float addData(float[] values) {
    return (float)
        Math.sqrt(Math.pow(values[0], 2) + Math.pow(values[1], 2) + Math.pow(values[2], 2));
  }
}
