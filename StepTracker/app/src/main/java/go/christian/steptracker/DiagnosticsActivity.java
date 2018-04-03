package go.christian.steptracker;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;


public class DiagnosticsActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager _sensorManager;
    private Sensor _accelSensor;

    private SensorDataPlotter _rawPlotter;
    private SensorMagnitudePlotter _smoothPlotter;
    private SensorDataSmoother _sensorDataSmoother;
    private SignalMerger _signalMerger;

    private ThresholdPeakDetector _thresholdPeakDetector;

    private TextView _stepCountTextView;
    private int _stepCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostics);

        _sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        _accelSensor = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        _sensorManager.registerListener(this, _accelSensor, SensorManager.SENSOR_DELAY_UI);

        _rawPlotter = new SensorDataPlotter((GraphView)findViewById(R.id.rawGraph), "Raw Accelerometer Data");
        _smoothPlotter = new SensorMagnitudePlotter((GraphView)findViewById(R.id.smoothGraph), "Smoothed Accelerometer Data");

        _sensorDataSmoother = new SensorDataSmoother();
        _signalMerger = new SignalMerger();

        _stepCountTextView = (TextView)findViewById(R.id.stepCountValue);

        _thresholdPeakDetector = new ThresholdPeakDetector();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
         if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            _rawPlotter.addSensorData(event.values);
            float smoothedDataPoint = _sensorDataSmoother.addData(_signalMerger.addData(event.values));
            _smoothPlotter.addSensorData(smoothedDataPoint);

            if (_thresholdPeakDetector.addData(smoothedDataPoint)) {
                _stepCount += 1;
                _stepCountTextView.setText("" + _stepCount);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
