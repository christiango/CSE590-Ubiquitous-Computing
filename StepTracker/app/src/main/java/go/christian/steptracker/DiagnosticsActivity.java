package go.christian.steptracker;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;

import java.io.IOException;
import java.util.ArrayList;

public class DiagnosticsActivity extends AppCompatActivity implements SensorEventListener {
  private SensorDataPlotter _rawPlotter;
  private SensorMagnitudePlotter _smoothPlotter;
  private SensorDataSmoother _sensorDataSmoother;
  private SignalMerger _signalMerger;

  private ThresholdPeakDetector _thresholdPeakDetector;

  private TextView _stepCountTextView;
  private TextView _androidStepCountTextView;

  // Set to -1 until we get the first sensor reading from android
  private int _baseAndroidStepCount = -1;

  private int _stepCount = 0;

  private boolean _isRecording = false;
  private ArrayList<Accelerometer.Event> _eventsToRecord;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_diagnostics);

    SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    sensorManager.registerListener(
        this,
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
        SensorManager.SENSOR_DELAY_UI);
    sensorManager.registerListener(
        this,
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
        SensorManager.SENSOR_DELAY_UI);

    _rawPlotter =
        new SensorDataPlotter((GraphView) findViewById(R.id.rawGraph), "Raw Accelerometer Data");
    _smoothPlotter =
        new SensorMagnitudePlotter(
            (GraphView) findViewById(R.id.smoothGraph), "Smoothed Accelerometer Data");

    _sensorDataSmoother = new SensorDataSmoother();
    _signalMerger = new SignalMerger();

    _stepCountTextView = findViewById(R.id.stepCountValue);
    _androidStepCountTextView = findViewById(R.id.androidStepCountValue);

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

      if (_isRecording) {
        _eventsToRecord.add(Accelerometer.Event.from(event));
      }
    } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
      int sensorValue = Math.round(event.values[0]);
      if (_baseAndroidStepCount == -1) {
        _baseAndroidStepCount = sensorValue;
      }

      _androidStepCountTextView.setText("" + (sensorValue - _baseAndroidStepCount));
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {}

  public void onRecordButtonClicked(View view) {
    ((Button) view).setText(_isRecording ? "Start Recording" : "Stop Recording");

    if (!_isRecording) {
      _eventsToRecord = new ArrayList<>();
    } else {
      showRecordFileNameDialog();
    }

    _isRecording = !_isRecording;
  }

  /**
   * This dialog was worked in in collaboration with Chris Dziemborowicz in an effort to be able to
   * share our exported data files to test our algorithms
   */
  private void showRecordFileNameDialog() {
    FrameLayout layout = new FrameLayout(this);
    final EditText input = new EditText(this);
    layout.addView(input);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder
        .setTitle("Export Data")
        .setMessage("Provide a name for the export file")
        .setView(layout)
        .setPositiveButton(
            "Ok",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {
                String fileName = input.getText().toString();
                try {
                  SensorDataExporter.export(
                      DiagnosticsActivity.this, _eventsToRecord, fileName + ".csv");
                } catch (IOException e) {
                }
              }
            })
        .setNegativeButton(
            "Cancel",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {}
            })
        .show();
  }
}
