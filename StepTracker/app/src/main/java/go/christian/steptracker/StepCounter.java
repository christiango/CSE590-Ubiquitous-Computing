package go.christian.steptracker;

public class StepCounter {
  private ISignalMerger _signalMerger;
  private IDataSmoother _dataSmoother;
  private IPeakDetector _peakDetector;

  private int _stepCount = 0;

  public StepCounter(
      ISignalMerger signalMerger, IDataSmoother smoother, IPeakDetector peakDetector) {
    _signalMerger = signalMerger;
    _dataSmoother = smoother;
    _peakDetector = peakDetector;
  }

  /**
   * Adds a new data point and returns the current step count.
   *
   * @param values - The raw x, y, and z sensor values
   */
  public int addData(float[] values) {
    if (_peakDetector.addData(_dataSmoother.addData(_signalMerger.addData(values)))) {
      _stepCount += 1;
    }

    return _stepCount;
  }
}
