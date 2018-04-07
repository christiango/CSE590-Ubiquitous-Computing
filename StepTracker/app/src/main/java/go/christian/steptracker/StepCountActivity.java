package go.christian.steptracker;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class StepCountActivity extends AppCompatActivity implements SensorEventListener {

    private StepCounter _stepCounter = StepCounterFactory.GetStepCounter();

    private TextView _stepCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_UI);

        _stepCountTextView = findViewById(R.id.stepCount);

        ImageView image = findViewById(R.id.progressImage);
        image.setImageDrawable(new PacmanDrawable());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            _stepCountTextView.setText("" + _stepCounter.addData(event.values));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
