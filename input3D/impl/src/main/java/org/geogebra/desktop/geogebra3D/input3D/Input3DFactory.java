package org.geogebra.desktop.geogebra3D.input3D;

import org.geogebra.common.euclidian3D.Input3DConstants;
import org.geogebra.common.geogebra3D.input3D.Input3D;
import org.geogebra.common.main.App;
import org.geogebra.desktop.geogebra3D.input3D.intelRealSense.InputIntelRealsense3D;
import org.geogebra.desktop.geogebra3D.input3D.zspace.InputZSpace3D;

/**
 * Factory to create the proper 3D input
 * @author mathieu
 *
 */
public class Input3DFactory {

	public enum Input3DExceptionType {
		INSTALL, INSTALL_RUNTIMES, RUN, ALREADY_USED, NOT_UP_TO_DATE, UNKNOWN
	}
	
	@SuppressWarnings("serial")
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
	 *            3D input type
	 * @return 3D input
	 * @throws Input3DException
	 *             if fails
	 */
	static public Input3D createInput3D(final App app, String type)
			throws Input3DException {

		if (type == null || type.length() == 0) {
			return null;
		}

		switch (type.charAt(0)) {
		case 'r':
			if (type.equals(Input3DConstants.PREFS_REALSENSE)) {
				// check for realsense
				return new InputIntelRealsense3D(app);
			}
			return null;
		case 'z':
			if (type.equals(Input3DConstants.PREFS_ZSPACE)) {
				// check for zSpace
				return new InputZSpace3D();
			}
			return null;
		}

		return null;

	}

	/**
	 * 
	 * @return input 3D instance for zspace
	 * @throws Input3DException
	 */
	static public Input3D createInputZSpace3D() throws Input3DException {
		return new InputZSpace3D(); // use this for zspace
	}

	/**
	 * try to init realsense
	 * 
	 * @throws Input3DException
	 *             if none
	 */
	public static void initRealsense() throws Input3DException {
		InputIntelRealsense3D.init();
	}

	/**
	 * try to detect zSpace
	 * 
	 * @throws Input3DException
	 *             if none
	 */
	public static void initZSpace() throws Input3DException {
		InputZSpace3D.initZSpace();
	}

}
