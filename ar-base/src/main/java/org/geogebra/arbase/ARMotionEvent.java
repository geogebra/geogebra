package org.geogebra.arbase;

abstract public class ARMotionEvent {

    final public static int FIRST_FINGER_DOWN = 0;
    final public static int FIRST_FINGER_UP = 1;
    final public static int ON_MOVE = 2;
    final public static int ACTION_CANCEL = 3;
    final public static int SECOND_FINGER_DOWN = 5;
    final public static int SECOND_FINGER_UP = 6;

    public abstract float getX();

    public abstract float getY();

    public abstract float getX(int index);

    public abstract float getY(int index);

    public abstract int getPointerCount();

    public abstract int getAction();

    public abstract void setLocation(int x, int y);

    public Object getAREvent(){
        return null;
    }
}
