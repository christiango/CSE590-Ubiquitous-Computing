package go.christian.steptracker;

import android.graphics.Color;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class SensorMagnitudePlotter {
    private LineGraphSeries<DataPoint> _series = new LineGraphSeries();

    private GraphView _graph;

    private int _timeAxis = 0;
    private final int MAX_DATA_PONTS = 100;

    public SensorMagnitudePlotter(GraphView graph, String chartTitle) {
        _graph = graph;

        _graph.setTitle(chartTitle);

        _graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        _graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
        _graph.getLegendRenderer().setVisible(true);

        _graph.getViewport().setXAxisBoundsManual(true);
        _graph.getViewport().setMinX(0);
        _graph.getViewport().setMaxX(40);

        _graph.getViewport().setYAxisBoundsManual(true);
        _graph.getViewport().setMinY(0);
        _graph.getViewport().setMaxY(5);

        _graph.addSeries(_series);
    }

    public void addSensorData(float value) {
        _series.appendData(new DataPoint(_timeAxis, value), true, MAX_DATA_PONTS);
        _timeAxis += 1;

    }
}
