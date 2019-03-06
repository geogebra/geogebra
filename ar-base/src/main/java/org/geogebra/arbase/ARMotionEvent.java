package org.geogebra.arbase;

public class ARMotionEvent {

    private int pointerCount;
    private float fingerPositions[][];

    public ARMotionEvent(float firstFingerX, float firstFingerY){
        pointerCount = 1;
        fingerPositions = new float[pointerCount][2];
        fingerPositions[0][0] = firstFingerX;
        fingerPositions[0][1] = firstFingerY;
    }

    public ARMotionEvent(float firstFingerX, float firstFingerY, float secondFingerX,
                         float secondFingerY){
        pointerCount = 2;
        fingerPositions = new float[pointerCount][2];
        fingerPositions[0][0] = firstFingerX;
        fingerPositions[0][1] = firstFingerY;
        fingerPositions[1][0] = secondFingerX;
        fingerPositions[1][1] = secondFingerY;
    }

    public float getX(int index) {
        return fingerPositions[index][0];
    }

    public float getY(int index) {
        return fingerPositions[index][1];
    }

    public int getPointerCount() {
        return pointerCount;
    }
}
