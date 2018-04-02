package go.christian.steptracker;

public class SensorDataSmoother {
    // Smoothing strategy inspired by the code at https://www.arduino.cc/en/Tutorial/Smoothing
    private class AverageSmoothingStrategy {
        private final int MAX_WINDOW_SIZE = 4;

        private float[] _values = new float[MAX_WINDOW_SIZE];
        private int _currentIndex = 0;

        // Adds a value and returns the current average
        public float addValue(float value) {
            _values[_currentIndex % MAX_WINDOW_SIZE] = value;

            int numberOfMeasurements = Math.min(MAX_WINDOW_SIZE, _currentIndex + 1);
            float sum = 0;
            for (int i = 0; i < numberOfMeasurements; i++) {
                sum += _values[i];
            }

            _currentIndex += 1;

            return sum / numberOfMeasurements;
        }
    }

    private AverageSmoothingStrategy _xSmoother = new AverageSmoothingStrategy();
    private AverageSmoothingStrategy _ySmoother = new AverageSmoothingStrategy();
    private AverageSmoothingStrategy _zSmoother = new AverageSmoothingStrategy();


    public float[] addData(float[] values) {
        float[] results = new float[3];

        results[0] = _xSmoother.addValue(values[0]);
        results[1] = _ySmoother.addValue(values[1]);
        results[2] = _zSmoother.addValue(values[2]);

        return results;
    }
}
