package org.geogebra.desktop.geogebra3D.input3D;

import org.geogebra.common.euclidian3D.Input3D;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.geogebra3D.input3D.intelRealSense.InputIntelRealsense3D;

/**
 * Factory to create the proper 3D input
 * @author mathieu
 *
 */
public class Input3DFactory {

	/**
	 * create a 3D input
	 * @return 3D input
	 */
	static public Input3D createInput3D(){

		Input3D ret = null;

		// return null; // use this to switch off 3D input
		//return new InputLeo3D(); //use this for Leonar3do input

		long time = System.currentTimeMillis();

		// check for realsense
		try {
			ret = new InputIntelRealsense3D();
		} catch (Exception e) {
			// no realsense camera
			ret = null;
			Log.debug(e.getMessage());
		}

		App.debug("============ checking 3D input time: "
				+ (System.currentTimeMillis() - time) + " ms");

		return ret;

		// use this for intel realsense
		// input
		// return new InputZSpace3D(); // use this for zspace
	}
}
