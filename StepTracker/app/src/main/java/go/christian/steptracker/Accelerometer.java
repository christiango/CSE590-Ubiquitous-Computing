package go.christian.steptracker;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

public class Accelerometer {

  public static class Event {
    public long timestamp;
    public float x;
    public float y;
    public float z;

    public Event(long timestamp, float x, float y, float z) {
      this.timestamp = timestamp;
      this.x = x;
      this.y = y;
      this.z = z;
    }

    public static Event from(SensorEvent event) {
      if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
        throw new UnsupportedOperationException();
      }
      return new Event(event.timestamp, event.values[0], event.values[1], event.values[2]);
    }
  }
}
