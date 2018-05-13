package io.makeabilitylab.facetrackerble;
import io.makeabilitylab.facetrackerble.SignalUtils;
import io.makeabilitylab.facetrackerble.ble.BLEDevice;

public class SignalSmoother {
    private final int WINDOW_SIZE = 20;
    private byte[] buffer = new byte[WINDOW_SIZE];
    private int index = 0;

    public void smoothAndSendData(BLEDevice BLEDevice, byte data) {
        buffer[index] = data;

        index+=1;

        if (index == WINDOW_SIZE) {
            int sum = 0;
            for (int i = 0; i < WINDOW_SIZE; i++) {
                sum += buffer[i];
            }

            BLEDevice.sendData(new byte[] { (byte) 0x01, SignalUtils.doubleToByte(sum * 1.0/ WINDOW_SIZE) } );

            index = 0;
        }
    }
}
