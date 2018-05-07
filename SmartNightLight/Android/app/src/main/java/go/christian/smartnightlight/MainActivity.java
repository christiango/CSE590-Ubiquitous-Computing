/* BLE demo: Use a single button to send data to the Duo board from the Android app to control the
 * LED on and off on the board through BLE.
 *
 * The app is built based on the example code provided by the RedBear Team:
 * https://github.com/RedBearLab/Android
 *
 * Starter code, which contained the complete bluetooth connectivity provided by Liang He
 */
package go.christian.smartnightlight;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import go.christian.smartnightlight.BLE.RBLGattAttributes;
import go.christian.smartnightlight.BLE.RBLService;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
  // Define the device name and the length of the name
  // Note the device name and the length should be consistent with the ones defined in the Duo
  // sketch
  private String mTargetDeviceName = "chrisgo";
  private int mNameLen = 0x08;

  private static final String TAG = MainActivity.class.getSimpleName();

  // Declare all variables associated with the UI components
  private Button mConnectBtn = null;
  private TextView mDeviceName = null;
  private TextView mRssiValue = null;
  private TextView mUUID = null;
  private SeekBar mRedSeekBar = null;
  private SeekBar mGreenSeekBar = null;
  private SeekBar mBlueSeekBar = null;

  private String mBluetoothDeviceName = "";
  private String mBluetoothDeviceUUID = "";

  // Declare all Bluetooth stuff
  private static BluetoothGattCharacteristic mCharacteristicTx = null;
  private static RBLService mBluetoothLeService;
  private BluetoothAdapter mBluetoothAdapter;
  private BluetoothDevice mDevice = null;
  private String mDeviceAddress;

  private boolean flag = true;
  private boolean mConnState = false;
  private boolean mScanFlag = false;

  private byte[] mData = new byte[3];
  private static final int REQUEST_ENABLE_BT = 1;
  private static final long SCAN_PERIOD = 10000; // millis

  private static final char[] hexArray = {
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
  };

  private byte mRValue = 0;
  private byte mGValue = 0;
  private byte mBValue = 0;

  private void setRgbColor(byte r, byte g, byte b, boolean updateSlider) {
    if (r == mRValue && g == mGValue && b == mBValue) {
      return;
    }

    mRValue = r;
    mGValue = g;
    mBValue = g;

    // Update the slider values
    if (updateSlider) {
      mRedSeekBar.setProgress(r);
      mGreenSeekBar.setProgress(g);
      mBlueSeekBar.setProgress(b);
    }

    sendRGBValueToLed(r, g, b);
  }

  // Process service connection. Created by the RedBear Team
  private final ServiceConnection mServiceConnection =
      new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
          mBluetoothLeService = ((RBLService.LocalBinder) service).getService();
          if (!mBluetoothLeService.initialize()) {
            Log.e(TAG, "Unable to initialize Bluetooth");
            finish();
          }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
          mBluetoothLeService = null;
        }
      };

  private void setButtonDisable() {
    flag = false;
    mConnState = false;
    mConnectBtn.setText("Connect");
    mRssiValue.setText("");
    mDeviceName.setText("");
    mUUID.setText("");
  }

  private void setButtonEnable() {
    flag = true;
    mConnState = true;
    mConnectBtn.setText("Disconnect");
  }

  // Process the Gatt and get data if there is data coming from Duo board. Created by the RedBear
  // Team
  private final BroadcastReceiver mGattUpdateReceiver =
      new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          final String action = intent.getAction();

          if (RBLService.ACTION_GATT_DISCONNECTED.equals(action)) {
            Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
            setButtonDisable();
          } else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();

            getGattService(mBluetoothLeService.getSupportedGattService());
          } else if (RBLService.ACTION_GATT_RSSI.equals(action)) {
            displayData(intent.getStringExtra(RBLService.EXTRA_DATA));
          }
        }
      };

  // Display the received RSSI on the interface
  private void displayData(String data) {
    if (data != null) {
      mRssiValue.setText(data);
      mDeviceName.setText(mBluetoothDeviceName);
      mUUID.setText(mBluetoothDeviceUUID);
    }
  }

  // Get Gatt service information for setting up the communication
  private void getGattService(BluetoothGattService gattService) {
    if (gattService == null) return;

    setButtonEnable();

    startReadRssi();

    mCharacteristicTx = gattService.getCharacteristic(RBLService.UUID_BLE_SHIELD_TX);
  }

  // Start a thread to read RSSI from the board
  private void startReadRssi() {
    new Thread() {
      public void run() {

        while (flag) {
          mBluetoothLeService.readRssi();
          try {
            sleep(500);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      };
    }.start();
  }

  // Scan all available BLE-enabled devices
  private void scanLeDevice() {
    new Thread() {

      @Override
      public void run() {
        mBluetoothAdapter.startLeScan(mLeScanCallback);

        try {
          Thread.sleep(SCAN_PERIOD);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        mBluetoothAdapter.stopLeScan(mLeScanCallback);
      }
    }.start();
  }

  // Callback function to search for the target Duo board which has matched UUID
  // If the Duo board cannot be found, debug if the received UUID matches the predefined UUID on the
  // board
  private BluetoothAdapter.LeScanCallback mLeScanCallback =
      new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(
            final BluetoothDevice device, final int rssi, final byte[] scanRecord) {

          runOnUiThread(
              new Runnable() {
                @Override
                public void run() {
                  byte[] serviceUuidBytes = new byte[16];
                  String serviceUuid = "";
                  for (int i = (21 + mNameLen), j = 0; i >= (6 + mNameLen); i--, j++) {
                    serviceUuidBytes[j] = scanRecord[i];
                  }
                  /*
                   * This is where you can test if the received UUID matches the defined UUID in the Arduino
                   * Sketch and uploaded to the Duo board: 0x713d0000503e4c75ba943148f18d941e.
                   */
                  serviceUuid = bytesToHex(serviceUuidBytes);
                  if (stringToUuidString(serviceUuid)
                          .equals(RBLGattAttributes.BLE_SHIELD_SERVICE.toUpperCase(Locale.ENGLISH))
                      && device.getName().equals(mTargetDeviceName)) {
                    mDevice = device;
                    mBluetoothDeviceName = mDevice.getName();
                    mBluetoothDeviceUUID = serviceUuid;
                  }
                }
              });
        }
      };

  // Convert an array of bytes into Hex format string
  private String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    int v;
    for (int j = 0; j < bytes.length; j++) {
      v = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
  }

  public static void sendRGBValueToLed(byte r, byte g, byte b) {
    byte buf[] = new byte[] {(byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    buf[1] = r;
    buf[2] = g;
    buf[3] = b;

    mCharacteristicTx.setValue(buf);
    mBluetoothLeService.writeCharacteristic(mCharacteristicTx);
  }

  // Convert a string to a UUID format
  private String stringToUuidString(String uuid) {
    StringBuffer newString = new StringBuffer();
    newString.append(uuid.toUpperCase(Locale.ENGLISH).substring(0, 8));
    newString.append("-");
    newString.append(uuid.toUpperCase(Locale.ENGLISH).substring(8, 12));
    newString.append("-");
    newString.append(uuid.toUpperCase(Locale.ENGLISH).substring(12, 16));
    newString.append("-");
    newString.append(uuid.toUpperCase(Locale.ENGLISH).substring(16, 20));
    newString.append("-");
    newString.append(uuid.toUpperCase(Locale.ENGLISH).substring(20, 32));

    return newString.toString();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Associate all UI components with variables
    mConnectBtn = (Button) findViewById(R.id.connectBtn);
    mDeviceName = (TextView) findViewById(R.id.deviceName);
    mRssiValue = (TextView) findViewById(R.id.rssiValue);
    mUUID = (TextView) findViewById(R.id.uuidValue);

    // Connection button click event
    mConnectBtn.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View v) {
            if (mScanFlag == false) {
              // Scan all available devices through BLE
              scanLeDevice();

              Timer mTimer = new Timer();
              mTimer.schedule(
                  new TimerTask() {

                    @Override
                    public void run() {
                      if (mDevice != null) {
                        mDeviceAddress = mDevice.getAddress();
                        mBluetoothLeService.connect(mDeviceAddress);
                        mScanFlag = true;
                      } else {
                        runOnUiThread(
                            new Runnable() {
                              public void run() {
                                Toast toast =
                                    Toast.makeText(
                                        MainActivity.this,
                                        "Couldn't search Ble Shiled device!",
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(0, 0, Gravity.CENTER);
                                toast.show();
                              }
                            });
                      }
                    }
                  },
                  SCAN_PERIOD);
            }

            System.out.println(mConnState);
            if (mConnState == false) {
              mBluetoothLeService.connect(mDeviceAddress);
            } else {
              mBluetoothLeService.disconnect();
              mBluetoothLeService.close();
              setButtonDisable();
            }
          }
        });

    // Bluetooth setup. Created by the RedBear team.
    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
      Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT).show();
      finish();
    }

    final BluetoothManager mBluetoothManager =
        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    mBluetoothAdapter = mBluetoothManager.getAdapter();
    if (mBluetoothAdapter == null) {
      Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT).show();
      finish();
      return;
    }

    Intent gattServiceIntent = new Intent(MainActivity.this, RBLService.class);
    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    mRedSeekBar = findViewById(R.id.redSeekBar);
    mGreenSeekBar = findViewById(R.id.greenSeekBar);
    mBlueSeekBar = findViewById(R.id.blueSeekBar);

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener =
        new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            setRgbColor(
                (byte) mRedSeekBar.getProgress(),
                (byte) mGreenSeekBar.getProgress(),
                (byte) mBlueSeekBar.getProgress(),
                false);
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {}
        };

    mRedSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
    mGreenSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
    mBlueSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

    SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_UI);
  }

  @Override
  protected void onResume() {
    super.onResume();

    // Check if BLE is enabled on the device. Created by the RedBear team.
    if (!mBluetoothAdapter.isEnabled()) {
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
  }

  @Override
  protected void onStop() {
    super.onStop();

    flag = false;

    unregisterReceiver(mGattUpdateReceiver);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if (mServiceConnection != null) unbindService(mServiceConnection);
  }

  // Create a list of intent filters for Gatt updates. Created by the RedBear team.
  private static IntentFilter makeGattUpdateIntentFilter() {
    final IntentFilter intentFilter = new IntentFilter();

    intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);
    intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
    intentFilter.addAction(RBLService.ACTION_GATT_SERVICES_DISCOVERED);
    intentFilter.addAction(RBLService.ACTION_DATA_AVAILABLE);
    intentFilter.addAction(RBLService.ACTION_GATT_RSSI);

    return intentFilter;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // User chose not to enable Bluetooth.
    if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
      finish();
      return;
    }

    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {}
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {}
}
