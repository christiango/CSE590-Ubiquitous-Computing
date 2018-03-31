package go.christian.steptracker;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class DiagnosticsActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager _sensorManager;
    private Sensor _accelSensor;
    private GraphView _graph;

    private LineGraphSeries<DataPoint> _seriesX = new LineGraphSeries();
    private LineGraphSeries<DataPoint> _seriesY = new LineGraphSeries();
    private LineGraphSeries<DataPoint> _seriesZ = new LineGraphSeries();

    private int _timeAxis = 0;

    private final int MAX_DATA_PONTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostics);

        _sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        _accelSensor = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        _sensorManager.registerListener(this, _accelSensor, SensorManager.SENSOR_DELAY_UI);

        _graph = findViewById(R.id.graph);

        _graph.setTitle("Raw Accelerometer Data");

        _graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        _graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
        _graph.getLegendRenderer().setVisible(true);

        _graph.getViewport().setXAxisBoundsManual(true);
        _graph.getViewport().setMinX(0);
        _graph.getViewport().setMaxX(40);

        _graph.getViewport().setYAxisBoundsManual(true);
        _graph.getViewport().setMinY(-20);
        _graph.getViewport().setMaxY(20);

        _seriesX.setTitle("X");
        _seriesY.setTitle("Y");
        _seriesZ.setTitle("Z");


        _seriesX.setColor(Color.RED);
        _seriesY.setColor(Color.GREEN);
        _seriesZ.setColor(Color.BLUE);

        _graph.addSeries(_seriesX);
        _graph.addSeries(_seriesY);
        _graph.addSeries(_seriesZ);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
         if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                _seriesX.appendData(new DataPoint(_timeAxis, event.values[0]), true, MAX_DATA_PONTS);
                _seriesY.appendData(new DataPoint(_timeAxis, event.values[1]), true, MAX_DATA_PONTS);
                _seriesZ.appendData(new DataPoint(_timeAxis, event.values[2]), true, MAX_DATA_PONTS);
                _timeAxis += 1;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
