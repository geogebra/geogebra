package org.geogebra.desktop.geogebra3D.input3D;

import org.geogebra.common.euclidian3D.Input3D;
import org.geogebra.desktop.geogebra3D.input3D.intelRealSense.InputIntelRealsense3D;
import org.geogebra.desktop.geogebra3D.input3D.intelRealSense.Socket;

/**
 * Factory to create the proper 3D input
 * @author mathieu
 *
 */
public class Input3DFactory {

	static public String PREFS_REALSENSE = "realsense";
	static public String PREFS_NONE = "none";
	
	public enum Input3DExceptionType{INSTALL, RUN};
	
	static public class Input3DException extends Exception {

		private Input3DExceptionType type;

		public Input3DException(Input3DExceptionType type, String message) {
			super(message);
			this.type = type;
		}

		public Input3DExceptionType getType() {
			return type;
		}
	}

	/**
	 * create a 3D input
	 * 
	 * @param type
	 * @return 3D input
	 * @throws Input3DException
	 *             if fails
	 */
	static public Input3D createInput3D(String type) throws Input3DException {

		// check for realsense
		return new InputIntelRealsense3D(type.equals(PREFS_REALSENSE));

		// return new InputZSpace3D(); // use this for zspace
	}

	/**
	 * try to init realsense
	 * 
	 * @throws Exception
	 *             if none
	 */
	public static void initRealsense() throws Exception {
		Socket.createSession();
	}

}
