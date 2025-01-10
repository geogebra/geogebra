package org.geogebra.xr;

public interface XRMotionEvent  {
	public static int FIRST_FINGER_DOWN = 0;
	public static int FIRST_FINGER_UP = 1;
	public static int ON_MOVE = 2;
	public static int ACTION_CANCEL = 3;
	public static int SECOND_FINGER_DOWN = 5;
	public static int SECOND_FINGER_UP = 6;

	int getPointerCount();

	int getAction();

	Object getXREvent();
}
