package geogebra.touch.utils;


public class SensorUtils {
	
	private static Accelerometer acc = null;
	
	public static geogebra.touch.utils.Accelerometer getAccelerometer() {
		if (acc == null) {
			acc = new geogebra.touch.utils.Accelerometer();
		}
		return acc;
	}

}
