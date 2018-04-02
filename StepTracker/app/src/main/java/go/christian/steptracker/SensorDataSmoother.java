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

    private AverageSmoothingStrategy _smoother = new AverageSmoothingStrategy();
    
    public float addData(float value) {
        return _smoother.addValue(value);
    }
}
