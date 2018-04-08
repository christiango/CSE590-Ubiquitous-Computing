package go.christian.steptracker;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Sensor data exporter implemented by Chris Dziemborowicz. */
public class SensorDataExporter {
  public static void export(Context context, List<Accelerometer.Event> data, String fileName)
      throws IOException {
    if (TextUtils.isEmpty(fileName)) {
      throw new IllegalArgumentException();
    }
    File file = new File(context.getExternalFilesDir(null /* type */), fileName);
    FileWriter fileWriter = new FileWriter(file, false /* append */);
    try {
      fileWriter.write("timestamp,x,y,z\n");
      for (Accelerometer.Event event : data) {
        fileWriter.write(Long.toString(event.timestamp));
        fileWriter.write(",");
        fileWriter.write(Float.toString(event.x));
        fileWriter.write(",");
        fileWriter.write(Float.toString(event.y));
        fileWriter.write(",");
        fileWriter.write(Float.toString(event.z));
        fileWriter.write("\n");
      }
    } finally {
      fileWriter.close();
    }
  }

  public static List<Accelerometer.Event> importFromString(String string) {
    List<Accelerometer.Event> result = new ArrayList<>();

    String lines[] = string.split("\\r?\\n");

    for (int i = 1; i< lines.length; i+=1) {
      String parts[] = lines[i].split(",");

      result.add(
          new Accelerometer.Event(
              Long.parseLong(parts[0]),
              Float.parseFloat(parts[1]),
              Float.parseFloat(parts[2]),
              Float.parseFloat(parts[3])));
    }

    return result;
  }
}

