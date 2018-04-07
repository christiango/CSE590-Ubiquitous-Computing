package go.christian.steptracker;

public class StepCounterFactory {

  public static StepCounter GetStepCounter() {
    return new StepCounter(
        new SignalMerger(), new SensorDataSmoother(), new ThresholdPeakDetector());
  }
}
