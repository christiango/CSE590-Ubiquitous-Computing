package go.christian.steptracker;

import android.graphics.Color;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class SensorDataPlotter {
    private LineGraphSeries<DataPoint> _seriesX = new LineGraphSeries();
    private LineGraphSeries<DataPoint> _seriesY = new LineGraphSeries();
    private LineGraphSeries<DataPoint> _seriesZ = new LineGraphSeries();

    private GraphView _graph;

    private int _timeAxis = 0;
    private final int MAX_DATA_PONTS = 100;

    public SensorDataPlotter(GraphView graph) {
        _graph = graph;

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

    public void addSensorData(float [] values) {
        _seriesX.appendData(new DataPoint(_timeAxis, values[0]), true, MAX_DATA_PONTS);
        _seriesY.appendData(new DataPoint(_timeAxis, values[1]), true, MAX_DATA_PONTS);
        _seriesZ.appendData(new DataPoint(_timeAxis, values[2]), true, MAX_DATA_PONTS);
        _timeAxis += 1;

    }
}
