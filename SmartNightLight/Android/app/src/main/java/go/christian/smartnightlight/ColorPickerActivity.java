package go.christian.smartnightlight;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;

public class ColorPickerActivity extends AppCompatActivity {

  private SeekBar mRedSeekBar = null;
  private SeekBar mGreenSeekBar = null;
  private SeekBar mBlueSeekBar = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_color_picker);

    mRedSeekBar = findViewById(R.id.redSeekBar);
    mGreenSeekBar = findViewById(R.id.greenSeekBar);
    mBlueSeekBar = findViewById(R.id.blueSeekBar);

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener =
        new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            MainActivity.sendRGBValueToLed(
                (byte) mRedSeekBar.getProgress(),
                (byte) mGreenSeekBar.getProgress(),
                (byte) mBlueSeekBar.getProgress());
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {}
        };

    mRedSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
    mGreenSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
    mBlueSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
  }
}
