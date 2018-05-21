package io.makeabilitylab.facetrackerble;

public class WinkDetector {
    private final int MAX_WINDOW_SIZE = 3;
    private final int COOLDOWN_TICKS = 20;
    private final double EYE_CLOSED_THRESHOLD = 0.5;
    private final double EYE_OPEN_THRESHOLD = 0.8;

    private double[] _leftEyeBuffer = new double[MAX_WINDOW_SIZE];
    private double[] _rightEyeBuffer = new double[MAX_WINDOW_SIZE];

    private int currentIndex = 0;

    // Prevents two blink events from triggering to close to one another
    private int remainigCooldownTicks = 0;

    private boolean isLeftEyeBlink() {
        for (int i =0; i< MAX_WINDOW_SIZE; i+=1) {
            if (!(_leftEyeBuffer[i] < EYE_CLOSED_THRESHOLD && _rightEyeBuffer[i] > EYE_OPEN_THRESHOLD)) {
                return false;
            }
        }
        return true;
    }

    private boolean isRightEyeBlink() {
        for (int i =0; i< MAX_WINDOW_SIZE; i+=1) {
            if (!(_rightEyeBuffer[i] < EYE_CLOSED_THRESHOLD && _leftEyeBuffer[i] > EYE_OPEN_THRESHOLD)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Takes in an eye open probability and determines if a wink event happened.
     * Returns 0 if this is not a gesture, returns -1 if it was a left
     * eye wink and 1 if it was a right eye wink.
     */
    public int AddData(double leftEyeOpenProbability, double rightEyeOpenProbability) {
        if (remainigCooldownTicks > 0) {
            remainigCooldownTicks -= 1;
            return 0;
        }

        _leftEyeBuffer[currentIndex % MAX_WINDOW_SIZE] = leftEyeOpenProbability;
        _rightEyeBuffer[currentIndex % MAX_WINDOW_SIZE] = rightEyeOpenProbability;

        currentIndex += 1;

        if (currentIndex < MAX_WINDOW_SIZE) {
            return 0;
        }

        if (isLeftEyeBlink()) {
            currentIndex = 0;
            remainigCooldownTicks = COOLDOWN_TICKS;
            return -1;
        }

        if (isRightEyeBlink()) {
            currentIndex = 0;
            remainigCooldownTicks = COOLDOWN_TICKS;
            return 1;
        }

        return 0;
    }
}
