package io.makeabilitylab.facetrackerble;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import io.makeabilitylab.facetrackerble.ble.BLEDevice;

public class FaceCountTracker extends Tracker<Face> {
  private static int _count = 0;
  private BLEDevice _mBleDevice;

  public FaceCountTracker(BLEDevice bleDevice) {
    _mBleDevice = bleDevice;
  }

  private void sendFaceCount() {
    _mBleDevice.sendData(new byte[] { (byte) 0x02, (byte) _count } );

  }
  @Override
  public void onNewItem(int faceId, Face face) {
    _count += 1;
    sendFaceCount();
  }

  @Override
  public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
  }

  @Override
  public void onMissing(FaceDetector.Detections<Face> detectionResults) {
  }

  @Override
  public void onDone() {
    _count -= 1;
    sendFaceCount();
  }
}
