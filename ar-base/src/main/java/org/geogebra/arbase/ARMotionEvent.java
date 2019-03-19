package org.geogebra.arbase;

public class ARMotionEvent {

    private int pointerCount;
    private float fingerPositions[][];
    protected int mAction;

    public static int FIRST_FINGER_DOWN = 0;
    public static int FIRST_FINGER_UP = 1;
    public static int ON_MOVE = 2;
    public static int ACTION_CANCEL = 3;
    public static int SECOND_FINGER_DOWN = 5;
    public static int SECOND_FINGER_UP = 6;

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

    public float getX() {
        return getX(0);
    }

    public float getY() {
        return getY(0);
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

    public int getAction() {
        return mAction;
    }

    public void setAction(int action) {
        mAction = action;
    }

    public void setLocation(int x, int y) {
        fingerPositions[0][0] = x;
        fingerPositions[0][1] = y;
    }

    public Object getAREvent(){
        return null;
    }
}
