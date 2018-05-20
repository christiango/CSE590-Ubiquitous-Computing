package io.makeabilitylab.facetrackerble;

import com.google.android.gms.vision.face.Face;


public class SignalUtils {

    // Converts a double value from 0 - 255.0 into a byte value
    public static byte doubleToByte(double value) {
        return (byte) Math.round(value);
    }

    public static byte faceToServo(Face face, int previewWidth) {
        return SignalUtils.doubleToByte(180 - (((face.getPosition().x + face.getWidth() / 2)/previewWidth) * 180));
    }
}
