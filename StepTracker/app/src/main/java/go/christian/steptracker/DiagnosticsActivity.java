package go.christian.steptracker;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;


public class DiagnosticsActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager _sensorManager;
    private Sensor _accelSensor;

    private SensorDataPlotter _rawPlotter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostics);

        _sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        _accelSensor = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        _sensorManager.registerListener(this, _accelSensor, SensorManager.SENSOR_DELAY_UI);

        _rawPlotter = new SensorDataPlotter((GraphView)findViewById(R.id.graph));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
         if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            _rawPlotter.addSensorData(event.values);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
