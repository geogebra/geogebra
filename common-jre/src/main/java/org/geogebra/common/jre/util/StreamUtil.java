package org.geogebra.common.jre.util;

import java.io.Closeable;
import java.io.IOException;

import org.geogebra.common.util.debug.Log;

/**
 * Static methods to work with streams
 */
public class StreamUtil {

	/**
	 * Closes stream without errors
	 * 
	 * @param c
	 *            stream or null
	 */
	public static void closeSilent(Closeable c) {
		try {
			if (c != null) {
				c.close();
			}
		} catch (IOException ex) {
			Log.error(ex.toString());
		}
	}

}
